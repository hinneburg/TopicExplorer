module Main exposing (main)

import DateRange exposing (DateRange(DateRange), Date)
import Date
import Common exposing
        (simpleButton, multiLineText, optField, switchResult,
         httpGet, httpPostJson, JsonReq, formatHTTPError,
         symbolWithHelp, tapePlay, tapePause, tapeStop)

import Html
import Html exposing (Html, div, button, input, text, table, tr, th, td, br)
import Html.Attributes exposing
        (placeholder, property, value, rowspan, colspan, href, target)
import Html.Events exposing (onInput, onClick)
import Http
import Json.Encode as Json
import Json.Decode as Decode
import Dict exposing (Dict)


-- MODEL
type alias Model =
    { errorMsg : String, corpora : List Corpus, inputs : Dict Identifier Input }

type alias Input =
    { explorerIdent : String, filterQuery : String, filterDate : DateRange }

emptyInput : Input
emptyInput =
    { explorerIdent = "", filterQuery = "", filterDate = DateRange "" "" }


init : ( Model, Cmd Msg )
init =
    ( {errorMsg = "", corpora = [], inputs = Dict.empty},
      listCmd listURL )

-- MESSAGES
type Msg
        = GetList
        | GetListSuccess (List Corpus)
        | StartCreator (Maybe DateRange) SearchId Identifier
        | ClearInput Identifier
        | SetExplorerIdent Identifier String
        | SetFilterQuery Identifier String
        | SetFilterDateFrom Identifier String
        | SetFilterDateTo Identifier String
        | ShowError Http.Error

type alias SearchId = Int
type alias Identifier = String

-- VIEW
view : Model -> Html Msg
view model =
    div [] <|
        [ table
            [ property "border" (Json.string "on") ]
            ( tr [] [
                th [] [ text "Identifier" ],
                th [] [ text "Query" ],
                th [] [ text "Date Range" ],
                th [] [ text "No" ],
                th [] [ text "Model" ],
                th [] [ text "Filter Query" ],
                th [colspan 2] [ text "Filter Date Range" ] ,
                th [colspan 2] [ text "NLP" ] ,
                th [colspan 2] [ text "TE" ]
                ]
              ::
              List.concatMap (tableRowsFromCorpus model) model.corpora
              ++
              [] )

        , simpleButton GetList "Update overview"
        , br [] []
        ]
        ++
        multiLineText model.errorMsg


type Corpus = Corpus SearchId Identifier String (Maybe DateRange) Int (List Job)

type Job =
    Job Identifier String DateRange
        (DateTriple Date) (Maybe String)
        (DateTriple (Maybe Date)) (Maybe String)

type DateTriple pending = DateTriple pending (Maybe Date) (Maybe Date)

type alias URI = String

tableRowsFromCorpus : Model -> Corpus -> List (Html Msg)
tableRowsFromCorpus
    model (Corpus searchId identifier query dateRange count jobs) =

    let corpusAttrs = [rowspan (List.length jobs + 1)]
        corpusColumns =
            td corpusAttrs [ text identifier ] ::
            td corpusAttrs [ text query ] ::
            td corpusAttrs [ text (maybe "" DateRange.format dateRange) ] ::
            td corpusAttrs [ text (toString count) ] ::
            []

        jobCells
            (Job identifier query (DateRange from to)
                initializeTimes urlNLP computeTimes urlTE) =
            td [] [ text identifier ] ::
            td [] [ text query ] ::
            td [] [ text from ] ::
            td [] [ text to ] ::
            td [] ( htmlFromInitTriple initializeTimes ) ::
            td [] ( maybe [] (List.singleton << appAnchor "NLP") urlNLP ) ::
            td [] ( htmlFromCompTriple computeTimes ) ::
            td [] ( maybe [] (List.singleton << appAnchor "TE") urlTE ) ::
            []

        inputs = Dict.get identifier model.inputs
        valueFromInputs select = value <| maybe "" select inputs
        inputCells =
            td [] [
                input [
                    placeholder "Model identifier",
                    valueFromInputs (.explorerIdent),
                    onInput (SetExplorerIdent identifier) ] [] ] ::
            td [] [
                input [
                    placeholder "Filter Query",
                    valueFromInputs (.filterQuery),
                    onInput (SetFilterQuery identifier) ] [] ] ::
            td [] [
                input [
                    placeholder "Date range start",
                    valueFromInputs (DateRange.from << (.filterDate)),
                    onInput (SetFilterDateFrom identifier) ] [] ] ::
            td [] [
                input [
                    placeholder "Date range stop",
                    valueFromInputs (DateRange.to << (.filterDate)),
                    onInput (SetFilterDateTo identifier) ] [] ] ::
            td [] [ simpleButton
                        (StartCreator dateRange searchId identifier)
                        "Create" ] ::
            []

    in  List.map (tr []) <|
        List.map2 (++)
            (corpusColumns :: List.map (\_ -> []) jobs)
            (List.map jobCells jobs ++ [inputCells])

