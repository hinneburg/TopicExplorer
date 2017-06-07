module Query.Parser (complete) where

import Query (Query(Literal, Not, And, Or))

import qualified Text.ParserCombinators.Parsec.Expr as Expr
import qualified Text.ParserCombinators.Parsec as Parsec
import Text.ParserCombinators.Parsec.Language (emptyDef)
import Text.ParserCombinators.Parsec.Token (TokenParser, makeTokenParser, stringLiteral)
import Text.ParserCombinators.Parsec ((<?>), (<|>))

import Control.Applicative ((<*))
import Data.Functor ((<$))


type Parser = Parsec.CharParser ()

lexeme :: Parser a -> Parser a
lexeme p = do a <- p; Parsec.spaces; return a

string :: String -> Parser String
string str = lexeme $ Parsec.string str

parens :: Parser a -> Parser a
parens = Parsec.between (string "(") (string ")")

literal :: Parser Query
literal =
   fmap Literal $
      stringLiteral tokenParser
      <|>
      lexeme (Parsec.many1 Parsec.alphaNum)

tokenParser :: TokenParser ()
tokenParser = makeTokenParser emptyDef

complete :: Parser (Maybe Query)
complete = Parsec.optionMaybe expr <* Parsec.eof

expr :: Parser Query
expr =
   Expr.buildExpressionParser table term
   <?> "expression"

term :: Parser Query
term =
   parens expr
   <|> literal
   <?> "simple expression"

table :: Expr.OperatorTable Char () Query
table =
   [ prefix "NOT" Not ] :
   [ binary "AND" And Expr.AssocRight ] :
   [ binary "OR" Or Expr.AssocRight ] :
   []

binary :: String -> (a -> a -> a) -> Expr.Assoc -> Expr.Operator Char () a
binary name fun assoc = Expr.Infix (fun <$ string name) assoc

prefix :: String -> (a -> a) -> Expr.Operator Char () a
prefix  name fun = Expr.Prefix (fun <$ string name)
