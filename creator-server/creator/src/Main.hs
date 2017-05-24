module Main (main) where

import qualified Server.Create as Create
import qualified Query.Parser as QueryParser

import qualified Paths_topic_creator as Paths

import qualified TopicExplorer.Creator.Corpus as Corpus
import qualified TopicExplorer.Configuration.Date as Date
import qualified TopicExplorer.Configuration.Database as SQL
import qualified TopicExplorer.Configuration.Log as Log
import TopicExplorer.Creator.Communication
         (CreateParams(CreateParams), DateRange(DateRange), Date)
import TopicExplorer.Creator.Corpus (Corpus(Corpus))
import TopicExplorer.Configuration.Utility (
         withJsonBody, mimetypeJSON,
         parseURINoQuery, makeURI,
         )
import TopicExplorer.Configuration.Corpus
         (Keywords(Keywords), Identifier(Identifier))

import qualified Happstack.Server as Happs
import Network.URI (URI)

import qualified Control.Concurrent.Split.Chan as Chan
import Control.Concurrent (forkIO)

import qualified System.Path.PartClass as PathClass
import qualified System.Path as Path
import qualified System.IO as IO
import System.Path ((</>))
import System.Process (readProcessWithExitCode)

import qualified Options.Applicative as OP

import qualified Text.ParserCombinators.Parsec as Parsec

import qualified Control.Monad.Exception.Synchronous as ME
import qualified Control.Monad.Trans.Class as MT
import Control.Monad.IO.Class (liftIO)
import Control.Monad (msum, void, when)
import Control.Applicative (liftA2, liftA3, (<*>), (<*))

import qualified Data.Aeson as Aeson
import qualified Data.ByteString.Char8 as B
import qualified Data.TotalMap as TotalMap
import qualified Data.Map as Map
import qualified Data.NonEmpty.Mixed as NonEmptyM
import qualified Data.NonEmpty as NonEmpty
import qualified Data.List.HT as ListHT
import qualified Data.Monoid.HT as Mn
import qualified Data.Char as Char
import Data.Traversable (forM)
import Data.Map (Map)
import Data.Maybe (fromMaybe)
import Data.Monoid ((<>))

import Text.Printf (printf)


ctJSON :: B.ByteString
ctJSON = B.pack mimetypeJSON

responseJson :: Aeson.Value -> Happs.ServerPart Happs.Response
responseJson = Happs.ok . Happs.toResponseBS ctJSON . Aeson.encode


site ::
   (PathClass.AbsRel ar) =>
   URI -> Path.Dir ar -> Chan.In Create.Task ->
   Happs.ServerPart Happs.Response
site appServer scriptDir createChan = do
   Happs.decodeBody $ Happs.defaultBodyPolicy "/tmp" 4096 4096 4096
   msum $
      (Happs.method Happs.GET >> methodGet appServer) :
      (Happs.method Happs.POST >> methodPost appServer scriptDir createChan) :
      []


parseSearchId :: String -> ME.Exceptional String Corpus.SearchId
parseSearchId str =
   case reads str of
      [(searchId, "")] -> return searchId
      _ -> ME.throw "invalid search id"

corpusFromRow :: [String] -> ME.Exceptional String (Corpus, String)
corpusFromRow [searchIdStr, query, identifier, corpusTable] = do
   searchId <- parseSearchId searchIdStr
   return
      (Corpus searchId (Keywords query) (Identifier identifier), corpusTable)
corpusFromRow _ = ME.throw "invalid number of cells"

data DateTriple = DateTriple Date Date Date
data Job = Job String String DateRange DateTriple DateTriple

instance Aeson.ToJSON DateTriple where
   toJSON (DateTriple pending running finished) =
      Aeson.toJSON $
      Map.filter ("NULL"/=) $
      Map.fromList $
         ("pending", pending) :
         ("running", running) :
         ("finished", finished) :
         []

jobFromRow :: [String] -> ME.Exceptional String (Corpus.SearchId, Job)
jobFromRow
   [searchIdStr, identifier, query, from, to,
      initPending, initRunning, initFinished,
      compPending, compRunning, compFinished] = do
   searchId <- parseSearchId searchIdStr
   return
      (searchId,
       Job identifier query
         (DateRange from to)
         (DateTriple initPending initRunning initFinished)
         (DateTriple compPending compRunning compFinished))
jobFromRow _ = ME.throw "invalid number of cells"

stripCorpusPrefix :: Corpus -> String -> String
stripCorpusPrefix corpus identifier =
   fromMaybe ("(" ++ identifier ++ ")") $
   ListHT.maybePrefixOf
      (Corpus.getIdentifier (Corpus.identifier corpus) ++ "_") identifier

isFinished :: DateTriple -> Bool
isFinished (DateTriple _ _ finished)  =  finished /= "NULL"