htmlFromInitTriple : DateTriple Date -> List (Html msg)
htmlFromInitTriple (DateTriple pending running finished) =
    flipMaybe (dateSymbol "finished" tapeStop) finished <|
    flipMaybe (dateSymbol "running" tapePlay) running <|
    dateSymbol "pending" tapePause pending

htmlFromCompTriple : DateTriple (Maybe Date) -> List (Html msg)
htmlFromCompTriple (DateTriple pending running finished) =
    flipMaybe (dateSymbol "finished" tapeStop) finished <|
    flipMaybe (dateSymbol "running" tapePlay) running <|
    flipMaybe (dateSymbol "pending" tapePause) pending []

dateSymbol : String -> String -> Date -> List (Html a)
dateSymbol tooltip symbol date = [ text date, symbolWithHelp tooltip symbol ]

flipMaybe : (a -> b) -> Maybe a -> b -> b
flipMaybe just m nothing = maybe nothing just m


appAnchor : String -> URI -> Html msg
appAnchor label uri = Html.a [ href uri, target "_blank" ] [ text label ]

maybe : b -> (a -> b) -> Maybe a -> b
maybe n j m =
    case m of
        Nothing -> n
        Just a -> j a


listDecode : Decode.Decoder (List Corpus)
listDecode =
    Decode.list <|
    Decode.map6 Corpus
        (Decode.field "search_id" Decode.int)
        (Decode.field "identifier" Decode.string)
        (Decode.field "query" Decode.string)
        (optField "date_range" DateRange.decode)
        (Decode.field "count" Decode.int)
        (Decode.field "jobs" <| Decode.list decodeJob)

decodeJob : Decode.Decoder Job
decodeJob =
    Decode.map7 Job
        (Decode.field "identifier" Decode.string)
        (Decode.field "filter_query" Decode.string)
        (Decode.field "filter_date" DateRange.decode)
        (Decode.field "initialize_times"
            <| decodeDateTriple (Decode.field "pending" Decode.string))
        (optField "url_nlp" Decode.string)
        (Decode.field "compute_times"
            <| decodeDateTriple (optField "pending" Decode.string))
        (optField "url_te" Decode.string)

decodeDateTriple : Decode.Decoder pending -> Decode.Decoder (DateTriple pending)
decodeDateTriple decodePending =
    Decode.map3 DateTriple
        decodePending
        (optField "running" Decode.string)
        (optField "finished" Decode.string)

listURL : String
listURL = "list"

msgFromResult : Result Http.Error (List Corpus) -> Msg
msgFromResult = switchResult ShowError GetListSuccess

listCmd : String -> Cmd Msg
listCmd = Cmd.map msgFromResult << httpGet listDecode


listPost : JsonReq -> Cmd Msg
listPost = Cmd.map msgFromResult << httpPostJson listDecode


