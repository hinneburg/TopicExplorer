module TopicExplorer.Creator.Corpus (
   Keywords(Keywords), Corpus.getKeywords,
   Identifier(Identifier), Corpus.getIdentifier,
   SearchId,
   Corpus(..),
   toJSONObject,
   ) where

import qualified TopicExplorer.Configuration.Corpus as Corpus
import TopicExplorer.Configuration.Corpus
         (Keywords(Keywords), Identifier(Identifier))

import Control.Applicative (pure, (<*>), (<$>))

import qualified Data.Aeson as Aeson
import qualified Data.Text as Text; import Data.Text (Text)
import qualified Data.Map as Map; import Data.Map (Map)
import Data.Aeson ((.:))
import Data.Int (Int32)


t :: String -> Text
t = Text.pack


type SearchId = Int32

data Corpus =
   Corpus {
      searchId :: SearchId,
      query :: Keywords,
      identifier :: Identifier
   }

instance Aeson.FromJSON Corpus where
   parseJSON =
      Aeson.withObject "Corpus" $ \v ->
      pure Corpus
      <*> v .: t"search_id"
      <*> (Keywords <$> v .: t"query")
      <*> (Identifier <$> v .: t"identifier")

instance Aeson.ToJSON Corpus where
   toJSON = Aeson.toJSON . toJSONObject

toJSONObject :: Corpus -> Map String Aeson.Value
toJSONObject (Corpus searchId_ (Keywords query_) (Identifier identifier_)) =
   Map.fromList $
   ("search_id", Aeson.toJSON searchId_) :
   ("query", Aeson.toJSON query_) :
   ("identifier", Aeson.toJSON identifier_) :
   []
