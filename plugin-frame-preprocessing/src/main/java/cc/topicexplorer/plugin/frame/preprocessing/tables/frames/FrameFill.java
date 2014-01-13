package cc.topicexplorer.plugin.frame.preprocessing.tables.frames;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import cc.topicexplorer.chain.commands.TableFillCommand;

import com.google.common.collect.Lists;

public final class FrameFill extends TableFillCommand {

	@Override
	public void setTableName() {
		this.tableName = "FRAMES";
	}

	@Override
	public void addDependencies() {
		this.beforeDependencies.add("TermFill");
		this.beforeDependencies.add("TermTopicFill");
		this.beforeDependencies.add("DocumentTermTopicFill");
		this.beforeDependencies.add("FrameCreate");
	}

	@Override
	public void fillTable() {
		alterAndFillTableTerm();
		createAndFillTableTopTerms();
		createAndFillTableTopTermsDocSameTopic();
		fillTableFrames();
		dropTemporaryTablesAndColumns();
		this.logger.info(String.format("Table %s is filled.", this.tableName));
	}

	private void alterAndFillTableTerm() {
		try {
			// add wordtype column to TERM table
			// and fetch values from another table
			this.database
					.executeUpdateQueryForUpdate("alter table TERM add column WORDTYPE VARCHAR(255) COLLATE utf8_bin");
			this.database
					.executeUpdateQueryForUpdate("update TERM, DOCUMENT_TERM_TOPIC "
							+ "SET TERM.WORDTYPE=DOCUMENT_TERM_TOPIC.WORDTYPE$WORDTYPE WHERE DOCUMENT_TERM_TOPIC.TERM=TERM.TERM_NAME");
		} catch (SQLException e) {
			this.logger.error("Table TERM could not be altered.");
			throw new RuntimeException(e);
		}
	}

	private void createAndFillTableTopTerms() {
		try {
			this.database
					.executeUpdateQuery("create table TopTerms ENGINE = MEMORY DEFAULT CHARSET=utf8 COLLATE=utf8_bin "
							+ "select TERM_NAME, TOPIC_ID, PR_TERM_GIVEN_TOPIC from TERM_TOPIC join TERM using (TERM_ID) "
							+ "where TOPIC_ID=0 AND WORDTYPE='SUBS'	order by PR_TERM_GIVEN_TOPIC desc limit 20;");
			this.database.executeUpdateQuery("alter table TopTerms add index (TOPIC_ID,TERM_NAME)");
			this.database.executeUpdateQuery("alter table TopTerms add index (TERM_NAME)");

			// best 20 nouns of best 20 topics
			for (int i = 1; i < 20; i++) {
				this.database.executeUpdateQueryForUpdate("insert into TopTerms "
						+ "select TERM_NAME, TOPIC_ID, PR_TERM_GIVEN_TOPIC from TERM_TOPIC join TERM "
						+ "using (TERM_ID) where TOPIC_ID=" + i
						+ " AND WORDTYPE='SUBS' order by PR_TERM_GIVEN_TOPIC desc limit 20;");
			}

			// best 20 verbs of best 20 topics
			for (int i = 0; i < 20; i++) {
				this.database.executeUpdateQueryForUpdate("insert into TopTerms "
						+ "select TERM_NAME, TOPIC_ID, PR_TERM_GIVEN_TOPIC from TERM_TOPIC join TERM "
						+ "using (TERM_ID) where TOPIC_ID=" + i
						+ " AND WORDTYPE='VERB' order by PR_TERM_GIVEN_TOPIC desc limit 20;");
			}
		} catch (SQLException e) {
			this.logger.error("Exception while handling temporary table TopTerms.");
			throw new RuntimeException(e);
		}
	}

	private void createAndFillTableTopTermsDocSameTopic() {
		try {
			this.database
					.executeUpdateQuery("create table TOP_TERMS_DOC_SAME_TOPIC ENGINE = InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin "
							+ "select DOCUMENT_TERM_TOPIC.DOCUMENT_ID, TopTerms.TOPIC_ID, "
							+ "DOCUMENT_TERM_TOPIC.POSITION_OF_TOKEN_IN_DOCUMENT, DOCUMENT_TERM_TOPIC.TERM, DOCUMENT_TERM_TOPIC.WORDTYPE$WORDTYPE "
							+ "from DOCUMENT_TERM_TOPIC join TopTerms on (DOCUMENT_TERM_TOPIC.TERM=TopTerms.TERM_NAME and DOCUMENT_TERM_TOPIC.TOPIC_ID=TopTerms.TOPIC_ID) "
							+ "order by DOCUMENT_TERM_TOPIC.DOCUMENT_ID asc, TopTerms.TOPIC_ID asc,	DOCUMENT_TERM_TOPIC.POSITION_OF_TOKEN_IN_DOCUMENT asc");
		} catch (SQLException e) {
			this.logger.error("Exception while handling temporary table TOP_TERMS_DOC_SAME_TOPIC.");
			throw new RuntimeException(e);
		}
	}

