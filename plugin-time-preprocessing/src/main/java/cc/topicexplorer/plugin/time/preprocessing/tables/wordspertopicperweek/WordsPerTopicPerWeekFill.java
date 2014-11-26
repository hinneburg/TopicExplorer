package cc.topicexplorer.plugin.time.preprocessing.tables.wordspertopicperweek;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;





/** MIT-JOOQ-START 
 import static jooq.generated.Tables.DOCUMENT_TERM_TOPIC;
 import static jooq.generated.Tables.TIME$WORDS_PER_TOPIC_PER_WEEK;
 import static jooq.generated.Tables.TOPIC;
 MIT-JOOQ-ENDE */
import cc.topicexplorer.commands.TableFillCommand;

import com.google.common.collect.Sets;

public class WordsPerTopicPerWeekFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(WordsPerTopicPerWeekFill.class);

	@Override
	public void fillTable() {
		if (timePluginIsActivated()) {
			this.deleteTempTables(true);
			/**
			 * MIT-JOOQ-START database.executeUpdateQuery("INSERT INTO " + TIME$WORDS_PER_TOPIC_PER_WEEK.getName() + "("
			 * + TIME$WORDS_PER_TOPIC_PER_WEEK.WEEK.getName() + ", " + TIME$WORDS_PER_TOPIC_PER_WEEK.TOPIC_ID.getName()
			 * + ", " + TIME$WORDS_PER_TOPIC_PER_WEEK.WORD_COUNT.getName() + ") SELECT " +
			 * DOCUMENT_TERM_TOPIC.TIME$WEEK.getName() + ", " + DOCUMENT_TERM_TOPIC.getName() + "." +
			 * DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + ", COUNT(*) AS count FROM " + DOCUMENT_TERM_TOPIC.getName() +
			 * ", " + TOPIC.getName() + " WHERE " + TOPIC.getName() + "." + TOPIC.TOPIC_ID.getName() + " = " +
			 * DOCUMENT_TERM_TOPIC.getName() + "." + DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + " GROUP BY " +
			 * DOCUMENT_TERM_TOPIC.TIME$WEEK.getName() + ", " + DOCUMENT_TERM_TOPIC.getName() + "." +
			 * DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() +";"); MIT-JOOQ-ENDE
			 */
			/** OHNE_JOOQ-START */
			// @formatter:off
			try {
				logger.info("starting");
				timeWeek();
				logger.info("TIME_WEEK done");
				timeTermWeek();
				logger.info("TIME_TERM_WEEK done");
				timeTopicWeek();
				logger.info("TIME_TOPIC_WEEK done");
				timeTermTopicWeek();
				logger.info("TIME_TERM_TOPIC_WEEK done");
				
				database.executeUpdateQuery("INSERT INTO "
						+ "TIME$WORDS_PER_TOPIC_PER_WEEK("
						+ "TIME$WORDS_PER_TOPIC_PER_WEEK.WEEK, "
						+ "TIME$WORDS_PER_TOPIC_PER_WEEK.TOPIC_ID, "
						+ "TIME$WORDS_PER_TOPIC_PER_WEEK.WORD_COUNT"
						+ ") SELECT DOCUMENT_TERM_TOPIC.TIME$WEEK, "
						+ "DOCUMENT_TERM_TOPIC.TOPIC_ID"
						+ ", COUNT(*) AS count FROM DOCUMENT_TERM_TOPIC"
						+ ", TOPIC WHERE TOPIC.TOPIC_ID = "
						+ "DOCUMENT_TERM_TOPIC.TOPIC_ID GROUP BY "
						+ "DOCUMENT_TERM_TOPIC.TIME$WEEK, "
						+ "DOCUMENT_TERM_TOPIC.TOPIC_ID;");
				
				if(Arrays.asList(properties.get("plugins").toString().split(",")).contains("hierarchicaltopic")) {
					database.executeUpdateQuery("INSERT INTO "
							+ "TIME$WORDS_PER_TOPIC_PER_WEEK("
							+ "TIME$WORDS_PER_TOPIC_PER_WEEK.WEEK, "
							+ "TIME$WORDS_PER_TOPIC_PER_WEEK.TOPIC_ID, "
							+ "TIME$WORDS_PER_TOPIC_PER_WEEK.WORD_COUNT"
							+ ") SELECT TIME$WORDS_PER_TOPIC_PER_WEEK.WEEK, "
							+ "t1.TOPIC_ID, "
							+ "SUM(TIME$WORDS_PER_TOPIC_PER_WEEK.WORD_COUNT) "
							+ "FROM "
							+ "TIME$WORDS_PER_TOPIC_PER_WEEK, TOPIC t1, TOPIC t2 "
							+ "WHERE t2.TOPIC_ID=TIME$WORDS_PER_TOPIC_PER_WEEK.TOPIC_ID AND "
							+ "t1.HIERARCHICAL_TOPIC$START <= t2.HIERARCHICAL_TOPIC$START AND "
							+ "t1.HIERARCHICAL_TOPIC$END >= t2.HIERARCHICAL_TOPIC$END AND "
							+ "t1.HIERARCHICAL_TOPIC$START < t1.HIERARCHICAL_TOPIC$END AND "
							+ "t2.HIERARCHICAL_TOPIC$START = t2.HIERARCHICAL_TOPIC$END "
							+ "GROUP BY t1.TOPIC_ID, WEEK");
				}
				
				logger.info("WORDS_PER_TOPIC_PER_WEEK done");
				
				updateBestWords();
				deleteTempTables(false);	
				logger.info("deleteing temps done");
				// @formatter:on
			} catch (SQLException e) {
				logger.error("Table " + this.tableName + " could not be filled properly.");
				throw new RuntimeException(e);
			}
			/** OHNE_JOOQ-ENDE */
		}

	}

	private boolean timePluginIsActivated() {
		return Boolean.parseBoolean(this.properties.getProperty("plugin_time"));
	}

	private void timeWeek() {
		try {
			database.executeUpdateQuery("create table TIME$WEEK (TIME$WEEK INTEGER(11) NOT NULL,"
					+ "TIME$NUMBER_OF_TOKEN_PER_WEEK int(11) NOT NULL) "
					+ "ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");
			database.executeUpdateQuery("insert into TIME$WEEK (TIME$WEEK,TIME$NUMBER_OF_TOKEN_PER_WEEK)"
					+ "select TIME$WEEK,count(*) TIME$NUMBER_OF_TOKEN_PER_WEEK "
					+ "from DOCUMENT_TERM_TOPIC join TERM on (TERM.TERM_NAME=DOCUMENT_TERM_TOPIC.TERM) "
					+ "group by TIME$WEEK;");
		} catch (SQLException e) {
			logger.error("Table TIME$WEEK could not be created properly.");
			throw new RuntimeException(e);
		}
	}

	private void timeTermWeek() {
		try {
			database.executeUpdateQuery("create table TIME$TERM_WEEK ("
					+ "TERM_NAME varchar(255) COLLATE utf8_bin NOT NULL, TERM_ID INTEGER(11) NOT NULL, "
					+ "TIME$WEEK INTEGER(11) NOT NULL, TIME$NUMBER_OF_TOKEN_TERM_PER_WEEK int(11) NOT NULL, "
					+ "TIME$PR_TERM_GIVEN_WEEK DOUBLE NOT NULL, TIME$PR_WEEK_GIVEN_TERM DOUBLE) "
					+ "ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");
			database.executeUpdateQuery("insert into TIME$TERM_WEEK (TERM_NAME, TERM_ID, "
					+ "TIME$WEEK, TIME$NUMBER_OF_TOKEN_TERM_PER_WEEK, TIME$PR_TERM_GIVEN_WEEK) "
					+ "select TERM.TERM_NAME, TERM.TERM_ID, X.TIME$WEEK, "
					+ "X.NUMBER_OF_TOKEN_TERM_PER_WEEK, "
					+ "cast(X.NUMBER_OF_TOKEN_TERM_PER_WEEK AS DECIMAL(65,30)) / "
					+ "cast(TIME$WEEK.TIME$NUMBER_OF_TOKEN_PER_WEEK AS DECIMAL(65,30)) "
					+ "as PR_TERM_GIVEN_WEEK from  ("
					+ "select TERM, TIME$WEEK, count(*) as NUMBER_OF_TOKEN_TERM_PER_WEEK "
					+ "from DOCUMENT_TERM_TOPIC group by TERM, TIME$WEEK ) X  join TERM  "
					+ "on (X.TERM=TERM.TERM_NAME) join TIME$WEEK on (X.TIME$WEEK=TIME$WEEK.TIME$WEEK);");
			database.executeUpdateQuery("create index TIME$TERM_WEEK$TERM_WEEK_IDX ON "
					+ "TIME$TERM_WEEK(TERM_NAME,TIME$WEEK,TIME$NUMBER_OF_TOKEN_TERM_PER_WEEK) USING BTREE");
		} catch (SQLException e) {
			logger.error("Table TIME$TERM_WEEK could not be created properly.");
			throw new RuntimeException(e);
		}
	}

	private void timeTopicWeek() {
		try {
			database.executeUpdateQuery("create table TIME$TOPIC_WEEK (TOPIC_ID INTEGER(11) NOT NULL, "
					+ "TIME$WEEK INTEGER(11) NOT NULL, TIME$NUMBER_OF_TOKEN_TOPIC_PER_WEEK int(11) NOT NULL, "
					+ "TIME$PR_TOPIC_GIVEN_WEEK DOUBLE NOT NULL,  TIME$PR_WEEK_GIVEN_TOPIC DOUBLE) "
					+ "ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");
			if(Arrays.asList(properties.get("plugins").toString().split(",")).contains("hierarchicaltopic")) {
				database.executeUpdateQuery("insert into TIME$TOPIC_WEEK (TOPIC_ID, TIME$WEEK, "
						+ "TIME$NUMBER_OF_TOKEN_TOPIC_PER_WEEK, TIME$PR_TOPIC_GIVEN_WEEK, "
						+ "TIME$PR_WEEK_GIVEN_TOPIC) select X.TOPIC_ID, X.TIME$WEEK, "
						+ "X.NUMBER_OF_TOKEN_TOPIC_PER_WEEK, "
						+ "cast(X.NUMBER_OF_TOKEN_TOPIC_PER_WEEK AS DECIMAL(65,30)) /  "
						+ "cast(TIME$WEEK.TIME$NUMBER_OF_TOKEN_PER_WEEK AS DECIMAL(65,30))  "
						+ "as TIME$PR_TOPIC_GIVEN_WEEK, "
						+ "cast(X.NUMBER_OF_TOKEN_TOPIC_PER_WEEK AS DECIMAL(65,30)) /  "
						+ "cast(TOPIC.NUMBER_OF_TOKENS AS DECIMAL(65,30)) as TIME$PR_WEEK_GIVEN_TOPIC "
						+ "from (select t1.TOPIC_ID, TIME$WEEK, count(*) as NUMBER_OF_TOKEN_TOPIC_PER_WEEK "
						+ "from DOCUMENT_TERM_TOPIC, TOPIC t1, TOPIC t2  WHERE DOCUMENT_TERM_TOPIC.TOPIC_ID=t2.TOPIC_ID AND "
						+ "t1.HIERARCHICAL_TOPIC$START <= t2.HIERARCHICAL_TOPIC$START AND "
						+ "t1.HIERARCHICAL_TOPIC$END >= t2.HIERARCHICAL_TOPIC$END AND "
						+ "t2.HIERARCHICAL_TOPIC$START = t2.HIERARCHICAL_TOPIC$END "
						+ "group by t1.TOPIC_ID, TIME$WEEK) X  join "
						+ "TOPIC on (X.TOPIC_ID=TOPIC.TOPIC_ID) join TIME$WEEK "
						+ "on (X.TIME$WEEK=TIME$WEEK.TIME$WEEK);");
			} else {
				database.executeUpdateQuery("insert into TIME$TOPIC_WEEK (TOPIC_ID, TIME$WEEK, "
						+ "TIME$NUMBER_OF_TOKEN_TOPIC_PER_WEEK, TIME$PR_TOPIC_GIVEN_WEEK, "
						+ "TIME$PR_WEEK_GIVEN_TOPIC) select X.TOPIC_ID, X.TIME$WEEK, "
						+ "X.NUMBER_OF_TOKEN_TOPIC_PER_WEEK, "
						+ "cast(X.NUMBER_OF_TOKEN_TOPIC_PER_WEEK AS DECIMAL(65,30)) /  "
						+ "cast(TIME$WEEK.TIME$NUMBER_OF_TOKEN_PER_WEEK AS DECIMAL(65,30))  "
						+ "as TIME$PR_TOPIC_GIVEN_WEEK, "
						+ "cast(X.NUMBER_OF_TOKEN_TOPIC_PER_WEEK AS DECIMAL(65,30)) /  "
						+ "cast(TOPIC.NUMBER_OF_TOKENS AS DECIMAL(65,30)) as TIME$PR_WEEK_GIVEN_TOPIC "
						+ "from (select TOPIC_ID, TIME$WEEK, count(*) as NUMBER_OF_TOKEN_TOPIC_PER_WEEK "
						+ "from DOCUMENT_TERM_TOPIC group by TOPIC_ID, TIME$WEEK) X  join "
						+ "TOPIC on (X.TOPIC_ID=TOPIC.TOPIC_ID) join TIME$WEEK "
						+ "on (X.TIME$WEEK=TIME$WEEK.TIME$WEEK);");
			}
			database.executeUpdateQuery("create index TIME$TOPIC_WEEK$TOPIC_WEEK_IDX "
					+ "ON  TIME$TOPIC_WEEK(TOPIC_ID,TIME$WEEK,TIME$NUMBER_OF_TOKEN_TOPIC_PER_WEEK) " + "USING BTREE");

		} catch (SQLException e) {
			logger.error("Table TIME$TOPIC_WEEK could not be created properly.");
			throw new RuntimeException(e);
		}
	}

	private void timeTermTopicWeek() {
		try {
			database.executeUpdateQuery("create table TIME$TERM_TOPIC_WEEK ( "
					+ "TERM_NAME varchar(255) COLLATE utf8_bin NOT NULL,  " + "TERM_ID INTEGER(11) NOT NULL,  "
					+ "TOPIC_ID INTEGER(11) NOT NULL, " + "TIME$WEEK INTEGER(11) NOT NULL, "
					+ "TIME$NUMBER_OF_TOKEN_AND_TOPIC_PER_WEEK int(11) NOT NULL,  "
					+ "TIME$PR_TOPIC_GIVEN_TERM_WEEK DOUBLE NOT NULL,  " + "TIME$PR_TERM_GIVEN_TOPIC_WEEK DOUBLE)  "
					+ "ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");
			if(Arrays.asList(properties.get("plugins").toString().split(",")).contains("hierarchicaltopic")) {
				database.executeUpdateQuery("insert into TIME$TERM_TOPIC_WEEK ("
						+ "TOPIC_ID,  "
						+ "TERM_NAME, "
						+ "TERM_ID, "
						+ "TIME$WEEK, "
						+ "TIME$NUMBER_OF_TOKEN_AND_TOPIC_PER_WEEK, "
						+ "TIME$PR_TOPIC_GIVEN_TERM_WEEK, "
						+ "TIME$PR_TERM_GIVEN_TOPIC_WEEK)  "
						+ "select X.TOPIC_ID,TIME$TERM_WEEK.TERM_NAME,TIME$TERM_WEEK.TERM_ID,X.TIME$WEEK,"
						+ "X.TIME$NUMBER_OF_TOKEN_AND_TOPIC_PER_WEEK,"
						+ "cast(X.TIME$NUMBER_OF_TOKEN_AND_TOPIC_PER_WEEK AS DECIMAL(65,30))/ "
						+ "cast(TIME$TERM_WEEK.TIME$NUMBER_OF_TOKEN_TERM_PER_WEEK AS DECIMAL(65,30)) as TIME$PR_TOPIC_GIVEN_TERM_WEEK,"
						+ "cast(X.TIME$NUMBER_OF_TOKEN_AND_TOPIC_PER_WEEK AS DECIMAL(65,30))/ "
						+ "cast(TIME$TOPIC_WEEK.TIME$NUMBER_OF_TOKEN_TOPIC_PER_WEEK AS DECIMAL(65,30)) as TIME$PR_TERM_GIVEN_TOPIC_WEEK "
						+ "from (select DOCUMENT_TERM_TOPIC.TERM, t1.TOPIC_ID, "
						+ "DOCUMENT_TERM_TOPIC.TIME$WEEK, count(*) TIME$NUMBER_OF_TOKEN_AND_TOPIC_PER_WEEK "
						+ "from DOCUMENT_TERM_TOPIC, TOPIC t1, TOPIC t2  WHERE DOCUMENT_TERM_TOPIC.TOPIC_ID=t2.TOPIC_ID AND "
						+ "t1.HIERARCHICAL_TOPIC$START <= t2.HIERARCHICAL_TOPIC$START AND "
						+ "t1.HIERARCHICAL_TOPIC$END >= t2.HIERARCHICAL_TOPIC$END AND "
						+ "t2.HIERARCHICAL_TOPIC$START = t2.HIERARCHICAL_TOPIC$END "
						+ "group by DOCUMENT_TERM_TOPIC.TERM, t1.TOPIC_ID, TIME$WEEK) X join TIME$TERM_WEEK "
						+ "on (X.TERM=TIME$TERM_WEEK.TERM_NAME and X.TIME$WEEK=TIME$TERM_WEEK.TIME$WEEK) "
						+ "join TIME$TOPIC_WEEK  "
						+ "on (X.TOPIC_ID=TIME$TOPIC_WEEK.TOPIC_ID and X.TIME$WEEK=TIME$TOPIC_WEEK.TIME$WEEK) ;");
			}else {
				database.executeUpdateQuery("insert into TIME$TERM_TOPIC_WEEK ("
						+ "TOPIC_ID,  "
						+ "TERM_NAME, "
						+ "TERM_ID, "
						+ "TIME$WEEK, "
						+ "TIME$NUMBER_OF_TOKEN_AND_TOPIC_PER_WEEK, "
						+ "TIME$PR_TOPIC_GIVEN_TERM_WEEK, "
						+ "TIME$PR_TERM_GIVEN_TOPIC_WEEK)  "
						+ "select X.TOPIC_ID,TIME$TERM_WEEK.TERM_NAME,TIME$TERM_WEEK.TERM_ID,X.TIME$WEEK,"
						+ "X.TIME$NUMBER_OF_TOKEN_AND_TOPIC_PER_WEEK,"
						+ "cast(X.TIME$NUMBER_OF_TOKEN_AND_TOPIC_PER_WEEK AS DECIMAL(65,30))/ "
						+ "cast(TIME$TERM_WEEK.TIME$NUMBER_OF_TOKEN_TERM_PER_WEEK AS DECIMAL(65,30)) as TIME$PR_TOPIC_GIVEN_TERM_WEEK,"
						+ "cast(X.TIME$NUMBER_OF_TOKEN_AND_TOPIC_PER_WEEK AS DECIMAL(65,30))/ "
						+ "cast(TIME$TOPIC_WEEK.TIME$NUMBER_OF_TOKEN_TOPIC_PER_WEEK AS DECIMAL(65,30)) as TIME$PR_TERM_GIVEN_TOPIC_WEEK "
						+ "from (select DOCUMENT_TERM_TOPIC.TERM, DOCUMENT_TERM_TOPIC.TOPIC_ID, "
						+ "DOCUMENT_TERM_TOPIC.TIME$WEEK, count(*) TIME$NUMBER_OF_TOKEN_AND_TOPIC_PER_WEEK "
						+ "from DOCUMENT_TERM_TOPIC group by DOCUMENT_TERM_TOPIC.TERM, "
						+ "DOCUMENT_TERM_TOPIC.TOPIC_ID, DOCUMENT_TERM_TOPIC.TIME$WEEK) X join TIME$TERM_WEEK "
						+ "on (X.TERM=TIME$TERM_WEEK.TERM_NAME and X.TIME$WEEK=TIME$TERM_WEEK.TIME$WEEK) "
						+ "join TIME$TOPIC_WEEK  "
						+ "on (X.TOPIC_ID=TIME$TOPIC_WEEK.TOPIC_ID and X.TIME$WEEK=TIME$TOPIC_WEEK.TIME$WEEK) ;");
			}
		} catch (SQLException e) {
			logger.error("Table TIME$TOPIC_TERM_WEEK could not be created properly.");
			throw new RuntimeException(e);
		}
	}

	private void updateBestWords() {
		try {
			ResultSet bestWordsQueryRS;
			database.executeUpdateQuery("ALTER TABLE `TIME$WORDS_PER_TOPIC_PER_WEEK`"
					+ " ADD `BEST_WORDS` TEXT NOT NULL");
		
			Statement stmt = database.getConnection().createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(Integer.MIN_VALUE);	
			bestWordsQueryRS = stmt.executeQuery("select distinct TERM_NAME,"
					+ "TOPIC_ID,TIME$WEEK,TIME$PR_TERM_GIVEN_TOPIC_WEEK from TIME$TERM_TOPIC_WEEK "
					+ "order by TOPIC_ID,TIME$WEEK,TIME$PR_TERM_GIVEN_TOPIC_WEEK desc");

			int topicId = -1;
			int count = 0;
			int week = 0;
			String bestWord = new String("");
			List<String> updateQueries = new ArrayList<String>();

			while (bestWordsQueryRS.next()) {
				if (topicId == -1) {
					topicId = bestWordsQueryRS.getInt("TOPIC_ID");
					week = bestWordsQueryRS.getInt("TIME$WEEK");
				}

				if (bestWordsQueryRS.getInt("TOPIC_ID") == topicId && week == bestWordsQueryRS.getInt("TIME$WEEK")) {
					if (count == 0) {
						bestWord = bestWordsQueryRS.getString("TERM_NAME");
					} else if (count < 4) {
						bestWord += "," + bestWordsQueryRS.getString("TERM_NAME");
					}
					count++;
				} else {
					updateQueries.add("UPDATE TIME$WORDS_PER_TOPIC_PER_WEEK SET BEST_WORDS='" + bestWord
							+ "' WHERE TOPIC_ID=" + topicId + " AND WEEK=" + week);
					topicId = bestWordsQueryRS.getInt("TOPIC_ID");
					week = bestWordsQueryRS.getInt("TIME$WEEK");
					bestWord = bestWordsQueryRS.getString("TERM_NAME");
					count = 1;

				}
			}
			logger.info("updates for WORDS_PER_TOPIC_PER_WEEK created");
			updateQueries.add("UPDATE TIME$WORDS_PER_TOPIC_PER_WEEK SET BEST_WORDS='" + bestWord + "' WHERE TOPIC_ID="
					+ topicId + " AND WEEK=" + week);
			for (String updateQuery : updateQueries) {
				database.executeUpdateQuery(updateQuery);
			}
			logger.info("updates for WORDS_PER_TOPIC_PER_WEEK done");
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be updated properly.");
			throw new RuntimeException(e);
		}
	}

	private void deleteTempTables(boolean optional) {
		try {
			String queryAdd = "";
			if(optional) {
				queryAdd = "IF EXISTS ";
			}
			database.executeUpdateQuery("DROP TABLE " + queryAdd + "TIME$WEEK");
			database.executeUpdateQuery("DROP TABLE " + queryAdd + "TIME$TERM_WEEK");
			database.executeUpdateQuery("DROP TABLE " + queryAdd + "TIME$TOPIC_WEEK");
			database.executeUpdateQuery("DROP TABLE " + queryAdd + "TIME$TERM_TOPIC_WEEK");
		} catch (SQLException e) {
			if(!optional) {
				logger.error("Error deleting temporary tables");
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void setTableName() {
		/**
		 * MIT-JOOQ-START this.tableName = TIME$WORDS_PER_TOPIC_PER_WEEK.getName(); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		this.tableName = "TIME$WORDS_PER_TOPIC_PER_WEEK";
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets
				.newHashSet("TermFill", "TopicFill", "Time_DocumentTermTopicFill", "Time_WordsPerTopicPerWeekCreate");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet("HierarchicalTopic_TopicFill");
	}

}
