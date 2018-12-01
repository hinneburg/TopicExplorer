insert into SEARCH_STRING 
  (
    SEARCH_STRING_ID
    , SEARCH_STRING
    , DESCRIPTIVE_IDENTIFIER
  )
SELECT
  MAX(search_string_id) +1
  , '${corpus}'
  , '${corpus}'
from 
  SEARCH_STRING
;