	private void fillTableFrames() {
		Collection<Frame> frames = Lists.newArrayList();

		try {
			ResultSet rs = this.database
					.executeQuery("SELECT * FROM TOP_TERMS_DOC_SAME_TOPIC order by DOCUMENT_ID asc, TOPIC_ID asc, POSITION_OF_TOKEN_IN_DOCUMENT asc");

			while (rs.next()) { // look for the first SUBS
				if (rs.getString("WORDTYPE$WORDTYPE").equals("SUBS")) {
					appendFramesOfRemainingResultSet(rs, frames, rs.getInt("POSITION_OF_TOKEN_IN_DOCUMENT"));
				}
			}

			// AUSGABE START
			for (Frame frame : frames) {
				this.database
						.executeUpdateQueryForUpdate(String
								.format("INSERT INTO %s (DOCUMENT_ID, TOPIC_ID, FRAME, START_POSITION, END_POSITION) VALUES (%d, %d, \"%s, %s\", %d, %d)",
										this.tableName, frame.getDocId(), frame.getTopicId(), frame.getTermSubs(),
										frame.getTermVerb(), frame.getPosSubs(), frame.getPosVerb()));
			}
			// AUSGABE ENDE

		} catch (SQLException e) {
			this.logger.error("Table could not be filled properly.");
			throw new RuntimeException(e);
		}
	}

	private void appendFramesOfRemainingResultSet(ResultSet rs, Collection<Frame> frames, int posSubs) {

		try {
			int docIdSubs = rs.getInt("DOCUMENT_ID");
			int topicIdSubs = rs.getInt("TOPIC_ID");
			String termSubs = rs.getString("TERM");
			boolean frameFound = false;

			while (rs.next()) {

				String wordTypeTmp = rs.getString("WORDTYPE$WORDTYPE");
				int posTmp = rs.getInt("POSITION_OF_TOKEN_IN_DOCUMENT");

				if (wordTypeTmp.equals("SUBS")) {
					appendFramesOfRemainingResultSet(rs, frames, posTmp);
				} else if (!frameFound && wordTypeTmp.equals("VERB") && rs.getInt("DOCUMENT_ID") == docIdSubs
						&& rs.getInt("TOPIC_ID") == topicIdSubs && posTmp - posSubs <= 150) {
					frameFound = true;
					frames.add(new Frame(docIdSubs, topicIdSubs, termSubs, rs.getString("TERM"), posSubs, posTmp));
				}

			}
		} catch (SQLException e) {
			this.logger.error("Exception in handling the current resultset.");
			throw new RuntimeException(e);
		}
	}

	private class Frame {
		private final int docId;
		private final int topicId;
		private final String termSubs;
		private final String termVerb;
		private final int posSubs;
		private final int posVerb;

		public Frame(int docId, int topicId, String termSubs, String termVerb, int posSubs, int posVerb) {
			this.docId = docId;
			this.topicId = topicId;
			this.termSubs = termSubs;
			this.termVerb = termVerb;
			this.posSubs = posSubs;
			this.posVerb = posVerb;
		}

		public int getDocId() {
			return this.docId;
		}

		public int getTopicId() {
			return this.topicId;
		}

		public String getTermSubs() {
			return this.termSubs;
		}

		public String getTermVerb() {
			return this.termVerb;
		}

		public int getPosSubs() {
			return this.posSubs;
		}

		public int getPosVerb() {
			return this.posVerb;
		}
	}

	private void dropTemporaryTablesAndColumns() {
		try {
			this.database.executeUpdateQuery("alter table TERM drop column WORDTYPE");
			this.database.dropTable("TopTerms");
			this.database.dropTable("TOP_TERMS_DOC_SAME_TOPIC");
		} catch (SQLException e) {
			this.logger.warn("At least one temporarely created table or column could not be dropped.", e);
		}
	}
}
