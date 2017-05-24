module TopicExplorer.Configuration.Log (info, warn) where

import Data.Time.Format (formatTime, defaultTimeLocale)
import Data.Time.LocalTime (getZonedTime)

import qualified System.IO as IO

import Control.Monad.HT ((<=<))


timePrefix :: String -> IO String
timePrefix msg = do
   now <- getZonedTime
   return $ formatTime defaultTimeLocale "%F %T" now ++ " " ++ msg

info :: String -> IO ()
info = putStrLn <=< timePrefix

warn :: String -> IO ()
warn = IO.hPutStrLn IO.stderr <=< timePrefix
