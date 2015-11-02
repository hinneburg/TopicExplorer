package cc.topicexplorer.plugin.frame.preprocessing.tables.documentchunk;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import cc.topicexplorer.database.Database;

public class DocumentChunk {

	private static final Logger logger = Logger.getLogger(DocumentChunk.class);
	private static final String pluginPrefix = "FRAME";

	// private final Database database;
	//
	// DocumentChunk(Database database) {
	// this.database = database;
	// }

	public static final String getCreateTableStatement() {
		// @formatter:off
		return 
		"CREATE TABLE " + 
		   pluginPrefix + "$DOCUMENT_CHUNK" +
		   "(" + 
		     "DOCUMENT_ID INTEGER UNSIGNED,"	+ 
		     "START_POSITION INTEGER UNSIGNED," + 
		     "END_POSITION INTEGER UNSIGNED" + 
		   ") ENGINE INNODB";
	   // @formatter:on
	}

	public static final String getSelectDocumentTextStatement() {
		// @formatter:off
		return 
		"SELECT " +
		  "DOCUMENT_ID," +
		  "TEXT$FULLTEXT " +
		"FROM " +
		  "DOCUMENT";
	   // @formatter:on
	}

	public static final List<String> cleanDelimiterList(List<String> delimiterList) {
		ArrayList<String> cleanDelimiterList = new ArrayList<String>();
		for (String s : delimiterList) {
			if (!s.equals("")) {
				cleanDelimiterList.add(s.trim());
			}
		}
		return cleanDelimiterList;
	}

	public static final SortedSet<Integer> getSetOfCutPositions(String text, List<String> delimiterList) {
		TreeSet<Integer> cutPosition = new TreeSet<Integer>();

		for (String delimiter : delimiterList) {
			System.out.println(delimiter);
			Integer position = text.indexOf(delimiter);
			while (position >= 0) {
				System.out.println(position.toString());
				cutPosition.add(position);
				position = text.indexOf(delimiter, position + 1);
			}
		}
		return cutPosition;

	}

	public static class ChunkRow {
		public Integer startPosition;
		public Integer endPosition;

		public ChunkRow() {
			startPosition = 0;
			endPosition = 0;
		}

		public ChunkRow(ChunkRow r) {
			startPosition = r.startPosition;
			endPosition = r.endPosition;
		}

		public boolean equals(ChunkRow r) {
			return this.startPosition.equals(r.startPosition) && this.endPosition.equals(r.endPosition);
		}

		@Override
		public String toString() {
			return "(" + startPosition.toString() + "," + endPosition.toString() + ")";
		}
	}

	public static final List<ChunkRow> getListOfChunkRows(SortedSet<Integer> cutPositions, Integer textLength) {
		cutPositions.add(0);
		cutPositions.add(textLength - 1);
		List<ChunkRow> rowsOfChunks = new ArrayList<ChunkRow>();
		ChunkRow chunk = new ChunkRow();
		Iterator<Integer> it = cutPositions.iterator();
		chunk.startPosition = it.next();
		while (it.hasNext()) {
			chunk.endPosition = it.next();
			rowsOfChunks.add(new ChunkRow(chunk));
			chunk.startPosition = chunk.endPosition + 1;
		}
		return rowsOfChunks;
	}

	public static final void fill(List<String> delimiterList, Database database) throws IOException, SQLException {
		List<String> cleanDelimiterList = cleanDelimiterList(delimiterList);

		ResultSet documentRS = database.executeQuery(getSelectDocumentTextStatement());

		BufferedWriter chunkWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				"temp/frameDelimiter.sql.csv", true), "UTF-8"));

		while (documentRS.next()) {
			Integer documentId = documentRS.getInt("DOCUMENT_ID");
			String text = documentRS.getString("TEXT$FULLTEXT");
			SortedSet<Integer> cutPositions = getSetOfCutPositions(text, cleanDelimiterList);
			List<ChunkRow> rowsOfChunks = getListOfChunkRows(cutPositions, text.length());

			for (ChunkRow chunk : rowsOfChunks) {
				chunkWriter.write(documentId.toString() + "," + chunk.startPosition.toString() + ","
						+ chunk.endPosition.toString() + "\n");
			}
		}
		chunkWriter.close();

	}
}
