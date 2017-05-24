module TopicExplorer.Configuration.Corpus where

import Control.DeepSeq (NFData, rnf)


newtype Keywords = Keywords {getKeywords :: String}
   deriving (Eq, Ord)

newtype Identifier = Identifier {getIdentifier :: String}
   deriving (Eq, Ord)

instance NFData Keywords where rnf = rnf . getKeywords
instance NFData Identifier where rnf = rnf . getIdentifier
