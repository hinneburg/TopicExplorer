package cc.topicexplorer.plugin.frame.procecssing.tables.documentchunk;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import cc.topicexplorer.plugin.frame.preprocessing.tables.documentchunk.DocumentChunk;
import cc.topicexplorer.plugin.frame.preprocessing.tables.documentchunk.DocumentChunk.ChunkRow;

public class DocumentChunkTest {

	@Test
	public void test_cleanDelimiterList() {
		List<String> inputDelimiters = new ArrayList();
		inputDelimiters.add(" .  ");
		inputDelimiters.add("; ");
		inputDelimiters.add(",");
		inputDelimiters.add("!");
		inputDelimiters.add("?");
		inputDelimiters.add("");

		List<String> expectedDelimiters = new ArrayList();
		expectedDelimiters.add(".");
		expectedDelimiters.add(";");
		expectedDelimiters.add(",");
		expectedDelimiters.add("!");
		expectedDelimiters.add("?");

		assertEquals(expectedDelimiters, DocumentChunk.cleanDelimiterList(inputDelimiters));
	}

	@Test
	public void test_getSetOfCutPositions() {
		String text = ". abc ! Ügh?, 64;56;.";
		ArrayList<String> delimiters = new ArrayList<String>();
		delimiters.add(".");
		delimiters.add(";");
		delimiters.add(",");
		delimiters.add("!");
		delimiters.add("?");

		TreeSet<Integer> expectedCutPositions = new TreeSet<Integer>();
		expectedCutPositions.add(0);
		expectedCutPositions.add(6);
		expectedCutPositions.add(11);
		expectedCutPositions.add(12);
		expectedCutPositions.add(16);
		expectedCutPositions.add(19);
		expectedCutPositions.add(20);

		SortedSet<Integer> actualCutPositions = DocumentChunk.getSetOfCutPositions(text, delimiters);
		assertEquals(expectedCutPositions, actualCutPositions);

	}

	@Test
	public void test_getListOfChunkRows() {
		TreeSet<Integer> cutPositions = new TreeSet<Integer>();
		cutPositions.add(0);
		cutPositions.add(6);
		cutPositions.add(11);
		cutPositions.add(12);
		cutPositions.add(16);
		cutPositions.add(19);
		cutPositions.add(20);

		List<ChunkRow> actualListOfChunkRows = DocumentChunk.getListOfChunkRows(cutPositions, 21);
		List<ChunkRow> expectedListOfChunkRows = new ArrayList<ChunkRow>();
		ChunkRow r1 = new ChunkRow();
		r1.startPosition = 0;
		r1.endPosition = 6;
		expectedListOfChunkRows.add(r1);
		ChunkRow r2 = new ChunkRow();
		r2.startPosition = 7;
		r2.endPosition = 11;
		expectedListOfChunkRows.add(r2);
		ChunkRow r3 = new ChunkRow();
		r3.startPosition = 12;
		r3.endPosition = 12;
		expectedListOfChunkRows.add(r3);
		ChunkRow r4 = new ChunkRow();
		r4.startPosition = 13;
		r4.endPosition = 16;
		expectedListOfChunkRows.add(r4);
		ChunkRow r5 = new ChunkRow();
		r5.startPosition = 17;
		r5.endPosition = 19;
		expectedListOfChunkRows.add(r5);
		ChunkRow r6 = new ChunkRow();
		r6.startPosition = 20;
		r6.endPosition = 20;
		expectedListOfChunkRows.add(r6);

		System.out.println(expectedListOfChunkRows.equals(actualListOfChunkRows));
		System.out.println(expectedListOfChunkRows);
		System.out.println(actualListOfChunkRows);

		assertEquals(expectedListOfChunkRows, actualListOfChunkRows);

	}
}
