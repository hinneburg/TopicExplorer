package cc.topicexplorer.plugin.frame.preprocessing.implementation;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

import cc.topicexplorer.database.Database;

public class DocumentChunk {

	private static final Logger logger = Logger.getLogger(DocumentChunk.class);
	private static final String pluginPrefix = "FRAME";

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

	public static final String getLoadChunksStatement() {
		// @formatter:off
		return 
		"LOAD DATA INFILE 'temp/frames.sql.csv' IGNORE INTO TABLE" +
		pluginPrefix + "$DOCUMENT_CHUNK " +
		"CHARACTER SET utf8 FIELDS TERMINATED BY ',' " +
		"(DOCUMENT_ID, START_POSITION, END_POSITION);";
	   // @formatter:on
	}

	public static final String getCreateIndexStatement() {
		// @formatter:off
		return 
		"CREATE INDEX " +
		pluginPrefix + "$DOCUMENT_CHUNK_IDX1 " +
		"ON " + pluginPrefix + "$DOCUMENT_CHUNK"+
		"(DOCUMENT_ID, START_POSITION, END_POSITION);";
	   // @formatter:on
	}

	public static final List<String> cleanDelimiterList(
			final List<String> delimiterList) {
		final ArrayList<String> cleanDelimiterList = new ArrayList<String>();
		for (final String s : delimiterList) {
			if (!s.equals("")) {
				cleanDelimiterList.add(s.trim());
			}
		}
		return cleanDelimiterList;
	}

	public static final SortedSet<Integer> getSetOfCutPositions(
			final String text, 
			final List<String> delimiterList) {
		final TreeSet<Integer> cutPosition = new TreeSet<Integer>();

		for (final String delimiter : delimiterList) {
			Integer position = text.indexOf(delimiter);
			while (position >= 0) {
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

	    @Override
	    public int hashCode() {
	        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
	            // if deriving: appendSuper(super.hashCode()).
	            append(startPosition).
	            append(endPosition).
	            toHashCode();
	    }

	    @Override
	    public boolean equals(Object obj) {
	       if (!(obj instanceof ChunkRow))
	            return false;
	        if (obj == this)
	            return true;

	        ChunkRow rhs = (ChunkRow) obj;
	        return new EqualsBuilder().
	            // if deriving: appendSuper(super.equals(obj)).
	            append(startPosition, rhs.startPosition).
	            append(endPosition, rhs.endPosition).
	            isEquals();
	    } 
		@Override
		public String toString() {
			return "(" + startPosition.toString() + "," + endPosition.toString() + ")";
		}
	}

	public static final List<ChunkRow> getListOfChunkRows(
			final SortedSet<Integer> cutPositions, 
			final Integer textLength) {
		if ( cutPositions.isEmpty() && textLength==0 ) return Collections.<ChunkRow>emptyList();
		if ( cutPositions.isEmpty() && textLength>0 ) {
			// No cut positions but non-empty text, return complete text as chunk
			final ChunkRow chunk = new ChunkRow();
			chunk.startPosition=0;
			chunk.endPosition=textLength - 1;
			final List<ChunkRow> rowsOfChunks = new ArrayList<ChunkRow>();
			rowsOfChunks.add(new ChunkRow(chunk));
			return rowsOfChunks;
		}
		if ( !cutPositions.isEmpty() && cutPositions.last() > textLength-1 ) {
			logger.error("Cut positions out of text size.");
			throw new RuntimeException("Cut positions out of text size.");
		}
				
		final List<ChunkRow> rowsOfChunks = new ArrayList<ChunkRow>();
		final ChunkRow chunk = new ChunkRow();
		final Iterator<Integer> it = cutPositions.iterator();
		final Integer cut = it.next();
		if (cut==0) chunk.startPosition = 1;
		else chunk.startPosition = 0;
		while (it.hasNext()) {
			chunk.endPosition = it.next();
			if (chunk.startPosition < chunk.endPosition)
				rowsOfChunks.add(new ChunkRow(chunk));
			chunk.startPosition = chunk.endPosition + 1;
		}
		if (chunk.startPosition <= textLength -1) {
			chunk.endPosition=textLength -1;
			rowsOfChunks.add(new ChunkRow(chunk));
		}
		return rowsOfChunks;
	}

	public static final void fill(final List<String> delimiterList, final Database database) throws IOException, SQLException {
		final List<String> cleanDelimiterList = cleanDelimiterList(delimiterList);

		final ResultSet documentRS = database.executeQuery( getSelectDocumentTextStatement() );
		logger.info("Document texts selected.");

		final BufferedWriter chunkWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				"temp/frameDelimiter.sql.csv", true), "UTF-8"));

		while (documentRS.next()) {
			final Integer documentId = documentRS.getInt("DOCUMENT_ID");
			final String text = documentRS.getString("TEXT$FULLTEXT");
			final SortedSet<Integer> cutPositions = getSetOfCutPositions(text, cleanDelimiterList);
			final List<ChunkRow> rowsOfChunks = getListOfChunkRows(cutPositions, text.length());

			for (final ChunkRow chunk : rowsOfChunks) {
				chunkWriter.write(documentId.toString() + "," + chunk.startPosition.toString() + ","
						+ chunk.endPosition.toString() + "\n");
			}
		}
		chunkWriter.close();
		logger.info("Document chunks written into file.");

		database.executeUpdateQuery( getLoadChunksStatement() );
		logger.info("Document chunks loaded into database table.");
		database.executeUpdateQuery( getCreateIndexStatement() );
		logger.info("Index on document chunk table created.");
	}
}
