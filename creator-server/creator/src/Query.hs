module Query where

import qualified TopicExplorer.Configuration.Database as SQL


data Query = Literal String | Not Query | And Query Query | Or Query Query
   deriving (Show)


format :: Query -> String
format =
   let parens str = "(" ++ str ++ ")"
       go (Literal str) =
         "DOCUMENT_TEXT LIKE '%" ++ SQL.escapeString str ++ "%'"
       go (Not x) = "NOT " ++ parens (go x)
       go (And x y) = parens (go x) ++ " AND " ++ parens (go y)
       go (Or x y) = parens (go x) ++ " OR " ++ parens (go y)
   in  go

formatMaybe :: Maybe Query -> String
formatMaybe = maybe "TRUE" format
