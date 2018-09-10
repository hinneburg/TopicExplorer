module TopicExplorer.Configuration.Utility where

import qualified Happstack.Server as Happs

import qualified Network.HTTP as HTTP
import qualified Network.URI as URI
import Network.URI (URI)

import Control.Monad.IO.Class (liftIO)
import Control.Monad (when)

import qualified Data.Aeson.Types as AesonType
import qualified Data.Aeson as Aeson
import qualified Data.Text as Text
import qualified Data.ByteString.Lazy.Char8 as BL
import qualified Data.List.HT as ListHT
import Data.Maybe (listToMaybe, fromMaybe)

import Text.Printf (printf)


parseRelativeOrAbsoluteURINoQuery :: String -> Either String URI
parseRelativeOrAbsoluteURINoQuery str =
   case URI.parseURIReference str of
      Nothing -> Left $ "not a valid URI: " ++ str
      Just uri -> do
         when (not $ null $ URI.uriQuery uri)
            (Left $ "URI contains a query: " ++ str)
         when (not $ null $ URI.uriFragment uri)
            (Left $ "URI contains a fragment: " ++ str)
         Right $
            if ListHT.takeRev 1 (URI.uriPath uri) == "/"
              then uri
              else uri {URI.uriPath = URI.uriPath uri ++ "/"}

parseURINoQuery :: String -> Either String URI
parseURINoQuery str =
   case URI.parseURI str of
      Nothing -> Left $ "not a valid URI: " ++ str
      Just uri -> do
         when (not $ null $ URI.uriQuery uri)
            (Left $ "URI contains a query: " ++ str)
         when (not $ null $ URI.uriFragment uri)
            (Left $ "URI contains a fragment: " ++ str)
         Right $
            if ListHT.takeRev 1 (URI.uriPath uri) == "/"
              then uri
              else uri {URI.uriPath = URI.uriPath uri ++ "/"}

makeURI :: String -> URI
makeURI = fromMaybe (error "cannot parse URI") . URI.parseURI


addQueryURI :: URI -> String -> URI
addQueryURI crawlServer command =
   crawlServer{URI.uriPath = URI.uriPath crawlServer ++ command}

getRequest :: URI -> String -> HTTP.Request BL.ByteString
getRequest crawlServer command =
   HTTP.mkRequest HTTP.GET $ addQueryURI crawlServer command

postRequest ::
   (Aeson.ToJSON params) =>
   URI -> String -> params -> HTTP.Request BL.ByteString
postRequest crawlServer command vars =
   let body = Aeson.encode vars
   in  HTTP.replaceHeader HTTP.HdrContentLength (show $ BL.length body) $
       HTTP.replaceHeader HTTP.HdrContentType mimetypeJSON $
       (HTTP.mkRequest HTTP.POST $ addQueryURI crawlServer command
            ::  HTTP.Request BL.ByteString)
         {HTTP.rqBody=body}


responseCode :: HTTP.Response a -> Int
responseCode response =
   case HTTP.rspCode response of
      (n2,n1,n0) -> n2*100 + n1*10 + n0



inverse :: (Bounded a, Enum a, Eq str) => (a -> str) -> str -> Maybe a
inverse fmt value =
   listToMaybe $ dropWhile ((value/=) . fmt) [minBound .. maxBound]


withEnum ::
   (Enum a, Bounded a) =>
   (a -> String) ->
   String ->
   (a -> Happs.ServerPart Happs.Response) ->
   Happs.ServerPart Happs.Response
withEnum fmt name act = do
   value <- Happs.look name
   case inverse fmt value of
      Nothing ->
         Happs.badRequest $
         Happs.toResponse (printf "unknown %s type: %s" name value :: String)
      Just x -> act x


parseEnum ::
   (Enum a, Bounded a) =>
   (a -> String) -> String -> Aeson.Value -> AesonType.Parser a
parseEnum fmt name =
   Aeson.withText name $ \txt ->
      case Text.unpack txt of
         str ->
            case inverse fmt str of
               Just x -> return x
               Nothing -> fail $ printf "unknown %s type: %s" name str


mimetypeJSON :: String
mimetypeJSON = "application/json; charset=utf-8"


withJsonBody ::
   (Aeson.FromJSON value) =>
   (value -> Happs.ServerPart Happs.Response) ->
   Happs.ServerPart Happs.Response
withJsonBody f = do
   body <- liftIO . Happs.takeRequestBody =<< Happs.askRq
   case body of
      Nothing -> Happs.badRequest $ Happs.toResponse "POST request has no body"
      Just rqbody ->
         case Aeson.eitherDecode $ Happs.unBody rqbody of
            Left str ->
               Happs.badRequest $ Happs.toResponse $
               "Parse error in POST request JSON body: " ++ str
            Right value -> f value
