package cc.topicexplorer.database.tables.documenttopic;

/** MIT-JOOQ-START 
 import static jooq.generated.Tables.DOCUMENT_TERM_TOPIC;
 import static jooq.generated.Tables.DOCUMENT_TOPIC;
 import static jooq.generated.Tables.DOCUMENT;
 import static jooq.generated.Tables.TOPIC; 
 MIT-JOOQ-ENDE */
import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;

import com.google.common.collect.Sets;

public class DocumentTopicFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(DocumentTopicFill.class);

	@Override
	public void setTableName() {
		/**
		 * MIT-JOOQ-START tableName = DOCUMENT_TOPIC.getName(); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		tableName = "DOCUMENT_TOPIC";
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public void fillTable() {
		/**
		 * MIT-JOOQ-START String sql = "INSERT INTO " + DOCUMENT_TOPIC.getName() + "(" +
		 * DOCUMENT_TOPIC.DOCUMENT_ID.getName() + ", " + DOCUMENT_TOPIC.TOPIC_ID.getName() + ",  " +
		 * DOCUMENT_TOPIC.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT.getName() + ", " +
		 * DOCUMENT_TOPIC.PR_DOCUMENT_GIVEN_TOPIC.getName() + ",  " + DOCUMENT_TOPIC.PR_TOPIC_GIVEN_DOCUMENT.getName() +
		 * ") " + "	select " + DOCUMENT_TERM_TOPIC.getName() + "." + DOCUMENT_TERM_TOPIC.DOCUMENT_ID.getName() + ", " +
		 * DOCUMENT_TERM_TOPIC.getName() + "." + DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + ",  " + "count(*), " +
		 * "cast(count(*) AS DECIMAL(65,30)) / cast(" + TOPIC.getName() + "." + TOPIC.NUMBER_OF_TOKENS.getName() +
		 * " AS DECIMAL(65,30)), " + "cast(count(*) AS DECIMAL(65,30)) / cast(" + DOCUMENT.getName() + "." +
		 * DOCUMENT.NUMBER_OF_TOKENS.getName() + " AS DECIMAL(65,30)) " + " from " + DOCUMENT_TERM_TOPIC.getName() +
		 * " join " + DOCUMENT.getName() + " on ( " + DOCUMENT_TERM_TOPIC.getName() + "." +
		 * DOCUMENT_TERM_TOPIC.DOCUMENT_ID.getName() + " = " + DOCUMENT.getName() + "." + DOCUMENT.DOCUMENT_ID.getName()
		 * + ")" + " join " + TOPIC.getName() + " on (" + DOCUMENT_TERM_TOPIC.getName() + "." +
		 * DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + " = " + TOPIC.getName() + "." + TOPIC.TOPIC_ID.getName() + ")" +
		 * " group by " + DOCUMENT_TERM_TOPIC.getName() + "." + DOCUMENT_TERM_TOPIC.DOCUMENT_ID.getName() + ", " +
		 * DOCUMENT_TERM_TOPIC.getName() + "." + DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + ", " + DOCUMENT.getName() +
		 * "." + DOCUMENT.NUMBER_OF_TOKENS.getName() + ", " + TOPIC.getName() + "." + TOPIC.NUMBER_OF_TOKENS.getName();
		 * 
		 * database.executeUpdateQuery(sql);
		 * 
		 * database.executeUpdateQuery("ALTER TABLE " + DOCUMENT_TOPIC.getName() + " ADD KEY PRIMARY_IDX(" +
		 * DOCUMENT_TOPIC.DOCUMENT_ID.getName() + "," + DOCUMENT_TOPIC.TOPIC_ID.getName() + ")," +
		 * " ADD KEY TOPIC_DOCUMENT_IDX (" + DOCUMENT_TOPIC.TOPIC_ID.getName() + "," +
		 * DOCUMENT_TOPIC.DOCUMENT_ID.getName() + ")," + " ADD KEY TOPIC_PR_DOCUMENT_GIVEN_TOPIC_IDX (" +
		 * DOCUMENT_TOPIC.TOPIC_ID.getName() + "," + DOCUMENT_TOPIC.PR_DOCUMENT_GIVEN_TOPIC.getName() + ")," +
		 * " ADD KEY DOCUMENT_PR_DOKUMENT_GIVEN_TOPIC_IDX (" + DOCUMENT_TOPIC.DOCUMENT_ID.getName() + "," +
		 * DOCUMENT_TOPIC.PR_TOPIC_GIVEN_DOCUMENT.getName() + ")"); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		// @formatter:off
		String sql = "INSERT INTO " + "DOCUMENT_TOPIC" + "("
				+ "DOCUMENT_TOPIC.DOCUMENT_ID" + ", "
				+ "DOCUMENT_TOPIC.TOPIC_ID" + ",  "
				+ "DOCUMENT_TOPIC.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT"
				+ ", " + "DOCUMENT_TOPIC.PR_DOCUMENT_GIVEN_TOPIC"
				+ ",  " + "DOCUMENT_TOPIC.PR_TOPIC_GIVEN_DOCUMENT"
				+ ") " + "	select "
				+ "X.DOCUMENT_ID, X.TOPIC_ID, X.document_topic_count, "
				+ "cast(X.document_topic_count AS DECIMAL(65,30)) / cast(TOPIC.NUMBER_OF_TOKENS AS DECIMAL(65,30)), "
				+ "cast(X.document_topic_count AS DECIMAL(65,30)) / cast(DOCUMENT.NUMBER_OF_TOKENS AS DECIMAL(65,30)) "
				+ "from (SELECT DOCUMENT_ID, TOPIC_ID, count(*) as document_topic_count "
				+ "from DOCUMENT_TERM_TOPIC "
				+ "group by DOCUMENT_TERM_TOPIC.DOCUMENT_ID, DOCUMENT_TERM_TOPIC.TOPIC_ID) X "
				+ "join DOCUMENT on (X.DOCUMENT_ID = DOCUMENT.DOCUMENT_ID) "
				+ "join TOPIC on (X.TOPIC_ID = TOPIC.TOPIC_ID) ";
		//@formatter:on

		try {
			database.executeUpdateQuery(sql);
			// @formatter:off
			database.executeUpdateQuery("ALTER TABLE " + "DOCUMENT_TOPIC"
					+ " ADD KEY PRIMARY_IDX(" + "DOCUMENT_ID"
					+ "," + "TOPIC_ID" + "),"
					+ " ADD KEY TOPIC_DOCUMENT_IDX ("
					+ "TOPIC_ID" + ","
					+ "DOCUMENT_ID" + "),"
					+ " ADD KEY TOPIC_PR_DOCUMENT_GIVEN_TOPIC_IDX ("
					+ "TOPIC_ID" + ","
					+ "PR_DOCUMENT_GIVEN_TOPIC" + "),"
					+ " ADD KEY DOCUMENT_PR_DOKUMENT_GIVEN_TOPIC_IDX ("
					+ "DOCUMENT_ID" + ","
					+ "PR_TOPIC_GIVEN_DOCUMENT" + ")");
			// @formatter:on
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
		return Sets.newHashSet("DocumentTopicCreate", "DocumentTermTopicFill", "DocumentFill", "TopicFill");
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
