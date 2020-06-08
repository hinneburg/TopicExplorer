package cc.topicexplorer.dataimport;

import java.io.IOException;

import org.junit.Test;

import cc.topicexplorer.dataimport.ImportFolder.TeiParseResult;

import static org.junit.Assert.assertEquals;

public class jsoupParsingTest {

	@Test
	public void testReadZeroLengthDocument() throws IOException {
		TeiParseResult resultZeroDoc = new TeiParseResult();
		resultZeroDoc = ImportFolder.parseGrobIdTei(jsoupParsingTest.class.getResourceAsStream("/" + "zeroLengthFile.xml"), "");
		assertEquals(true, resultZeroDoc.empty);

		TeiParseResult resultNonZeroDoc = new TeiParseResult();
		resultNonZeroDoc = ImportFolder.parseGrobIdTei(jsoupParsingTest.class.getResourceAsStream("/" + "notEmptyDocument.xml"), "");
		assertEquals(false, resultNonZeroDoc.empty);
		
	}

	
	@Test
	public void testReadDocument() throws IOException {

		TeiParseResult resultNonZeroDoc = new TeiParseResult();
		resultNonZeroDoc = ImportFolder.parseGrobIdTei(jsoupParsingTest.class.getResourceAsStream("/" + "docWithNothing.xml"), "docWithNothing");
		assertEquals(false, resultNonZeroDoc.empty);
		assertEquals("file: docWithNothing, , , ", resultNonZeroDoc.title);
		assertEquals("", resultNonZeroDoc.text);
		
		
	}

}
