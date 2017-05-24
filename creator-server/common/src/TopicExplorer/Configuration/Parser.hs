module TopicExplorer.Configuration.Parser where

import Text.ParserCombinators.Parsec (Parser)

import Control.Monad (replicateM)
import Control.Applicative (liftA2, (<|>))


atMost :: Int -> Parser a -> Parser [a]
atMost n0 p =
   let go 0 = return []
       go n = liftA2 (:) p (go (n-1)) <|> return []
   in  go n0

betweenNum :: Int -> Int -> Parser a -> Parser [a]
betweenNum minN maxN p = liftA2 (++) (replicateM minN p) (atMost (maxN-minN) p)
