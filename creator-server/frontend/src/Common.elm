module Common exposing
        (simpleButton, multiLineText, optField, switchResult,
         httpGet, httpPostJson, JsonReq, formatHTTPError,
         symbolWithHelp, tapePlay, tapePause, tapeStop)

import Html exposing (Html, button, text, br)
import Html.Events exposing (onClick)
import Html.Attributes exposing (title)

import Http
import Json.Encode as Json
import Json.Decode as Decode


simpleButton : msg -> String -> Html msg
simpleButton cmd label = button [ onClick cmd ] [ text label ]

multiLineText : String -> List (Html msg)
multiLineText = List.intersperse (br [] []) << List.map text << String.lines


tapePlay : String
tapePlay = "\9654"

tapePause : String
tapePause = "\9616\8239\9616"
-- "\9208" - Media Control Pause - not available in my font

tapeStop : String
tapeStop = "\11035"
-- "\9632" - Black Square - too small
-- "\9608" - Full Block - too narrow
-- "\9209" - Media Control Stop - not available in my font

symbolWithHelp : String -> String -> Html a
symbolWithHelp tooltip symbol =
    Html.span [ title tooltip ] [ text symbol ]


switchResult : (e -> b) -> (a -> b) -> Result e a -> b
switchResult err ok res =
    case res of
        Ok a -> ok a
        Err errMsg -> err errMsg


optField : String -> Decode.Decoder a -> Decode.Decoder (Maybe a)
optField field decoder =
    Decode.oneOf
        [Decode.field field (Decode.map Just decoder), Decode.succeed Nothing]

httpGet : Decode.Decoder result -> String -> Cmd (Result Http.Error result)
httpGet decoder url = Http.send identity <| Http.get url decoder

type alias JsonReq = (String, Json.Value)

httpPostJson :
    Decode.Decoder result -> JsonReq -> Cmd (Result Http.Error result)
httpPostJson decoder (url, param) =
    Http.send identity <| Http.post url (Http.jsonBody param) decoder

formatHTTPError : Http.Error -> String
formatHTTPError err =
    case err of
        Http.BadStatus resp ->
            "HTTP error " ++ toString resp.status.code ++
            ": " ++ resp.status.message ++
            "\n" ++ resp.body
        _ -> toString err
