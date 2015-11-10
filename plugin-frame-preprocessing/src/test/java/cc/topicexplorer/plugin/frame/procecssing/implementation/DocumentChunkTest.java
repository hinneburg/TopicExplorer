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
		ChunkRow r3 = new ChunkRow();
		r3.startPosition = 13;
		r3.endPosition = 16;
		expectedListOfChunkRows.add(r3);
		ChunkRow r4 = new ChunkRow();
		r4.startPosition = 17;
		r4.endPosition = 19;
		expectedListOfChunkRows.add(r4);

		assertEquals(expectedListOfChunkRows, actualListOfChunkRows);
    }

	@Test
    public void test_chunking2() {
		List<String> inputDelimiters = new ArrayList<String>();
		inputDelimiters.add(",");
		inputDelimiters.add(".");
		inputDelimiters.add(":");
		inputDelimiters.add("?");
		inputDelimiters.add("!");
		inputDelimiters.add(";");
		String text = "Es war ein Mann, der hatte drei Söhne und weiter nichts im Vermögen als das Haus, worin er wohnte. Nun hätte jeder gerne nach seinem Tode das Haus gehabt, dem Vater war aber einer so lieb wie der andere, da wußte er nicht, wie er's anfangen sollte, daß er keinem zu nahe tät. Verkaufen wollte er das Haus auch nicht,";
		
		SortedSet<Integer> actualCutPos = DocumentChunk.getSetOfCutPositions(
				text, 
				DocumentChunk.cleanDelimiterList(inputDelimiters)
				);
		SortedSet<Integer> expectedCutPos = new TreeSet<Integer>();
		expectedCutPos.add(15);
		expectedCutPos.add(80);
		expectedCutPos.add(97);
		expectedCutPos.add(153);
		expectedCutPos.add(202);
		expectedCutPos.add(221);
		expectedCutPos.add(247);
		expectedCutPos.add(274);
		expectedCutPos.add(315);

		assertEquals(expectedCutPos,actualCutPos);
		
		List<ChunkRow> actualListOfChunkRows = DocumentChunk.getListOfChunkRows(
				actualCutPos, 
				text.length());
		List<ChunkRow> expectedListOfChunkRows = new ArrayList<ChunkRow>();
		ChunkRow r1 = new ChunkRow();
		r1.startPosition = 0;
		r1.endPosition = 15;
		expectedListOfChunkRows.add(r1);
		ChunkRow r2 = new ChunkRow();
		r2.startPosition = 16;
		r2.endPosition = 80;
		expectedListOfChunkRows.add(r2);
		ChunkRow r3 = new ChunkRow();
		r3.startPosition = 81;
		r3.endPosition = 97;
		expectedListOfChunkRows.add(r3);
		ChunkRow r4 = new ChunkRow();
		r4.startPosition = 98;
		r4.endPosition = 153;
		expectedListOfChunkRows.add(r4);
		ChunkRow r5 = new ChunkRow();
		r5.startPosition = 154;
		r5.endPosition = 202;
		expectedListOfChunkRows.add(r5);
		ChunkRow r6 = new ChunkRow();
		r6.startPosition = 203;
		r6.endPosition = 221;
		expectedListOfChunkRows.add(r6);
		ChunkRow r7 = new ChunkRow();
		r7.startPosition = 222;
		r7.endPosition = 247;
		expectedListOfChunkRows.add(r7);
		ChunkRow r8 = new ChunkRow();
		r8.startPosition = 248;
		r8.endPosition = 274;
		expectedListOfChunkRows.add(r8);
		ChunkRow r9 = new ChunkRow();
		r9.startPosition = 275;
		r9.endPosition = 315;
		expectedListOfChunkRows.add(r9);

		assertEquals(expectedListOfChunkRows, actualListOfChunkRows);
    }
	@Test
    public void test_chunking3() {
		List<String> inputDelimiters = new ArrayList<String>();
		inputDelimiters.add(",");
		inputDelimiters.add(".");
		inputDelimiters.add(":");
		inputDelimiters.add("?");
		inputDelimiters.add("!");
		inputDelimiters.add(";");
		System.out.println(inputDelimiters.toString());
		String text = "Es war ein Mann, der hatte drei Söhne und weiter nichts im Vermögen als das Haus, worin er wohnte. Nun hätte jeder gerne nach seinem Tode das Haus gehabt, dem Vater war aber einer so lieb wie der andere, da wußte er nicht, wie er's anfangen sollte, daß er keinem zu nahe tät. Verkaufen wollte er das Haus auch nicht";
		
		SortedSet<Integer> actualCutPos = DocumentChunk.getSetOfCutPositions(
				text, 
				DocumentChunk.cleanDelimiterList(inputDelimiters)
				);
		SortedSet<Integer> expectedCutPos = new TreeSet<Integer>();
		expectedCutPos.add(15);
		expectedCutPos.add(80);
		expectedCutPos.add(97);
		expectedCutPos.add(153);
		expectedCutPos.add(202);
		expectedCutPos.add(221);
		expectedCutPos.add(247);
		expectedCutPos.add(274);
		
		assertEquals(expectedCutPos,actualCutPos);
		
		List<ChunkRow> actualListOfChunkRows = DocumentChunk.getListOfChunkRows(
				actualCutPos, 
				text.length());
		List<ChunkRow> expectedListOfChunkRows = new ArrayList<ChunkRow>();
		ChunkRow r1 = new ChunkRow();
		r1.startPosition = 0;
		r1.endPosition = 15;
		expectedListOfChunkRows.add(r1);
		ChunkRow r2 = new ChunkRow();
		r2.startPosition = 16;
		r2.endPosition = 80;
		expectedListOfChunkRows.add(r2);
		ChunkRow r3 = new ChunkRow();
		r3.startPosition = 81;
		r3.endPosition = 97;
		expectedListOfChunkRows.add(r3);
		ChunkRow r4 = new ChunkRow();
		r4.startPosition = 98;
		r4.endPosition = 153;
		expectedListOfChunkRows.add(r4);
		ChunkRow r5 = new ChunkRow();
		r5.startPosition = 154;
		r5.endPosition = 202;
		expectedListOfChunkRows.add(r5);
		ChunkRow r6 = new ChunkRow();
		r6.startPosition = 203;
		r6.endPosition = 221;
		expectedListOfChunkRows.add(r6);
		ChunkRow r7 = new ChunkRow();
		r7.startPosition = 222;
		r7.endPosition = 247;
		expectedListOfChunkRows.add(r7);
		ChunkRow r8 = new ChunkRow();
		r8.startPosition = 248;
		r8.endPosition = 274;
		expectedListOfChunkRows.add(r8);
		ChunkRow r9 = new ChunkRow();
		r9.startPosition = 275;
		r9.endPosition = 314;
		expectedListOfChunkRows.add(r9);

		assertEquals(expectedListOfChunkRows, actualListOfChunkRows);
    }

}
