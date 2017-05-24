module TopicExplorer.Creator.Communication where

import TopicExplorer.Creator.Corpus (SearchId)

import Control.Applicative (pure, (<*>), (<$>))

import qualified Data.Aeson as Aeson
import qualified Data.Text as Text
import Data.Aeson ((.:), (.=))
import Data.Text (Text)



data CreateParams = CreateParams SearchId String String DateRange

searchIdId :: Text
searchIdId = Text.pack "search_id"

identifierId :: Text
identifierId = Text.pack "identifier"

filterQueryId :: Text
filterQueryId = Text.pack "filterQuery"

filterDateId :: Text
filterDateId = Text.pack "filterDate"

instance Aeson.FromJSON CreateParams where
   parseJSON =
      Aeson.withObject "CreateParams" $ \v ->
      pure CreateParams
         <*> v .: searchIdId <*> v .: identifierId
         <*> v .: filterQueryId <*> v .: filterDateId

instance Aeson.ToJSON CreateParams where
   toJSON (CreateParams searchId identifier filterQuery filterDate) =
      Aeson.object $
         (searchIdId .= searchId) :
         (identifierId .= identifier) :
         (filterQueryId .= filterQuery) :
         (filterDateId .= filterDate) :
         []


data DateRange = DateRange Date Date
type Date = String

fromId :: Text
fromId = Text.pack "from"

toId :: Text
toId = Text.pack "to"

instance Aeson.FromJSON DateRange where
   parseJSON =
      Aeson.withObject "DateRange" $ \v ->
         DateRange <$> v .: fromId <*> v .: toId

instance Aeson.ToJSON DateRange where
   toJSON (DateRange from to) =
      Aeson.object $
         (fromId .= from) :
         (toId .= to) :
         []
