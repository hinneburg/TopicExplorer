module TopicExplorer.Configuration.Date where

import TopicExplorer.Configuration.Parser (betweenNum)

import qualified Text.ParserCombinators.Parsec as Parsec

import Control.Monad (replicateM, void)
import Control.Applicative ((<$>))
import Control.DeepSeq (NFData, rnf)

import Text.Printf (printf)


data Date = Date {year, month, day, hour, minute :: Int}
   deriving (Show, Eq, Ord)

instance NFData Date where
   rnf (Date year_ month_ day_ hour_ minute_) =
      rnf (year_, month_, day_, hour_, minute_)


{-
We could also use formatter and parser of the 'time' library.
I am however uncertain how much interpretation of time zones it does
and what time data type of that library is best for our purposes.
-}
format :: Date -> String
format d =
   printf "%04d-%02d-%02d %02d:%02d"
      (year d) (month d) (day d) (hour d) (minute d)


parser :: Parsec.Parser Date
parser = do
   (year_, month_, day_) <- dateParser
   Parsec.skipMany1 Parsec.space
   (hour_, minute_) <- timeParser
   return $ Date year_ month_ day_ hour_ minute_

{- |
This parser allows to omit the time
and fills the time values with values from the default record.
-}
partialParser :: Date -> Parsec.Parser Date
partialParser defaultDate = do
   (year_, month_, day_) <- dateParser
   (hour_, minute_) <-
      Parsec.option (hour defaultDate, minute defaultDate) $ do
         Parsec.skipMany1 Parsec.space
         timeParser
   return $ Date year_ month_ day_ hour_ minute_

dateParser :: Parsec.Parser (Int,Int,Int)
dateParser = do
   year_ <- read <$> replicateM 4 Parsec.digit
   void $ Parsec.char '-'
   month_ <- read <$> betweenNum 1 2 Parsec.digit
   void $ Parsec.char '-'
   day_ <- read <$> betweenNum 1 2 Parsec.digit
   return (year_, month_, day_)

timeParser :: Parsec.Parser (Int,Int)
timeParser = do
   hour_ <- read <$> betweenNum 1 2 Parsec.digit
   void $ Parsec.char ':'
   minute_ <- read <$> replicateM 2 Parsec.digit
   return (hour_, minute_)





data Range = Range Date Date

instance NFData Range where rnf (Range from to) = rnf (from, to)


cellsFromRange :: Range -> [String]
cellsFromRange (Range from to) = [format from, format to]

joinRanges :: Range -> Range -> Range
joinRanges (Range from _) (Range _ to) = Range from to

rangeUnion :: Range -> Range -> Range
rangeUnion (Range from0 to0) (Range from1 to1) =
   Range (min from0 from1) (max to0 to1)
