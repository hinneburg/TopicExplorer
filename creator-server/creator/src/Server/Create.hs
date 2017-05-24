module Server.Create where

import qualified Query
import Query (Query)

import qualified TopicExplorer.Configuration.Database as SQL
import qualified TopicExplorer.Configuration.Date as Date
import qualified TopicExplorer.Configuration.Log as Log
import TopicExplorer.Creator.Corpus (SearchId)
import TopicExplorer.Configuration.Date (Date)
import TopicExplorer.Configuration.Exception (foreverCatchAll)

import qualified Control.Concurrent.Split.Chan as Chan
import Control.Concurrent (threadDelay)
import Control.Exception (onException)
import Control.Monad (when)

import Data.Time.LocalTime (getZonedTime)

import qualified System.Environment as Env
import qualified System.Path.PartClass as PartClass
import qualified System.Path.IO as PathIO
import qualified System.Path as Path
import System.Path ((</>))
import System.Exit (ExitCode(ExitSuccess, ExitFailure))
import System.Process (rawSystem)

import qualified Data.List.HT as ListHT
import Data.Maybe (isNothing, mapMaybe)

import Text.Printf (printf)


data Parameters = Parameters Identifier SearchId String (Maybe Query) DateRange
data DateRange = DateRange (Maybe Date) (Maybe Date)
type Identifier = String

dateStatement ::
   SearchId -> String -> String -> String -> Maybe Date -> [String]
dateStatement searchId agg op dateName mdate =
   "select TABLE_NAME from CRAWL" :
   printf "where SEARCH_STRING_ID = %d limit 1 into @CorpusTable;" searchId :
   SQL.dynamicStatement
      (printf
         "concat ('select %s(DOCUMENT_DATE) from ', @CorpusTable, ' limit 1 into @Date;')"
         agg) ++
   (printf "set @%s=%s;" dateName $
      case mdate of
         Nothing -> "@Date"
         Just date -> printf "%s(@Date,'%s')" op (Date.format date)) :
   "" :
   []

addPendingJob :: Parameters -> IO ()
addPendingJob
   (Parameters identifier searchId filterExpr filterQuery
      (DateRange filterFrom filterTo)) = do
   now <- getZonedTime
   SQL.runStatement $ unlines $
      dateStatement searchId "min" "greatest" "FromDate" filterFrom ++
      dateStatement searchId "max" "least" "ToDate" filterTo ++
      "insert into TOPIC_EXPLORER_DEFINITION (TE_IDENTIFIER, SEARCH_STRING_ID, INITIALIZE_PENDING, FILTER_TEXT_QUERY, FILTER_TEXT_SQL, FILTER_START_DATETIME, FILTER_END_DATETIME)" :
      printf "values ('%s', %d, '%s', '%s', '%s', @FromDate, @ToDate);"
         identifier searchId (SQL.formatTime now)
         (SQL.escapeString filterExpr)
         (SQL.escapeString $ Query.formatMaybe filterQuery) :
      []

setTEDTimeToCurrent :: String -> String -> IO ()
setTEDTimeToCurrent timeName identifier = do
   now <- getZonedTime
   SQL.runStatement $ unlines $
      "update TOPIC_EXPLORER_DEFINITION" :
      printf "set %s = '%s'" timeName (SQL.formatTime now) :
      printf "where TE_IDENTIFIER='%s';" identifier :
      []

initializeExplorer ::
   (PartClass.AbsRel ar) => Path.Dir ar -> Parameters -> IO ExitCode
initializeExplorer scriptDir (Parameters identifier searchId _ _ _) = do
   setTEDTimeToCurrent "INITIALIZE_RUNNING" identifier
   exitCode <-
      rawSystem
         (Path.toString $ scriptDir </> Path.relFile "initialize_te.sh")
         [identifier, show searchId]
   setTEDTimeToCurrent "INITIALIZE_FINISHED" identifier
   return exitCode


databaseLocalProperties :: FilePath
databaseLocalProperties = "database.local.properties"

findDBName :: String -> IO String
findDBName config =
   case mapMaybe (ListHT.maybePrefixOf "DB=") $ lines config of
      ident:_ -> return ident
      [] -> ioError $ userError $ "missing DB definition in " ++ databaseLocalProperties

