package cc.topicexplorer.plugin.time.preprocessing.tables.wordspertopicperweek;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/** MIT-JOOQ-START 
 import static jooq.generated.Tables.DOCUMENT_TERM_TOPIC;
 import static jooq.generated.Tables.TIME$WORDS_PER_TOPIC_PER_WEEK;
 import static jooq.generated.Tables.TOPIC;
 MIT-JOOQ-ENDE */
import cc.topicexplorer.commands.TableFillCommand;

public class WordsPerTopicPerWeekFill extends TableFillCommand {
	private void timeWeek() {
		try {
			database.executeUpdateQuery("create table TIME$WEEK ("
					+ "TIME$WEEK INTEGER(11) NOT NULL,"
					+ "TIME$NUMBER_OF_TOKEN_PER_WEEK int(11) NOT NULL) "
					+ "ENGINE=MEMORY DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");
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
			database.executeUpdateQuery("create table TIME$TERM_WEEK ( "
					+ "TERM_NAME varchar(255) COLLATE utf8_bin NOT NULL,  "
					+ "TERM_ID INTEGER(11) NOT NULL,  "
					+ "TIME$WEEK INTEGER(11) NOT NULL, "
					+ "TIME$NUMBER_OF_TOKEN_TERM_PER_WEEK int(11) NOT NULL,  "
					+ "TIME$PR_TERM_GIVEN_WEEK DOUBLE NOT NULL,  "
					+ "TIME$PR_WEEK_GIVEN_TERM DOUBLE)  "
					+ "ENGINE=MEMORY DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");
			database.executeUpdateQuery("insert into TIME$TERM_WEEK "
					+ "( "
					+ "TERM_NAME, "
					+ "TERM_ID, "
					+ "TIME$WEEK, "
					+ "TIME$NUMBER_OF_TOKEN_TERM_PER_WEEK, "
					+ "TIME$PR_TERM_GIVEN_WEEK "
					+ ") "
					+ "select  "
					+ "TERM.TERM_NAME, "
					+ "TERM.TERM_ID, "
					+ "X.TIME$WEEK, "
					+ "X.NUMBER_OF_TOKEN_TERM_PER_WEEK, "
					+ "cast(X.NUMBER_OF_TOKEN_TERM_PER_WEEK AS DECIMAL(65,30)) /  "
					+ "    cast(TIME$WEEK.TIME$NUMBER_OF_TOKEN_PER_WEEK AS DECIMAL(65,30))  "
					+ "    as PR_TERM_GIVEN_WEEK "
					+ "from  "
					+ "( "
					+ "select TERM, TIME$WEEK, count(*) as NUMBER_OF_TOKEN_TERM_PER_WEEK "
					+ "from DOCUMENT_TERM_TOPIC " + "group by "
					+ "TERM, TIME$WEEK " + ") X  " + "join  " + "TERM  "
					+ "on (X.TERM=TERM.TERM_NAME) " + "join " + "TIME$WEEK "
					+ "on (X.TIME$WEEK=TIME$WEEK.TIME$WEEK);");
		} catch (SQLException e) {
			logger.error("Table TIME$TERM_WEEK could not be created properly.");
			throw new RuntimeException(e);
		}
	}

