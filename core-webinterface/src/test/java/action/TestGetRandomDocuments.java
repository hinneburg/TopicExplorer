package action;

import org.junit.Test;

import cc.topicexplorer.actions.getrandomdocs.GetRandomDocuments;
import static org.junit.Assert.*;

public class TestGetRandomDocuments {

	@Test
	public void testGetMainQuery() {
		GetRandomDocuments myActionGetRandomDocuments = new GetRandomDocuments(20);
		myActionGetRandomDocuments.addDocumentAttributeToSelectClause("DOCUMENT.Text$FULLTEXT");
		String generateMainQuery = myActionGetRandomDocuments.getMainQuery();
		assertEquals("MainQuery should be","SELECT "
			+ "x.DOCUMENT_ID,x.Text$FULLTEXT,"
			+ "y.TOPIC_ID,"
			+ "y.PR_TOPIC_GIVEN_DOCUMENT,"
			+ "y.PR_DOCUMENT_GIVEN_TOPIC "
			+ "FROM (SELECT DOCUMENT.DOCUMENT_ID, DOCUMENT.Text$FULLTEXT "
			+ "FROM DOCUMENT "
			+ "LIMIT 20) x, DOCUMENT_TOPIC y "
			+ "WHERE x.DOCUMENT_ID=y.DOCUMENT_ID "
			+ "ORDER BY x.DOCUMENT_ID, y.TOPIC_ID"
			, generateMainQuery);
	}
	
}
