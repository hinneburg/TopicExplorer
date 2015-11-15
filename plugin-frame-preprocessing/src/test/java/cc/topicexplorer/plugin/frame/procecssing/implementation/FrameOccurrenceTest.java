package cc.topicexplorer.plugin.frame.procecssing.implementation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import cc.topicexplorer.plugin.frame.preprocessing.implementation.FrameOccurrence;
import cc.topicexplorer.plugin.frame.preprocessing.implementation.FrameOccurrence.tbl.FRAME$FRAME_OCCURRENCE;

public class FrameOccurrenceTest {

	private String readFileFromClasspath(String fileName) throws IOException {
		InputStream fis = this.getClass().getResourceAsStream("/" + fileName);
		return IOUtils.toString(fis, "UTF-8");
	}

	public String aufgabe2a_getSQL() throws IOException {
		return (readFileFromClasspath("Uebung03_Aufgabe02a.sql"));
	}

	@Test
	public void test_FrameOccurrenceTable() throws IOException {
		assertEquals("FRAME$FRAME_OCCURRENCE", FRAME$FRAME_OCCURRENCE.name);
		assertEquals(readFileFromClasspath("FrameOccurrencesCreate.sql")
				.replace("\n", ""), FRAME$FRAME_OCCURRENCE.create_stmt);
		assertEquals(readFileFromClasspath("FrameOccurrencesFill.sql")
				.replace("\n", ""), FrameOccurrence.getInsertTableStatement());
	}

}
