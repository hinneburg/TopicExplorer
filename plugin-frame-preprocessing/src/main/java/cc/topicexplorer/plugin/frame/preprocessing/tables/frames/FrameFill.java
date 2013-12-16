package cc.topicexplorer.plugin.frame.preprocessing.tables.frames;

import java.sql.ResultSet;
import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableFillCommand;

public final class FrameFill extends TableFillCommand {

	@Override
	public void fillTable() throws SQLException {
		this.logger.info("Alter table TERM");
		this.alterAndFillTableTerm();
		this.logger.info("Create and fill table TopTerms");
		this.createAndFillTableTopTerms();
		this.logger.info("Create and fill table TOP_TERMS_DOC_SAME_TOPIC");
		this.createAndFillTableTopTermsDocSameTopic();
		this.logger.info("Fill table FRAMES");
		this.fillTableFrames();
		this.logger.info("Drop temporarily used tables and columns");
		this.dropTemporaryTablesAndColumns();
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
			this.database
					.executeUpdateQuery("alter table TopTerms add index (TOPIC_ID,TERM_NAME)");
			this.database
					.executeUpdateQuery("alter table TopTerms add index (TERM_NAME)");

			// TODO Ist das zweite insert vom ersten abhängig? Bzw. können die
			// beiden for-Schleifen zusammengelegt werden?

			// best 20 nouns of best 20 topics
			for (int i = 1; i < 20; i++) {
				this.database
						.executeUpdateQueryForUpdate("insert into TopTerms "
								+ "select TERM_NAME, TOPIC_ID, PR_TERM_GIVEN_TOPIC from TERM_TOPIC join TERM "
								+ "using (TERM_ID) where TOPIC_ID="
								+ i
								+ " AND WORDTYPE='SUBS' order by PR_TERM_GIVEN_TOPIC desc limit 20;");
			}

			// best 20 verbs of best 20 topics
			for (int i = 0; i < 20; i++) {
				this.database
						.executeUpdateQueryForUpdate("insert into TopTerms "
								+ "select TERM_NAME, TOPIC_ID, PR_TERM_GIVEN_TOPIC from TERM_TOPIC join TERM "
								+ "using (TERM_ID) where TOPIC_ID="
								+ i
								+ " AND WORDTYPE='VERB' order by PR_TERM_GIVEN_TOPIC desc limit 20;");
			}
		} catch (SQLException e) {
			this.logger
					.error("Exception while handling temporary table TopTerms.");
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
			this.logger
					.error("Exception while handling temporary table TOP_TERMS_DOC_SAME_TOPIC.");
			throw new RuntimeException(e);
		}
	}

	private void fillTableFrames() {
		int doc = 0;
		int topic = 0;
		int pos = -150;
		String wordType = "";
		int endPos;
		int docTmp = 0;
		int topicTmp = 0;
		int posTmp;
		String wordTypeTmp = "";
		String term1 = "";
		String term2;
		
		try {
			ResultSet rs = this.database
					.executeQuery("SELECT * FROM TOP_TERMS_DOC_SAME_TOPIC");
			while (rs.next()) {
				docTmp = rs.getInt("DOCUMENT_ID");
				topicTmp = rs.getInt("TOPIC_ID");
				posTmp = rs.getInt("POSITION_OF_TOKEN_IN_DOCUMENT");
				wordTypeTmp = rs.getString("WORDTYPE$WORDTYPE");

				// Abbruchkriterien: Wenn eins eintrifft, dann ist es kein Frame
				// (erst Subs, dann Verb)
				// Wenn, dann ist es Frame-Anfang und gleichzeitig Fensteranfang
				
				if (doc != docTmp || topic != topicTmp || posTmp - pos > 150
						|| wordTypeTmp.equals("SUBS")) {
					doc = docTmp;
					topic = topicTmp;
					pos = posTmp;
					wordType = wordTypeTmp;
					term1 = rs.getString("TERM");
				} else {
					if (wordType.equals("SUBS")) {
						term2 = rs.getString("TERM");
						
						endPos = rs.getInt("POSITION_OF_TOKEN_IN_DOCUMENT");
						
						this.database
								.executeUpdateQueryForUpdate("INSERT INTO "
										+ this.tableName
										+ " (DOCUMENT_ID, TOPIC_ID, "
										+ "FRAME, START_POSITION, END_POSITION) VALUES ("
										+ doc + ", " + topic + ", \"" + term1
										+ ", " + term2 + "\", " + pos + ", "
										+ endPos + ")");
					}
					pos = -150;
				}
			}

		} catch (SQLException e) {
			this.logger.error("Table " + this.tableName
					+ " could not be filled properly.");
			throw new RuntimeException(e);
		}
	}

	private void dropTemporaryTablesAndColumns() {
		try {
			this.database
					.executeUpdateQuery("alter table TERM drop column WORDTYPE");
			this.database.dropTable("TopTerms");
			this.database.dropTable("TOP_TERMS_DOC_SAME_TOPIC");
		} catch (SQLException e) {
			this.logger
					.error("At least one temporarely created table or column could not be dropped.");
			throw new RuntimeException(e);
		}
	}

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

}