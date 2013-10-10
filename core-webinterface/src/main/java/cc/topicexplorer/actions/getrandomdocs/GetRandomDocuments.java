/**
 * API for GetRandomDocuments action
 * 
 * The action picks documents randomly from the database and joins the related topics to them.
 * It ask first for the total number of documents
 *  
 * SELECT COUNT(*) AS COUNT FROM DOCUMENT;
 * 
 * and the ...

SELECT * 
FROM (
	SELECT
		DOCUMENT.DOCUMENT_ID, 
		DOCUMENT.TEXT$TITLE, 
		DOCUMENT.LINK$URL, 
		DOCUMENT.TEXT$FULLTEXT 
	FROM DOCUMENT 
	ORDER BY DOCUMENT_ID DESC LIMIT 74,20) x 
	JOIN DOCUMENT_TOPIC y 
	on (x.DOCUMENT_ID = y.DOCUMENT_ID) 
ORDER BY x.DOCUMENT_ID, y.TOPIC_ID
 
 * 
 */
package cc.topicexplorer.actions.getrandomdocs;

import cc.topicexplorer.database.SelectMap;

public class GetRandomDocuments {

	private SelectMap preQuery, innerQuery, mainQuery;

	GetRandomDocuments(Integer NumberOfRandomDocuments)
	{
		preQuery = new SelectMap();
		preQuery.select.add("COUNT(*) AS COUNT"); 
		preQuery.from.add("DOCUMENT"); 

		innerQuery = new SelectMap();
		
		innerQuery.select.add("DOCUMENT.DOCUMENT_ID"); 
		innerQuery.from.add("DOCUMENT"); 
		innerQuery.limit = NumberOfRandomDocuments; 
		
		mainQuery = new SelectMap();
		
		mainQuery.select.add("*"); 
		mainQuery.from.add("DOCUMENT_TOPIC y"); 
		mainQuery.where.add("x.DOCUMENT_ID=y.DOCUMENT_ID"); 
		mainQuery.orderBy.add("x.DOCUMENT_ID"); 
		mainQuery.orderBy.add("y.TOPIC_ID"); 
	
	}
	
	void addSelectAttributeToInnerQuery (String attribute) {
		innerQuery.select.add(attribute);
	}
	
	
}