	private void timeTopicWeek() {
		try {
			database.executeUpdateQuery("create table TIME$TOPIC_WEEK ( "
					+ "TOPIC_ID INTEGER(11) NOT NULL, "
					+ "TIME$WEEK INTEGER(11) NOT NULL, "
					+ "TIME$NUMBER_OF_TOKEN_TOPIC_PER_WEEK int(11) NOT NULL,  "
					+ "TIME$PR_TOPIC_GIVEN_WEEK DOUBLE NOT NULL,  "
					+ "TIME$PR_WEEK_GIVEN_TOPIC DOUBLE)  "
					+ "ENGINE=MEMORY DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");
			database.executeUpdateQuery("insert into TIME$TOPIC_WEEK "
					+ "( "
					+ "TOPIC_ID, "
					+ "TIME$WEEK, "
					+ "TIME$NUMBER_OF_TOKEN_TOPIC_PER_WEEK, "
					+ "TIME$PR_TOPIC_GIVEN_WEEK,  "
					+ "TIME$PR_WEEK_GIVEN_TOPIC "
					+ ") "
					+ "select  "
					+ "X.TOPIC_ID, "
					+ "X.TIME$WEEK, "
					+ "X.NUMBER_OF_TOKEN_TOPIC_PER_WEEK, "
					+ "cast(X.NUMBER_OF_TOKEN_TOPIC_PER_WEEK AS DECIMAL(65,30)) /  "
					+ "    cast(TIME$WEEK.TIME$NUMBER_OF_TOKEN_PER_WEEK AS DECIMAL(65,30))  "
					+ "    as TIME$PR_TOPIC_GIVEN_WEEK, "
					+ "cast(X.NUMBER_OF_TOKEN_TOPIC_PER_WEEK AS DECIMAL(65,30)) /  "
					+ "    cast(TOPIC.NUMBER_OF_TOKENS AS DECIMAL(65,30))  "
					+ "    as TIME$PR_WEEK_GIVEN_TOPIC "
					+ "from  "
					+ "( "
					+ "select TOPIC_ID, TIME$WEEK, count(*) as NUMBER_OF_TOKEN_TOPIC_PER_WEEK "
					+ "from DOCUMENT_TERM_TOPIC " + "group by "
					+ "TOPIC_ID, TIME$WEEK " + ") X  " + "join  " + "TOPIC "
					+ "on (X.TOPIC_ID=TOPIC.TOPIC_ID) " + "join "
					+ "TIME$WEEK " + "on (X.TIME$WEEK=TIME$WEEK.TIME$WEEK);");
		} catch (SQLException e) {
			logger.error("Table TIME$TOPIC_WEEK could not be created properly.");
			throw new RuntimeException(e);
		}
	}

	private void timeTermTopicWeek() {
		try {
			database.executeUpdateQuery("create table TIME$TERM_TOPIC_WEEK ( "
					+ "TERM_NAME varchar(255) COLLATE utf8_bin NOT NULL,  "
					+ "TERM_ID INTEGER(11) NOT NULL,  "
					+ "TOPIC_ID INTEGER(11) NOT NULL, "
					+ "TIME$WEEK INTEGER(11) NOT NULL, "
					+ "TIME$NUMBER_OF_TOKEN_AND_TOPIC_PER_WEEK int(11) NOT NULL,  "
					+ "TIME$PR_TOPIC_GIVEN_TERM_WEEK DOUBLE NOT NULL,  "
					+ "TIME$PR_TERM_GIVEN_TOPIC_WEEK DOUBLE)  "
					+ "ENGINE=MEMORY DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");
			database.executeUpdateQuery("insert into TIME$TERM_TOPIC_WEEK (  "
					+ "TOPIC_ID,  "
					+ "TERM_NAME, "
					+ "TERM_ID, "
					+ "TIME$WEEK, "
					+ "TIME$NUMBER_OF_TOKEN_AND_TOPIC_PER_WEEK,  "
					+ "TIME$PR_TOPIC_GIVEN_TERM_WEEK,  "
					+ "TIME$PR_TERM_GIVEN_TOPIC_WEEK "
					+ ")   "
					+ "select  "
					+ "DOCUMENT_TERM_TOPIC.TOPIC_ID,  "
					+ "TIME$TERM_WEEK.TERM_NAME, "
					+ "TIME$TERM_WEEK.TERM_ID,  "
					+ "DOCUMENT_TERM_TOPIC.TIME$WEEK, "
					+ "count(*) TIME$NUMBER_OF_TOKEN_AND_TOPIC_PER_WEEK, "
					+ "cast(count(*) AS DECIMAL(65,30)) /  "
					+ "    cast(TIME$TERM_WEEK.TIME$NUMBER_OF_TOKEN_TERM_PER_WEEK AS DECIMAL(65,30))  "
					+ "    as TIME$PR_TOPIC_GIVEN_TERM_WEEK, "
					+ "cast(count(*) AS DECIMAL(65,30)) /  "
					+ "    cast(TIME$TOPIC_WEEK.TIME$NUMBER_OF_TOKEN_TOPIC_PER_WEEK AS DECIMAL(65,30))  "
					+ "    as TIME$PR_TERM_GIVEN_TOPIC_WEEK "
					+ "from DOCUMENT_TERM_TOPIC  "
					+ " join TIME$TERM_WEEK on  "
					+ "     ( "
					+ "      DOCUMENT_TERM_TOPIC.TERM = TIME$TERM_WEEK.TERM_NAME and  "
					+ "      DOCUMENT_TERM_TOPIC.TIME$WEEK=TIME$TERM_WEEK.TIME$WEEK "
					+ "     )   "
					+ " join TIME$TOPIC_WEEK on  "
					+ "     ( "
					+ "      DOCUMENT_TERM_TOPIC.TOPIC_ID = TIME$TOPIC_WEEK.TOPIC_ID and  "
					+ "      DOCUMENT_TERM_TOPIC.TIME$WEEK=TIME$TOPIC_WEEK.TIME$WEEK "
					+ "     )   " + " group by  "
					+ "DOCUMENT_TERM_TOPIC.TERM,  "
					+ "DOCUMENT_TERM_TOPIC.TOPIC_ID, "
					+ "DOCUMENT_TERM_TOPIC.TIME$WEEK, "
					+ "TIME$TERM_WEEK.TIME$NUMBER_OF_TOKEN_TERM_PER_WEEK, "
					+ "TIME$TOPIC_WEEK.TIME$NUMBER_OF_TOKEN_TOPIC_PER_WEEK");
		} catch (SQLException e) {
			logger.error("Table TIME$TOPIC_TERM_WEEK could not be created properly.");
			throw new RuntimeException(e);
		}
	}
	