creatorReq : SearchId -> Identifier -> Input -> JsonReq
creatorReq searchId identifier inp =
    ("create",
     Json.object <|
        ("search_id", Json.int searchId) ::
        ("identifier", Json.string (identifier ++ "_" ++ inp.explorerIdent)) ::
        ("filterQuery", Json.string inp.filterQuery) ::
        ("filterDate", DateRange.encode inp.filterDate) ::
        [])


mapAdjacent : (a -> a -> b) -> List a -> List b
mapAdjacent f x = List.map2 f x (List.drop 1 x)

isAscending : List comparable -> Bool
isAscending = List.all identity << mapAdjacent (<=)

resultMap : (a -> Result x b) -> List a -> Result x (List b)
resultMap f =
    let go xt =
            case xt of
                [] -> Ok []
                x::xs -> Result.map2 (::) (f x) (go xs)
    in  go

defaultDate : Date -> Date -> Date
defaultDate deflt date = if date=="" then deflt else date

dateSubrange : DateRange -> DateRange -> Result String Bool
dateSubrange (DateRange from0 to0) (DateRange from1 to1) =
    Result.map (isAscending << List.map Date.toTime) <|
    resultMap Date.fromString <|
    [from1, defaultDate from1 from0, defaultDate to1 to0, to1]

startCreator :
    Maybe DateRange ->
    SearchId -> Identifier -> Input ->
    Result String (Cmd Msg)
startCreator mrng searchId ident inp =
    Result.andThen
        (\corpusRng ->
            dateSubrange inp.filterDate corpusRng
            |>
            Result.andThen
                (\contained ->
                    if contained
                      then Ok <| listPost <| creatorReq searchId ident inp
                      else Err "Filter date range not included in corpus date range."))
    <|
    Result.fromMaybe
        "You cannot create a Topic Explorer for an empty corpus." mrng

showError : Model -> Http.Error -> ( Model, Cmd msg )
showError model error =
    ( { model | errorMsg = formatHTTPError error }, Cmd.none )


-- UPDATE
update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        GetList ->
            ( model, listCmd listURL )
        GetListSuccess corpora ->
            ( { model | errorMsg="", corpora=corpora }, Cmd.none )
        StartCreator mrng searchId ident ->
            resultResolve (showError model << Http.BadStatus << badStatus 404)
            <|
            Result.map ((,) model)
            <|
            Result.andThen (startCreator mrng searchId ident)
            <|
            Result.fromMaybe "Empty parameters"
            <|
            Dict.get ident model.inputs
        ClearInput ident ->
            ( { model | inputs = Dict.remove ident model.inputs }, Cmd.none )
        SetExplorerIdent ident explorerIdent ->
            setInput model ident (\inp -> {inp | explorerIdent=explorerIdent})
        SetFilterQuery ident query ->
            setInput model ident (\inp -> {inp | filterQuery=query})
        SetFilterDateFrom ident from ->
            setInput model ident
                (\inp -> {inp |
                    filterDate = DateRange from (DateRange.to inp.filterDate)})
        SetFilterDateTo ident to ->
            setInput model ident
                (\inp -> {inp |
                    filterDate = DateRange (DateRange.from inp.filterDate) to})
        ShowError error -> showError model error

badStatus : Int -> String -> Http.Response String
badStatus code msg =
    {
        url = "",
        status = {code = code, message = msg},
        headers = Dict.empty,
        body = ""
    }

resultResolve : (e -> a) -> Result e a -> a
resultResolve handle result =
    case result of
        Err e -> handle e
        Ok a -> a

setInput : Model -> String -> (Input -> Input) -> ( Model, Cmd b )
setInput model ident f =
    ( { model |
        inputs =
            Dict.update ident
                (Just << f << maybe emptyInput identity) model.inputs } ,
      Cmd.none )


-- MAIN
main : Program Never Model Msg
main =
    Html.program
        { init = init
        , view = view
        , update = update
        , subscriptions = always Sub.none
        }