simulateComputeTEScript :: (PartClass.AbsRel ar) => Path.Dir ar -> IO String
simulateComputeTEScript _scriptDir = do
   now <- getZonedTime
   result <-
      SQL.runQuery $ unlines $
         "select TOPIC_EXPLORER_ID into @teid" :
         "from TOPIC_EXPLORER where RUNNING is null order by PENDING limit 1;" :
         "" :
         "update TOPIC_EXPLORER set" :
         printf "RUNNING = '%s'," (SQL.formatTime now) :
         printf "FINISHED = '%s'" (SQL.formatTime now) :
         "where TOPIC_EXPLORER_ID = @teid;" :
         "" :
         "select TE_IDENTIFIER" :
         "from TOPIC_EXPLORER where TOPIC_EXPLORER_ID = @teid;" :
         []
   case lines result of
      [_, name] -> return name
      _ -> ioError $ userError "incomprehensible database answer for next topic explorer to be computed"

runComputeTEScript :: (PartClass.AbsRel ar) => Path.Dir ar -> IO String
runComputeTEScript scriptDir = do
   exitCode <-
      rawSystem (Path.toString $ scriptDir </> Path.relFile "compute_te.sh") []
   case exitCode of
      ExitSuccess -> return ()
      ExitFailure n ->
         ioError $ userError $ "compute_te.sh stopped with exit code " ++ show n
   baseDir <-
      either (ioError . userError) (return . Path.idAbsRel) . Path.parse =<<
      Env.getEnv "TE_BASE_DIR"
   let dir = Path.relDir
   findDBName =<<
      PathIO.readFile
         (baseDir </> dir "helper" </> dir "tmp" </>
          dir "resources" </> Path.relFile databaseLocalProperties)

computeExplorer :: (PartClass.AbsRel ar) => Path.Dir ar -> IO ()
computeExplorer scriptDir = do
   Log.info "start compute_te.sh"
   ident <- runComputeTEScript scriptDir
   Log.info $ "finished computation of topic explorer " ++ ident
   SQL.runStatement $ unlines $
      "select TOPIC_EXPLORER_ID, PENDING, RUNNING, FINISHED" :
      "from TOPIC_EXPLORER order by FINISHED desc limit 1" :
      "into @teid, @pending, @running, @finished;" :
      "" :
      "update TOPIC_EXPLORER_DEFINITION set" :
      "TE_COMPUTATION_PENDING = @pending," :
      "TE_COMPUTATION_RUNNING = @running," :
      "TE_COMPUTATION_FINISHED = @finished," :
      "TOPIC_EXPLORER_ID = @teid" :
      printf "where TE_IDENTIFIER='%s';" ident :
      []


data Task = Initialize Parameters | Compute

thread :: (PartClass.AbsRel ar) =>
   Path.Dir ar -> Chan.Out Task -> Chan.In Bool -> IO ()
thread scriptDir chan finishChan =
   foreverCatchAll $ do
      task <- Chan.read chan
      case task of
         Initialize params ->
            Log.info . printf "initialize_te terminated with %s" . show
               =<< initializeExplorer scriptDir params
         Compute -> do
            onException
               (computeExplorer scriptDir)
               (Chan.write finishChan False)
            Chan.write finishChan True


checkFinishedNLP :: IO (Maybe String)
checkFinishedNLP = do
   result <-
      SQL.runQuery $
         "select TOPIC_EXPLORER_ID from TOPIC_EXPLORER " ++
         "where RUNNING is null order by PENDING limit 1;"
   case lines result of
      [] -> return Nothing
      [_] -> return Nothing
      [_, identifier] -> do
         Log.info $ "found finished NLP configuration for " ++ identifier
         return $ Just identifier
      _ -> ioError $ userError "incomprehensible database answer for next topic explorer to be computed"

waitNLPThread :: Chan.In Task -> Chan.Out Bool -> IO ()
waitNLPThread taskChan finishChan =
   foreverCatchAll $ do
      let loop = do
            m <- checkFinishedNLP
            when (isNothing m) $ threadDelay (10*1000*1000) >> loop
       in loop
      Chan.write taskChan Compute
      success <- Chan.read finishChan
      when (not success) $ threadDelay (10*1000*1000)
