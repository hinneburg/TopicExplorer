module TopicExplorer.Configuration.Exception (
   catchDoesNotExist,
   maybeDoesNotExist,
   foreverCatchAll,
   catchAll,
   ) where

import qualified TopicExplorer.Configuration.Log as Log

import qualified System.IO.Error as IOError

import qualified Control.Exception as Exc
import Control.Monad (forever)


catchDoesNotExist :: IO a -> IO a -> IO a
catchDoesNotExist act handler =
   IOError.catchIOError act
      (\e -> if IOError.isDoesNotExistError e then handler else ioError e)

maybeDoesNotExist :: IO a -> IO (Maybe a)
maybeDoesNotExist act =
   catchDoesNotExist (fmap Just act) (return Nothing)


foreverCatchAll :: IO () -> IO ()
foreverCatchAll loopBody =
   forever $ catchAll "forever" loopBody

catchAll :: String -> IO () -> IO ()
catchAll context act =
   Exc.catch act $ \e ->
      Log.warn $ context ++ ": " ++ show (e :: Exc.SomeException)
