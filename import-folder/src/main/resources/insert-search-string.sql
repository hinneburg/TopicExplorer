insert into SEARCH_STRING 
  (
    SEARCH_STRING_ID
    , SEARCH_STRING
    , DESCRIPTIVE_IDENTIFIER
  )
SELECT
  CASE 
    WHEN (MAX(search_string_id) +1) is NULL THEN 1
    ELSE (MAX(search_string_id) +1)
  END 
  , '${corpus}'
  , '${corpus}'
from 
  SEARCH_STRING
;