jsonFromJob :: URI -> Corpus -> Job -> Aeson.Value
jsonFromJob appServer corpus
      (Job identifier query range initializeTimes computeTimes) =

   Aeson.toJSON $
   Map.unions $
      (Map.fromList $
         ("identifier", Aeson.toJSON $ stripCorpusPrefix corpus identifier) :
         ("filter_query", Aeson.toJSON query) :
         ("filter_date", Aeson.toJSON range) :
         ("initialize_times", Aeson.toJSON initializeTimes) :
         ("compute_times", Aeson.toJSON computeTimes) :
         []) :
      (Mn.when (isFinished initializeTimes) $
       Map.singleton "url_nlp" $
         Aeson.toJSON $ show appServer ++ identifier ++ "_nlp") :
      (Mn.when (isFinished computeTimes) $
       Map.singleton "url_te" $
         Aeson.toJSON $ show appServer ++ identifier ++ "_te") :
      []


jsonFromJobs :: URI -> Corpus -> [Job] -> Aeson.Value
jsonFromJobs appServer corpus =
   Aeson.toJSON . map (jsonFromJob appServer corpus)

joinGroups :: [(Corpus, a)] -> [(Corpus.SearchId, b)] -> [((Corpus, a), [b])]
joinGroups corpora explorers =
   Map.elems $
   TotalMap.intersectionPartialWith (flip (,))
      (TotalMap.fromPartial [] $ fmap NonEmpty.flatten $
       Map.fromList $ NonEmptyM.groupPairs explorers)
      (Map.fromList $ decorate (Corpus.searchId . fst) corpora)

decorate :: (a -> k) -> [a] -> [(k, a)]
decorate f = map (\x -> (f x, x))

assertSuccess :: String -> ME.Exceptional String a -> IO a
assertSuccess msg = ME.switch (ioError . userError . printf "%s: %s" msg) return

sqlArticleDateFormat :: String
sqlArticleDateFormat = "%Y-%m-%d %H:%i"

queryCorpora :: URI -> IO [Map String Aeson.Value]
queryCorpora appServer = do
   corpora <-
      assertSuccess "database row for corpus" .
      mapM corpusFromRow . drop 1
      =<<
      (SQL.runQueryList $
         "select SEARCH_STRING_ID, SEARCH_STRING, DESCRIPTIVE_IDENTIFIER, TABLE_NAME\n" ++
         "from SEARCH_STRING join CRAWL using (SEARCH_STRING_ID)\n" ++
         "order by SEARCH_STRING_ID;\n")
   jobs <-
      return . joinGroups corpora
      =<<
      assertSuccess "database row for job" .
      mapM jobFromRow . drop 1
      =<<
      (SQL.runQueryList $ unlines $
         "select" :
         "  TED.SEARCH_STRING_ID, TED.TE_IDENTIFIER, TED.FILTER_TEXT_QUERY," :
         printf "  date_format(TED.FILTER_START_DATETIME,'%s')," sqlArticleDateFormat :
         printf "  date_format(TED.FILTER_END_DATETIME,'%s')," sqlArticleDateFormat :
         "  TED.INITIALIZE_PENDING, TED.INITIALIZE_RUNNING, TED.INITIALIZE_FINISHED," :
         "  TE.PENDING, TE.RUNNING, TE.FINISHED" :
         "from TOPIC_EXPLORER_DEFINITION TED left join TOPIC_EXPLORER TE" :
         "  using (TOPIC_EXPLORER_ID)" :
         "order by TED.SEARCH_STRING_ID, TED.INITIALIZE_PENDING;" :
         [])
   forM jobs $ \((corpus, corpusTable), associatedJobs) -> do
      info <-
         SQL.runQueryList $
         printf
            "select date_format(min(DOCUMENT_DATE),'%s'), date_format(max(DOCUMENT_DATE),'%s'), count(DOCUMENT_ID) from %s;"
            sqlArticleDateFormat sqlArticleDateFormat corpusTable
      ME.switch (ioError . userError) return $ do
         (from, to, countStr) <-
            case info of
               [_, [from, to, countStr]] -> return (from, to, countStr)
               _ -> ME.throw "expected three cells for corpus info"
         count <-
            case reads countStr of
               [(count, "")] -> return count
               _ ->
                  ME.throw $
                  printf "article count <%s> is not a number" countStr
         let realDate d = d/="NULL"
         range <-
            case (realDate from, realDate to) of
               (True, True) ->
                  return $
                  Map.singleton "date_range" $ Aeson.toJSON $ DateRange from to
               (False, False) -> return Map.empty
               _ -> ME.throw "one range boundary is present and the other one is missing"

         return $
            Corpus.toJSONObject corpus <>
            range <>
            (Map.fromList $
               [("count", Aeson.toJSON (count :: Integer)),
                ("jobs", jsonFromJobs appServer corpus associatedJobs)])


