module DateRange exposing
        (DateRange(DateRange), Date, from, to, decode, encode, format)

import Json.Encode as Json
import Json.Decode as Decode


type DateRange = DateRange Date Date
type alias Date = String

from : DateRange -> Date
from (DateRange from _) = from

to : DateRange -> Date
to (DateRange _ to) = to

decode : Decode.Decoder DateRange
decode =
    Decode.map2 DateRange
        (Decode.field "from" Decode.string)
        (Decode.field "to" Decode.string)

encode : DateRange -> Json.Value
encode (DateRange from to) =
    Json.object
        [("from", Json.string from),
         ("to", Json.string to)]

format : DateRange -> String
format (DateRange from to) = from ++ " - " ++ to
