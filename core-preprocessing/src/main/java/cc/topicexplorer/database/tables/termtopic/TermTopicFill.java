package cc.topicexplorer.database.tables.termtopic;

/** MIT-JOOQ-START 
 import static jooq.generated.Tables.TERM;
 import static jooq.generated.Tables.TERM_TOPIC;
 import static jooq.generated.Tables.DOCUMENT_TERM_TOPIC;
 import static jooq.generated.Tables.TOPIC; 
 MIT-JOOQ-ENDE */

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;

import com.google.common.collect.Sets;

public class TermTopicFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(TermTopicFill.class);

	@Override
	public void setTableName() {
		/**
		 * MIT-JOOQ-START tableName = TERM_TOPIC.getName(); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		tableName = "TERM_TOPIC";
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public void fillTable() {

// @formatter:off
/** MIT-JOOQ-START 
		database.executeUpdateQuery("insert into " + TERM_TOPIC.getName() + " ( "
				+ TERM_TOPIC.TOPIC_ID.getName() + ", "
				+ TERM_TOPIC.TERM_ID.getName() + ", "
				+ TERM_TOPIC.NUMBER_OF_TOKEN_TOPIC.getName() + ", "
				+ TERM_TOPIC.PR_TOPIC_GIVEN_TERM.getName() + ", "
				+ TERM_TOPIC.PR_TERM_GIVEN_TOPIC.getName() + ") " + " select "
				+ DOCUMENT_TERM_TOPIC.getName() + "."
				+ DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + ", "
				+ TERM.getName() + "." + TERM.TERM_ID.getName() + ", "
				+ "count(*) as NUMBER_OF_TOKEN_TOPIC, "
				+ " cast(count(*) AS DECIMAL(65,30)) / cast(" + TERM.getName()
				+ "." + TERM.CORPUS_FREQUENCY.getName()
				+ " AS DECIMAL("65,30)), "
				+ " cast(count(*) AS DECIMAL(65,30)) / cast(" + TOPIC.getName()
				+ "." + TOPIC.NUMBER_OF_TOKENS.getName()
				+ " AS DECIMAL(65,30)) " + " from DOCUMENT_TERM_TOPIC "
				+ " join " + TERM.getName() + " on ("
				+ DOCUMENT_TERM_TOPIC.getName() + "."
				+ DOCUMENT_TERM_TOPIC.TERM.getName() + " = " + TERM.getName()
				+ "." + TERM.TERM_NAME.getName() + " ) " + " join "
				+ TOPIC.getName() + " on ( " + DOCUMENT_TERM_TOPIC.getName()
				+ "." + DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + " = "
				+ TOPIC.getName() + "." + TOPIC.TOPIC_ID.getName() + " ) "
				+ " group by " + DOCUMENT_TERM_TOPIC.getName() + "."
				+ DOCUMENT_TERM_TOPIC.TERM.getName() + ", "
				+ DOCUMENT_TERM_TOPIC.getName() + "."
				+ DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + ", "
				+ TERM.getName() + "." + TERM.TERM_ID.getName() + ", "
				+ TERM.getName() + "." + TERM.CORPUS_FREQUENCY.getName() + ", "
				+ TOPIC.getName() + "." + TOPIC.NUMBER_OF_TOKENS.getName());
		database.executeUpdateQuery("ALTER TABLE " + TERM_TOPIC.getName()
				+ " ADD KEY PRIMARY_IDX (" + TERM_TOPIC.TERM_ID.getName() + ", "
				+ TERM_TOPIC.TOPIC_ID.getName() + "), "
				+ " ADD KEY `TOPIC_TERM_IDX` (" + TERM_TOPIC.TOPIC_ID.getName()
				+ ", " + TERM_TOPIC.TERM_ID.getName() + ")");
 MIT-JOOQ-ENDE */ 	
//				@formatter:on
		/** OHNE_JOOQ-START */
//		@formatter:off
		try {
			database.executeUpdateQuery("insert into " + "TERM_TOPIC" + " ( "
					+ "TERM_TOPIC.TOPIC_ID" + ", "
					+ "TERM_TOPIC.TERM_ID" + ", "
					+ "TERM_TOPIC.NUMBER_OF_TOKEN_TOPIC" + ", "
					+ "TERM_TOPIC.NUMBER_OF_DOCUMENT_TOPIC" + ", "
					+ "TERM_TOPIC.PR_TOPIC_GIVEN_TERM" + ", "
					+ "TERM_TOPIC.PR_TERM_GIVEN_TOPIC" + ") select X.TOPIC_ID, "
					+ "TERM.TERM_ID, X.term_topic_count as NUMBER_OF_TOKEN_TOPIC,"
					+ "X.NUMBER_OF_DOCUMENT_TOPIC,"
					+ "cast(X.term_topic_count AS DECIMAL(65,30)) / cast(TERM.CORPUS_FREQUENCY AS DECIMAL(65,30)), "
					+ "cast(X.term_topic_count AS DECIMAL(65,30)) / cast(TOPIC.NUMBER_OF_TOKENS AS DECIMAL(65,30)) "
					+ "from "
					+ "( select TERM, TOPIC_ID, count(*) as term_topic_count, count(DISTINCT DOCUMENT_ID) as NUMBER_OF_DOCUMENT_TOPIC "
					+ "from DOCUMENT_TERM_TOPIC "
					+ "group by "
					+ "DOCUMENT_TERM_TOPIC.TERM, "
					+ "DOCUMENT_TERM_TOPIC.TOPIC_ID"
					+ ") X "
					+ "join TERM on (X.TERM = TERM.TERM_NAME ) "
					+ "join TOPIC on (X.TOPIC_ID = TOPIC.TOPIC_ID )");
			database.executeUpdateQuery("ALTER TABLE " + "TERM_TOPIC"
					+ " ADD KEY PRIMARY_IDX (" + "TERM_ID" + ", "
					+ "TOPIC_ID" + "), "
					+ " ADD KEY `TOPIC_TERM_IDX` (" + "TOPIC_ID"
					+ ", " + "TERM_ID" + ")");
//		@formatter:on
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be filled properly.");
			throw new RuntimeException(e);
		}
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("TermTopicCreate", "DocumentTermTopicFill", "DocumentFill", "TopicFill");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}

}
