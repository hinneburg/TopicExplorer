package cc.topicexplorer.plugin.time.preprocessing.tables.wordspertopicperweek;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableFillCommand;

/** MIT-JOOQ-START 
import static jooq.generated.Tables.DOCUMENT_TERM_TOPIC;
import static jooq.generated.Tables.TIME$WORDS_PER_TOPIC_PER_WEEK;
import static jooq.generated.Tables.TOPIC;
MIT-JOOQ-ENDE */ 

/**
 * @author user
 * 
 */
public class WordsPerTopicPerWeekFill extends TableFillCommand {

	@Override
	public void fillTable() throws SQLException {

		if(Boolean.parseBoolean(this.properties.getProperty("plugin_time"))) {
			/** MIT-JOOQ-START 
			database.executeUpdateQuery("INSERT INTO " 
					+ TIME$WORDS_PER_TOPIC_PER_WEEK.getName() 
					+ "("
					+ TIME$WORDS_PER_TOPIC_PER_WEEK.WEEK.getName() 
					+ ", "
					+ TIME$WORDS_PER_TOPIC_PER_WEEK.TOPIC_ID.getName() 
					+ ", "
					+ TIME$WORDS_PER_TOPIC_PER_WEEK.WORD_COUNT.getName() 
					+ ") SELECT "
					+ DOCUMENT_TERM_TOPIC.TIME$WEEK.getName()
					+ ", "
					+ DOCUMENT_TERM_TOPIC.getName()
					+ "."
					+ DOCUMENT_TERM_TOPIC.TOPIC_ID.getName()
					+ ", COUNT(*) AS count FROM "
					+ DOCUMENT_TERM_TOPIC.getName()
					+ ", "
					+ TOPIC.getName()
					+ " WHERE "
					+ TOPIC.getName()
					+ "."
					+ TOPIC.TOPIC_ID.getName()
					+ " = "
					+ DOCUMENT_TERM_TOPIC.getName()
					+ "."
					+ DOCUMENT_TERM_TOPIC.TOPIC_ID.getName()
					+ " GROUP BY "
					+ DOCUMENT_TERM_TOPIC.TIME$WEEK.getName()
					+ ", "
					+ DOCUMENT_TERM_TOPIC.getName()
					+ "."
					+ DOCUMENT_TERM_TOPIC.TOPIC_ID.getName()
					+";");
				MIT-JOOQ-ENDE */ 
	/** OHNE_JOOQ-START */ 
			database.executeUpdateQuery("INSERT INTO " 
					+ "TIME$WORDS_PER_TOPIC_PER_WEEK" 
					+ "("
					+ "TIME$WORDS_PER_TOPIC_PER_WEEK.WEEK" 
					+ ", "
					+ "TIME$WORDS_PER_TOPIC_PER_WEEK.TOPIC_ID" 
					+ ", "
					+ "TIME$WORDS_PER_TOPIC_PER_WEEK.WORD_COUNT" 
					+ ") SELECT "
					+ "DOCUMENT_TERM_TOPIC.TIME$WEEK"
					+ ", "
					+ "DOCUMENT_TERM_TOPIC"
					+ "."
					+ "DOCUMENT_TERM_TOPIC.TOPIC_ID"
					+ ", COUNT(*) AS count FROM "
					+ "DOCUMENT_TERM_TOPIC"
					+ ", "
					+ "TOPIC"
					+ " WHERE "
					+ "TOPIC"
					+ "."
					+ "TOPIC.TOPIC_ID"
					+ " = "
					+ "DOCUMENT_TERM_TOPIC"
					+ "."
					+ "DOCUMENT_TERM_TOPIC.TOPIC_ID"
					+ " GROUP BY "
					+ "DOCUMENT_TERM_TOPIC.TIME$WEEK"
					+ ", "
					+ "DOCUMENT_TERM_TOPIC"
					+ "."
					+ "DOCUMENT_TERM_TOPIC.TOPIC_ID"
					+";");
	/** OHNE_JOOQ-ENDE */ 	
		}

	}

	@Override
	public void setTableName() {
	/** MIT-JOOQ-START 
		this.tableName = TIME$WORDS_PER_TOPIC_PER_WEEK.getName();
		MIT-JOOQ-ENDE */ 
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