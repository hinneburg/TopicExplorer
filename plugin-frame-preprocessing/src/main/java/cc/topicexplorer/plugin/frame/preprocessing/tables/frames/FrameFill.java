package cc.topicexplorer.plugin.frame.preprocessing.tables.frames;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * <b>Needed database tables</b>: {@code TERM}, {@code TERM_TOPIC}, {@code DOCUMENT_TERM_TOPIC}.
 * <p>
 * <b>{@link #fillTable()} method</b> will arrange the content of the table {@code TopTermsDocSameTopic} and search for
 * frames (noun-verb combinations) in that table. Every so found frame will be written to the table {@code FRAMES}. For
 * every topic only the 20 best fitting nouns and 20 best fitting verbs are used.
 * <p>
 * <b>Frames will be identified</b> if the distance between a noun and a succeeding verb is less or equal 150
 * characters. Further both, noun and verb, must be consistent with their {@code TOPIC_ID} and {@code DOCUMENT_ID}.
 * Accepted values in the column {@code $WORDTYPE} are {@code SUBS} and {@code VERB}.
 * <p>
 * Within any frame there will be <b>only one noun and one verb</b>. No second verb to a specific noun and no second
 * noun to a specific verb, unless one occurs a second time in the text corpus.
 */
public final class FrameFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(FrameFill.class);

	@Override
	public void setTableName() {
		this.tableName = "FRAMES";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("TermFill", "TermTopicFill", "DocumentTermTopicFill", "FrameCreate");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public void fillTable() {
		fillWordtypeColumnOfTableTerm();
		createAndFillTableTopTerms();
		createAndFillTableTopTermsDocSameTopic();
		fillTableFrames();
		dropTemporaryTablesAndColumns();
		logger.info(String.format("Table %s is filled.", this.tableName));
	}

	private void fillWordtypeColumnOfTableTerm() {
		try {
			database.executeUpdateQueryForUpdate("alter table TERM add column WORDTYPE VARCHAR(255) COLLATE utf8_bin");
			database.executeUpdateQueryForUpdate("update TERM, DOCUMENT_TERM_TOPIC "
					+ "SET TERM.WORDTYPE=DOCUMENT_TERM_TOPIC.WORDTYPE$WORDTYPE WHERE DOCUMENT_TERM_TOPIC.TERM=TERM.TERM_NAME");
		} catch (SQLException e) {
			logger.error("Table TERM could not be altered.");
			throw new RuntimeException(e);
		}
	}

	private void createAndFillTableTopTerms() {
		try {
			database.executeUpdateQuery("create table TopTerms ENGINE = MEMORY DEFAULT CHARSET=utf8 COLLATE=utf8_bin "
					+ "select TERM_NAME, TOPIC_ID, PR_TERM_GIVEN_TOPIC from TERM_TOPIC join TERM using (TERM_ID) "
					+ "where TOPIC_ID=0 AND WORDTYPE='SUBS'	order by PR_TERM_GIVEN_TOPIC desc limit 20;");
			database.executeUpdateQuery("alter table TopTerms add index (TOPIC_ID,TERM_NAME)");
			database.executeUpdateQuery("alter table TopTerms add index (TERM_NAME)");
			int numTopics = Integer.parseInt((String) properties.get("malletNumTopics"));

			// best 20 nouns of best 20 topics
			for (int i = 1; i < numTopics; i++) {
				database.executeUpdateQueryForUpdate("insert into TopTerms "
						+ "select TERM_NAME, TOPIC_ID, PR_TERM_GIVEN_TOPIC from TERM_TOPIC join TERM "
						+ "using (TERM_ID) where TOPIC_ID=" + i
						+ " AND WORDTYPE='SUBS' order by PR_TERM_GIVEN_TOPIC desc limit 20;");
			}

			// best 20 verbs of best 20 topics
			for (int i = 0; i < numTopics; i++) {
				database.executeUpdateQueryForUpdate("insert into TopTerms "
						+ "select TERM_NAME, TOPIC_ID, PR_TERM_GIVEN_TOPIC from TERM_TOPIC join TERM "
						+ "using (TERM_ID) where TOPIC_ID=" + i
						+ " AND WORDTYPE='VERB' order by PR_TERM_GIVEN_TOPIC desc limit 20;");
			}
		} catch (SQLException e) {
			logger.error("Exception while handling temporary table TopTerms.");
			throw new RuntimeException(e);
		}
	}

	private void createAndFillTableTopTermsDocSameTopic() {
		try {
			database.executeUpdateQuery("create table TOP_TERMS_DOC_SAME_TOPIC ENGINE = InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin "
					+ "select DOCUMENT_TERM_TOPIC.DOCUMENT_ID, TopTerms.TOPIC_ID, "
					+ "DOCUMENT_TERM_TOPIC.POSITION_OF_TOKEN_IN_DOCUMENT, DOCUMENT_TERM_TOPIC.TERM, DOCUMENT_TERM_TOPIC.WORDTYPE$WORDTYPE "
					+ "from DOCUMENT_TERM_TOPIC join TopTerms on (DOCUMENT_TERM_TOPIC.TERM=TopTerms.TERM_NAME and DOCUMENT_TERM_TOPIC.TOPIC_ID=TopTerms.TOPIC_ID) "
					+ "order by DOCUMENT_TERM_TOPIC.DOCUMENT_ID asc, TopTerms.TOPIC_ID asc,	DOCUMENT_TERM_TOPIC.POSITION_OF_TOKEN_IN_DOCUMENT asc");
		} catch (SQLException e) {
			logger.error("Exception while handling temporary table TOP_TERMS_DOC_SAME_TOPIC.");
			throw new RuntimeException(e);
		}
	}

	private void fillTableFrames() {
		Collection<Frame> frames = Lists.newArrayList();

		try {
			ResultSet topTerms = database
					.executeQuery("SELECT * FROM TOP_TERMS_DOC_SAME_TOPIC order by DOCUMENT_ID asc, TOPIC_ID asc, POSITION_OF_TOKEN_IN_DOCUMENT asc");

			int documentId = 0;
			int position = 0;
			int topicId = 0;
			String term = null, wordType = null;
			int endPos;
			while (topTerms.next()) {
				if (documentId != topTerms.getInt("DOCUMENT_ID") || topicId != topTerms.getInt("TOPIC_ID")
						|| topTerms.getInt("POSITION_OF_TOKEN_IN_DOCUMENT") - position > 150
						|| topTerms.getString("WORDTYPE$WORDTYPE").equals("SUBS")) {
					documentId = topTerms.getInt("DOCUMENT_ID");
					topicId = topTerms.getInt("TOPIC_ID");
					position = topTerms.getInt("POSITION_OF_TOKEN_IN_DOCUMENT");
					term = topTerms.getString("TERM");
					wordType = topTerms.getString("WORDTYPE$WORDTYPE");
				} else {
					if (wordType.equals("SUBS")) {
						endPos = topTerms.getInt("POSITION_OF_TOKEN_IN_DOCUMENT") + topTerms.getString("TERM").length();
						frames.add(new Frame(documentId, topicId, term, topTerms.getString("TERM"), position, endPos));
					}
					position = -150;
				}
			}

			for (Frame frame : frames) {
				database.executeUpdateQueryForUpdate(String
						.format("INSERT INTO %s (DOCUMENT_ID, TOPIC_ID, FRAME, START_POSITION, END_POSITION) VALUES (%d, %d, \"%s, %s\", %d, %d)",
								tableName, frame.getDocumentId(), frame.getTopicId(), frame.getTermNoun(),
								frame.getTermVerb(), frame.getPosNoun(), frame.getPosVerb()));
			}

			// Set inactive
			database.executeUpdateQuery("UPDATE " + this.tableName
					+ ", (SELECT DISTINCT DOCUMENT_ID,TOPIC_ID,FRAME,START_POSITION"
					+ " FROM FRAMES JOIN DOCUMENT USING (DOCUMENT_ID)," + "(SELECT '%。%' PATTERN FROM DUAL "
					+ "UNION ALL SELECT '%？%' PATTERN FROM DUAL " + "UNION ALL SELECT '%?%' PATTERN FROM DUAL "
					+ "UNION ALL SELECT '%！%' PATTERN FROM DUAL " + "UNION ALL SELECT '%!%' PATTERN FROM DUAL "
					+ "UNION ALL SELECT '%．．%' PATTERN FROM DUAL " + "UNION ALL SELECT '%..%' PATTERN FROM DUAL "
					+ "UNION ALL SELECT '%・・%' PATTERN FROM DUAL " + "UNION ALL SELECT '%．．．%' PATTERN FROM DUAL "
					+ "UNION ALL SELECT '%...%' PATTERN FROM DUAL " + "UNION ALL SELECT '%…%' PATTERN FROM DUAL "
					+ "UNION ALL SELECT '%・・・%' PATTERN FROM DUAL) X "
					+ "WHERE SUBSTRING(TEXT$FULLTEXT,START_POSITION+1,END_POSITION-START_POSITION) LIKE X.PATTERN) Y "
					+ "SET " + this.tableName + ".ACTIVE=0 WHERE Y.DOCUMENT_ID=" + this.tableName + ".DOCUMENT_ID AND "
					+ "Y.TOPIC_ID=" + this.tableName + ".TOPIC_ID AND " + "Y.FRAME=" + this.tableName + ".FRAME AND "
					+ "Y.START_POSITION=" + this.tableName + ".START_POSITION");
		} catch (SQLException e) {
			logger.error("Table could not be filled properly.");
			throw new RuntimeException(e);
		}
		bestFrames();
	}

	private void bestFrames() {
		int numTopics = Integer.parseInt((String) properties.get("malletNumTopics"));
		try {
			database.executeUpdateQuery("DROP TABLE IF EXISTS BEST_FRAMES");
			for (int i = 0; i < numTopics; i++) {
				if (i == 0) {
					database.executeUpdateQueryForUpdate("CREATE TABLE BEST_FRAMES AS SELECT FRAME, TOPIC_ID, COUNT(DISTINCT DOCUMENT_ID) AS FRAME_COUNT FROM FRAMES WHERE TOPIC_ID="
							+ i + " GROUP BY FRAME ORDER BY FRAME_COUNT DESC LIMIT 10");
				} else {
					database.executeUpdateQueryForUpdate("INSERT INTO BEST_FRAMES SELECT FRAME, TOPIC_ID, COUNT(DISTINCT DOCUMENT_ID) AS FRAME_COUNT FROM FRAMES WHERE TOPIC_ID="
							+ i + " GROUP BY FRAME ORDER BY FRAME_COUNT DESC LIMIT 10");
				}
			}
		} catch (SQLException e) {
			logger.error("Exception while creating bestFrames table.");
			throw new RuntimeException(e);
		}
	}

	private void dropTemporaryTablesAndColumns() {
		try {
			database.executeUpdateQuery("alter table TERM drop column WORDTYPE");
			database.dropTable("TopTerms");
			database.dropTable("TOP_TERMS_DOC_SAME_TOPIC");
		} catch (SQLException e) {
			logger.warn("At least one temporarely created table or column could not be dropped.", e);
		}
	}

	private static class Frame {

		private final int documentId;
		private final int topicId;
		private final String termNoun;
		private final String termVerb;
		private final int positionOfNounInDocument;
		private final int positionOfVerbInDocument;

		public Frame(int docId, int topic, String noun, String term, int startPos, int endPos) {
			documentId = docId;
			topicId = topic;
			termNoun = noun;
			termVerb = term;
			positionOfNounInDocument = startPos;
			positionOfVerbInDocument = endPos;
		}

		public int getDocumentId() {
			return this.documentId;
		}

		public int getTopicId() {
			return this.topicId;
		}

		public String getTermNoun() {
			return this.termNoun;
		}

		public String getTermVerb() {
			return this.termVerb;
		}

		public int getPosNoun() {
			return this.positionOfNounInDocument;
		}

		public int getPosVerb() {
			return this.positionOfVerbInDocument;
		}
	}

}