methodGet :: URI -> Happs.ServerPart Happs.Response
methodGet appServer =
   msum $
      (Happs.dir "list" $ do
         responseJson . Aeson.toJSON =<< liftIO (queryCorpora appServer)) :
      []


{-
Defines ServerPart as type constructor synonym instead of a plain type synonym.
Should be defined this way in Happstack.
-}
type ServerPart = Happs.ServerPartT IO

startCreate ::
   (PathClass.AbsRel ar) =>
   URI -> Path.Dir ar -> Chan.In Create.Task -> CreateParams ->
   ME.ExceptionalT String ServerPart Happs.Response
startCreate appServer scriptDir createChan
   (CreateParams searchId identifierMixedCase filterQuery
      (DateRange filterFrom filterTo)) = do
   let identifier = map Char.toUpper identifierMixedCase
   query <-
      ME.mapExceptionT show $ ME.fromEitherT $ return $
      Parsec.parse QueryParser.complete "filter query" filterQuery
   let parseDate name deflt =
         ME.mapExceptionT show . ME.fromEitherT . return .
         Parsec.parse
            (Parsec.optionMaybe (Date.partialParser deflt) <* Parsec.eof) name
   fromDate <-
      parseDate "filter date range beginning" (Date.Date 1 1 1 0 0) filterFrom
   toDate <-
      parseDate "filter date range end" (Date.Date 9999 12 31 23 59) filterTo
   ME.assertT "filter date range is empty" $
      fromMaybe True $ liftA2 (<) fromDate toDate
   let params =
         Create.Parameters identifier searchId filterQuery query $
            Create.DateRange fromDate toDate
   ME.ExceptionalT $ liftIO $ do
      (exitCode,msg,err) <-
         readProcessWithExitCode
            (Path.toString $ scriptDir </> Path.relFile "exists_te.sh")
            [identifier] ""
      Log.info msg
      when (not $ null err) $ Log.info err
      return $
         ME.mapException
            (printf "%s (exists_te.sh terminated with code %d)" msg) $
         ME.fromExitCode exitCode

   MT.lift $ liftIO $ do
      Create.addPendingJob params
      Chan.write createChan $ Create.Initialize params
   MT.lift $ responseJson . Aeson.toJSON =<< liftIO (queryCorpora appServer)

{-
curl -X POST --header "Content-Type: application/json" --data-binary '["foo","bar"]' http://localhost:7200/create-set
curl -X POST --header "Content-Type: application/json" --data-binary '{"filterDate":{"to":"2017-03-14 23:40:00","from":"2017-03-14 21:55:00"},"search_id":0,"identifier":"SENSO","filterQuery":"戦争"}' http://localhost:7200/create
-}
methodPost ::
   (PathClass.AbsRel ar) =>
   URI -> Path.Dir ar -> Chan.In Create.Task ->
   Happs.ServerPart Happs.Response
methodPost appServer scriptDir createChan =
   msum $
      (Happs.dir "create" $
         withJsonBody $ \params ->
         ME.switchT (Happs.badRequest . Happs.toResponse) return $
         startCreate appServer scriptDir createChan params) :
      []


optParser :: Path.AbsRelDir -> OP.Parser (Int, Path.AbsRelDir, URI)
optParser defltScriptDir =
   liftA3 (,,)
      (OP.option OP.auto $
         OP.long "port" <>
         OP.metavar "PORT" <>
         OP.value 7200 <>
         OP.help "Port for HTTP server")
      (OP.option (OP.eitherReader Path.parse) $
         OP.long "script-dir" <>
         OP.metavar "DIR" <>
         OP.value defltScriptDir <>
         OP.help "Directory for Topic-Explorer shell scripts")
      (OP.option (OP.eitherReader parseURINoQuery) $
         OP.long "app-server" <>
         OP.metavar "URL" <>
         OP.value (makeURI "http://localhost:8080/") <>
         OP.help "URL of application server")


optionInfo :: OP.Parser a -> OP.ParserInfo a
optionInfo parser =
   OP.info
      (OP.helper <*> parser)
      (OP.fullDesc <> OP.progDesc "Web-server for Topic-Explorer creation")


main :: IO ()
main = do
   IO.hSetBuffering IO.stdout IO.LineBuffering
   IO.hSetBuffering IO.stderr IO.LineBuffering
   dataDir <-
      either (ioError . userError) return . Path.parse =<< Paths.getDataDir
   (port, scriptDir, appServer) <-
      OP.execParser $ optionInfo $ optParser (dataDir </> Path.relDir "script")
   (createIn, createOut) <- Chan.new
   (finishIn, finishOut) <- Chan.new
   void $ forkIO $ Create.thread scriptDir createOut finishIn
   void $ forkIO $ Create.waitNLPThread createIn finishOut
   Happs.simpleHTTP (Happs.nullConf{Happs.port=port}) $
      site appServer scriptDir createIn
