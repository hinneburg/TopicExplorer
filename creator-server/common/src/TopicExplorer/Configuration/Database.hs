module TopicExplorer.Configuration.Database (
   escapeString,
   formatTime,
   dynamicStatement,
   runStatement,
   runQuery,
   runQueryList,
   runQueryExc,
   ) where

import TopicExplorer.Configuration.Exception (catchDoesNotExist)

import qualified System.Environment as Env
import System.Process (readProcess, readProcessWithExitCode)

import qualified Data.Time.Format as TimeFmt
import Data.Time.LocalTime (ZonedTime)

import qualified Control.Monad.Exception.Synchronous as ME
import Control.Monad (void)
import Control.Applicative ((<$>))

import qualified Data.List.HT as ListHT


{- |
See <https://dev.mysql.com/doc/refman/5.7/en/string-literals.html>.

'%' and '_' are not escaped.
This would be necessary for pattern matching functions.
-}
specialChar :: Char -> Maybe Char
specialChar c =
   case c of
      '\0'  -> Just '0'
      '\''  -> Just '\''
      '"'   -> Just '"'
      '\b'  -> Just 'b'
      '\n'  -> Just 'n'
      '\r'  -> Just 'r'
      '\t'  -> Just 't'
      '\26' -> Just 'Z'
      '\\'  -> Just '\\'
      _ -> Nothing

escapeChar :: Char -> String
escapeChar c = maybe [c] (('\\':) . (:[])) $ specialChar c

escapeString :: String -> String
escapeString = concatMap escapeChar


formatTime :: ZonedTime -> String
formatTime = TimeFmt.formatTime TimeFmt.defaultTimeLocale "%F %T"


dynamicStatement :: String -> [String]
dynamicStatement build =
   ("SELECT " ++ build ++ " INTO @stmt;") :
   "PREPARE prep FROM @stmt;" :
   "EXECUTE prep;" :
   "DEALLOCATE PREPARE prep;" :
   []


runStatement :: String -> IO ()
runStatement sql = void $ runQuery sql

{-
alternative to catchDoesNotExist on getEnv: lookupEnv
available since base-4.6 of GHC-7.6
-}
getMySQLArgs :: IO [String]
getMySQLArgs = do
   database <- Env.getEnv "TE_MANAGEMENT_DB_NAME"
   loginArgs <-
      catchDoesNotExist
         ((\file -> ["--defaults-extra-file=" ++ file]) <$>
            Env.getEnv "MYSQL_TE_MANAGEMENT_LOGIN_FILE")
         (return [])
   return $ loginArgs ++ database : []

runQuery :: String -> IO String
runQuery sql = do
   args <- getMySQLArgs
   readProcess "mysql" args sql

runQueryList :: String -> IO [[String]]
runQueryList sql =
   map (ListHT.chop ('\t'==)) . lines <$> runQuery sql

runQueryExc :: String -> IO (ME.Exceptional String String)
runQueryExc sql = do
   args <- getMySQLArgs
   (code, res, err) <- readProcessWithExitCode "mysql" args sql
   return $ fmap (const res) $ ME.mapException (const err) $
      ME.fromExitCode code