	private void updateBestWords() {
		try {
			database.executeUpdateQuery("ALTER TABLE `TIME$WORDS_PER_TOPIC_PER_WEEK`"
					+ " ADD `BEST_WORDS` TEXT NOT NULL");
			
			ResultSet bestWordsQueryRS = database.executeQuery("select distinct TERM_NAME,"
					+ "TOPIC_ID,TIME$WEEK,TIME$PR_TERM_GIVEN_TOPIC_WEEK from TIME$TERM_TOPIC_WEEK "
					+ "order by TOPIC_ID,TIME$WEEK,TIME$PR_TERM_GIVEN_TOPIC_WEEK desc");
			
			int topicId = -1;
			int count = 0;
			int week = 0;
			String bestWord = new String("");
			List<String> updateQueries= new ArrayList<String>();
			
			while (bestWordsQueryRS.next()) {
				if(topicId == -1) {
					topicId = bestWordsQueryRS.getInt("TOPIC_ID");
					week = bestWordsQueryRS.getInt("TIME$WEEK");
				}
				
				if(bestWordsQueryRS.getInt("TOPIC_ID") == topicId && week == bestWordsQueryRS.getInt("TIME$WEEK")) {
					if(count == 0) {	
						bestWord = bestWordsQueryRS.getString("TERM_NAME");		
					} else if(count < 4) {
						bestWord += "," + bestWordsQueryRS.getString("TERM_NAME");
					}
					count ++;
				} else {	
					updateQueries.add("UPDATE TIME$WORDS_PER_TOPIC_PER_WEEK SET BEST_WORDS='"
							+ bestWord + "' WHERE TOPIC_ID=" + topicId + " AND WEEK=" + week);
					topicId = bestWordsQueryRS.getInt("TOPIC_ID");
					week = bestWordsQueryRS.getInt("TIME$WEEK");
					bestWord = bestWordsQueryRS.getString("TERM_NAME");
					count = 1;
				
				}
			}
			updateQueries.add("UPDATE TIME$WORDS_PER_TOPIC_PER_WEEK SET BEST_WORDS='"
					+ bestWord + "' WHERE TOPIC_ID=" + topicId + " AND WEEK=" + week);
			for(String updateQuery: updateQueries) {
				database.executeUpdateQuery(updateQuery);
			}
				
			database.executeUpdateQuery("DROP TABLE TIME$WEEK");
			database.executeUpdateQuery("DROP TABLE TIME$TERM_WEEK");
			database.executeUpdateQuery("DROP TABLE TIME$TOPIC_WEEK");
			database.executeUpdateQuery("DROP TABLE TIME$TERM_TOPIC_WEEK");
			
		} catch (SQLException e) {
			logger.error("Table " + this.tableName
						+ " could not be updated properly.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void fillTable() {
		if (Boolean.parseBoolean(this.properties.getProperty("plugin_time"))) {
			/**
			 * MIT-JOOQ-START database.executeUpdateQuery("INSERT INTO " +
			 * TIME$WORDS_PER_TOPIC_PER_WEEK.getName() + "(" +
			 * TIME$WORDS_PER_TOPIC_PER_WEEK.WEEK.getName() + ", " +
			 * TIME$WORDS_PER_TOPIC_PER_WEEK.TOPIC_ID.getName() + ", " +
			 * TIME$WORDS_PER_TOPIC_PER_WEEK.WORD_COUNT.getName() + ") SELECT "
			 * + DOCUMENT_TERM_TOPIC.TIME$WEEK.getName() + ", " +
			 * DOCUMENT_TERM_TOPIC.getName() + "." +
			 * DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() +
			 * ", COUNT(*) AS count FROM " + DOCUMENT_TERM_TOPIC.getName() +
			 * ", " + TOPIC.getName() + " WHERE " + TOPIC.getName() + "." +
			 * TOPIC.TOPIC_ID.getName() + " = " + DOCUMENT_TERM_TOPIC.getName()
			 * + "." + DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + " GROUP BY " +
			 * DOCUMENT_TERM_TOPIC.TIME$WEEK.getName() + ", " +
			 * DOCUMENT_TERM_TOPIC.getName() + "." +
			 * DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() +";"); MIT-JOOQ-ENDE
			 */
			/** OHNE_JOOQ-START */
			// @formatter:off
			try {
				timeWeek();
				timeTermWeek();
				timeTopicWeek();
				timeTermTopicWeek();
				
				database.executeUpdateQuery("INSERT INTO "
						+ "TIME$WORDS_PER_TOPIC_PER_WEEK" + "("
						+ "TIME$WORDS_PER_TOPIC_PER_WEEK.WEEK" + ", "
						+ "TIME$WORDS_PER_TOPIC_PER_WEEK.TOPIC_ID" + ", "
						+ "TIME$WORDS_PER_TOPIC_PER_WEEK.WORD_COUNT"
						+ ") SELECT " + "DOCUMENT_TERM_TOPIC.TIME$WEEK" + ", "
						+ "DOCUMENT_TERM_TOPIC.TOPIC_ID"
						+ ", COUNT(*) AS count FROM " + "DOCUMENT_TERM_TOPIC"
						+ ", " + "TOPIC" + " WHERE " + "TOPIC.TOPIC_ID" + " = "
						+ "DOCUMENT_TERM_TOPIC.TOPIC_ID" + " GROUP BY "
						+ "DOCUMENT_TERM_TOPIC.TIME$WEEK" + ", "
						+ "DOCUMENT_TERM_TOPIC.TOPIC_ID" + ";");
				
				updateBestWords();
				// @formatter:on
			} catch (SQLException e) {
				logger.error("Table " + this.tableName
						+ " could not be filled properly.");
				throw new RuntimeException(e);
			}
			/** OHNE_JOOQ-ENDE */
		}

	}

	@Override
	public void setTableName() {
		/**
		 * MIT-JOOQ-START this.tableName =
		 * TIME$WORDS_PER_TOPIC_PER_WEEK.getName(); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		this.tableName = "TIME$WORDS_PER_TOPIC_PER_WEEK";
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("TopicFill");
		beforeDependencies.add("Time_DocumentTermTopicFill");
		beforeDependencies.add("Time_WordsPerTopicPerWeekCreate");

	}
}