	insert into CRAWL 
	  (
	    SEARCH_STRING_ID
	    , CRAWL_DATE
	    , TABLE_NAME
	    , ACTIVE
	    , LAST_MODIFIED_DATE
	    , LAST_MODIFIED_USER
	  )
	SELECT
	  MAX(search_string_id)
	  , now()
	  , 'COPRUS_${corpus}'
	  , 0
	  , now()
	  , 'import-folder'
	from 
	  SEARCH_STRING
	;
