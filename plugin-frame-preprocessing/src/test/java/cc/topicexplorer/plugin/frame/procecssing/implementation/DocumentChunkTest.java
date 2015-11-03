package cc.topicexplorer.plugin.frame.procecssing.implementation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import cc.topicexplorer.plugin.frame.preprocessing.implementation.DocumentChunk;
import cc.topicexplorer.plugin.frame.preprocessing.implementation.DocumentChunk.ChunkRow;

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
//		Text '.     .    ..   .  . '
		TreeSet<Integer> cutPositions = new TreeSet<Integer>();
		cutPositions.add(0);
		cutPositions.add(6);
		cutPositions.add(11);
		cutPositions.add(12);
		cutPositions.add(16);
		cutPositions.add(19);
		
		List<ChunkRow> actualListOfChunkRows = DocumentChunk.getListOfChunkRows(cutPositions, 21);
		List<ChunkRow> expectedListOfChunkRows = new ArrayList<ChunkRow>();
		ChunkRow r1 = new ChunkRow();
		r1.startPosition = 1;
		r1.endPosition = 6;
		expectedListOfChunkRows.add(r1);
		ChunkRow r2 = new ChunkRow();
		r2.startPosition = 7;
		r2.endPosition = 11;
		expectedListOfChunkRows.add(r2);
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

		assertEquals(expectedListOfChunkRows, actualListOfChunkRows);

	}

	@Test
	public void test_getListOfChunkRows_emptyCutPositions_emptyText() {
//		Text ''
		TreeSet<Integer> cutPositions = new TreeSet<Integer>();
		
		List<ChunkRow> actualListOfChunkRows = DocumentChunk.getListOfChunkRows(cutPositions, 0);
		List<ChunkRow> expectedListOfChunkRows = new ArrayList<ChunkRow>();
		
		assertEquals(expectedListOfChunkRows, actualListOfChunkRows);

	}

	@Test
	public void test_getListOfChunkRows_emptyCutPositions_nonemptyText1() {
//		Text ' '
		TreeSet<Integer> cutPositions = new TreeSet<Integer>();
		
		List<ChunkRow> actualListOfChunkRows = DocumentChunk.getListOfChunkRows(cutPositions, 1);
		List<ChunkRow> expectedListOfChunkRows = new ArrayList<ChunkRow>();
		ChunkRow r1 = new ChunkRow();
		r1.startPosition = 0;
		r1.endPosition = 0;
		expectedListOfChunkRows.add(r1);
		
		assertEquals(expectedListOfChunkRows, actualListOfChunkRows);

	}

	@Test
	public void test_getListOfChunkRows_emptyCutPositions_nonemptyText2() {
//		Text '  '
		TreeSet<Integer> cutPositions = new TreeSet<Integer>();
		
		List<ChunkRow> actualListOfChunkRows = DocumentChunk.getListOfChunkRows(cutPositions, 2);
		List<ChunkRow> expectedListOfChunkRows = new ArrayList<ChunkRow>();
		ChunkRow r1 = new ChunkRow();
		r1.startPosition = 0;
		r1.endPosition = 1;
		expectedListOfChunkRows.add(r1);
		
		assertEquals(expectedListOfChunkRows, actualListOfChunkRows);

	}

	@Test
	public void test_getListOfChunkRows_nonemptyCutPositions_nonemptyText() {
//		Text '.     .    ..        '
		TreeSet<Integer> cutPositions = new TreeSet<Integer>();
		cutPositions.add(0);
		cutPositions.add(6);
		cutPositions.add(11);
		cutPositions.add(12);

		List<ChunkRow> actualListOfChunkRows = DocumentChunk.getListOfChunkRows(cutPositions, 21);
		List<ChunkRow> expectedListOfChunkRows = new ArrayList<ChunkRow>();
		ChunkRow r1 = new ChunkRow();
		r1.startPosition = 1;
		r1.endPosition = 6;
		expectedListOfChunkRows.add(r1);
		ChunkRow r2 = new ChunkRow();
		r2.startPosition = 7;
		r2.endPosition = 11;
		expectedListOfChunkRows.add(r2);
		ChunkRow r3 = new ChunkRow();
		ChunkRow r4 = new ChunkRow();
		r4.startPosition = 13;
		r4.endPosition = 20;
		expectedListOfChunkRows.add(r4);
		
		assertEquals(expectedListOfChunkRows, actualListOfChunkRows);

	}
	
	@Test
    public void test_chunking() {
		List<String> inputDelimiters = new ArrayList();
		inputDelimiters.add(" .  ");
		inputDelimiters.add("; ");
		inputDelimiters.add(",");
		inputDelimiters.add("!");
		inputDelimiters.add("?");
		inputDelimiters.add("");
		String text = ". abc ! Ügh?, 64;56;.";

		List<ChunkRow> actualListOfChunkRows = DocumentChunk.getListOfChunkRows(
				DocumentChunk.getSetOfCutPositions(
						text, 
						DocumentChunk.cleanDelimiterList(inputDelimiters)
						), 
						text.length());
		List<ChunkRow> expectedListOfChunkRows = new ArrayList<ChunkRow>();
		ChunkRow r1 = new ChunkRow();
		r1.startPosition = 1;
		r1.endPosition = 6;
		expectedListOfChunkRows.add(r1);
		ChunkRow r2 = new ChunkRow();
		r2.startPosition = 7;
		r2.endPosition = 11;
		expectedListOfChunkRows.add(r2);
		ChunkRow r4 = new ChunkRow();
		r4.startPosition = 13;
		r4.endPosition = 16;
		expectedListOfChunkRows.add(r4);
		ChunkRow r5 = new ChunkRow();
		r5.startPosition = 17;
		r5.endPosition = 19;
		expectedListOfChunkRows.add(r5);

		assertEquals(expectedListOfChunkRows, actualListOfChunkRows);


    }
}
