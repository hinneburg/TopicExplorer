package cc.topicexplorer.database.tables.documenttopic;
/** MIT-JOOQ-START 
import static jooq.generated.Tables.DOCUMENT_TERM_TOPIC;
import static jooq.generated.Tables.DOCUMENT_TOPIC;
import static jooq.generated.Tables.DOCUMENT;
import static jooq.generated.Tables.TOPIC; 
 MIT-JOOQ-ENDE */ 
import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableFillCommand;

public class DocumentTopicFill extends TableFillCommand {
	@Override
	public void setTableName() {
/** MIT-JOOQ-START 
		tableName = DOCUMENT_TOPIC.getName();
 MIT-JOOQ-ENDE */
 /** OHNE_JOOQ-START */
		tableName = "DOCUMENT_TOPIC";
 /** OHNE_JOOQ-ENDE */ 
	}
	
	@Override
	public void fillTable() throws SQLException {
/** MIT-JOOQ-START 
		String sql = "INSERT INTO " + DOCUMENT_TOPIC.getName() + "("
				+ DOCUMENT_TOPIC.DOCUMENT_ID.getName() + ", "
				+ DOCUMENT_TOPIC.TOPIC_ID.getName() + ",  "
				+ DOCUMENT_TOPIC.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT.getName()
				+ ", " + DOCUMENT_TOPIC.PR_DOCUMENT_GIVEN_TOPIC.getName()
				+ ",  " + DOCUMENT_TOPIC.PR_TOPIC_GIVEN_DOCUMENT.getName()
				+ ") " + "	select " + DOCUMENT_TERM_TOPIC.getName() + "."
				+ DOCUMENT_TERM_TOPIC.DOCUMENT_ID.getName() + ", "
				+ DOCUMENT_TERM_TOPIC.getName() + "."
				+ DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + ",  " + "count(*), "
				+ "cast(count(*) AS DECIMAL(65,30)) / cast(" + TOPIC.getName()
				+ "." + TOPIC.NUMBER_OF_TOKENS.getName()
				+ " AS DECIMAL(65,30)), "
				+ "cast(count(*) AS DECIMAL(65,30)) / cast("
				+ DOCUMENT.getName() + "."
				+ DOCUMENT.NUMBER_OF_TOKENS.getName() + " AS DECIMAL(65,30)) "
				+ " from " + DOCUMENT_TERM_TOPIC.getName() + " join "
				+ DOCUMENT.getName() + " on ( " + DOCUMENT_TERM_TOPIC.getName()
				+ "." + DOCUMENT_TERM_TOPIC.DOCUMENT_ID.getName() + " = "
				+ DOCUMENT.getName() + "." + DOCUMENT.DOCUMENT_ID.getName()
				+ ")" + " join " + TOPIC.getName() + " on ("
				+ DOCUMENT_TERM_TOPIC.getName() + "."
				+ DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + " = "
				+ TOPIC.getName() + "." + TOPIC.TOPIC_ID.getName() + ")"
				+ " group by " + DOCUMENT_TERM_TOPIC.getName() + "."
				+ DOCUMENT_TERM_TOPIC.DOCUMENT_ID.getName() + ", "
				+ DOCUMENT_TERM_TOPIC.getName() + "."
				+ DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + ", "
				+ DOCUMENT.getName() + "."
				+ DOCUMENT.NUMBER_OF_TOKENS.getName() + ", " + TOPIC.getName()
				+ "." + TOPIC.NUMBER_OF_TOKENS.getName();

		database.executeUpdateQuery(sql);

		database.executeUpdateQuery("ALTER TABLE " + DOCUMENT_TOPIC.getName()
				+ " ADD KEY PRIMARY_IDX(" + DOCUMENT_TOPIC.DOCUMENT_ID.getName()
				+ "," + DOCUMENT_TOPIC.TOPIC_ID.getName() + "),"
				+ " ADD KEY TOPIC_DOCUMENT_IDX ("
				+ DOCUMENT_TOPIC.TOPIC_ID.getName() + ","
				+ DOCUMENT_TOPIC.DOCUMENT_ID.getName() + "),"
				+ " ADD KEY TOPIC_PR_DOCUMENT_GIVEN_TOPIC_IDX ("
				+ DOCUMENT_TOPIC.TOPIC_ID.getName() + ","
				+ DOCUMENT_TOPIC.PR_DOCUMENT_GIVEN_TOPIC.getName() + "),"
				+ " ADD KEY DOCUMENT_PR_DOKUMENT_GIVEN_TOPIC_IDX ("
				+ DOCUMENT_TOPIC.DOCUMENT_ID.getName() + ","
				+ DOCUMENT_TOPIC.PR_TOPIC_GIVEN_DOCUMENT.getName() + ")");
 MIT-JOOQ-ENDE */ 
/** OHNE_JOOQ-START */ 
		String sql = "INSERT INTO " + "DOCUMENT_TOPIC" + "("
				+ "DOCUMENT_TOPIC.DOCUMENT_ID" + ", "
				+ "DOCUMENT_TOPIC.TOPIC_ID" + ",  "
				+ "DOCUMENT_TOPIC.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT"
				+ ", " + "DOCUMENT_TOPIC.PR_DOCUMENT_GIVEN_TOPIC"
				+ ",  " + "DOCUMENT_TOPIC.PR_TOPIC_GIVEN_DOCUMENT"
				+ ") " + "	select "
				+ "DOCUMENT_TERM_TOPIC.DOCUMENT_ID" + ", "
				+ "DOCUMENT_TERM_TOPIC.TOPIC_ID" + ",  " + "count(*), "
				+ "cast(count(*) AS DECIMAL(65,30)) / cast("
				+ "TOPIC.NUMBER_OF_TOKENS"
				+ " AS DECIMAL(65,30)), "
				+ "cast(count(*) AS DECIMAL(65,30)) / cast("
				+ "DOCUMENT.NUMBER_OF_TOKENS" + " AS DECIMAL(65,30)) "
				+ " from " + "DOCUMENT_TERM_TOPIC" + " join "
				+ "DOCUMENT" + " on ( "
				+ "DOCUMENT_TERM_TOPIC.DOCUMENT_ID" + " = "
				+ "DOCUMENT.DOCUMENT_ID"
				+ ")" + " join " + "TOPIC" + " on ("
				+ "DOCUMENT_TERM_TOPIC.TOPIC_ID" + " = "
				+ "TOPIC.TOPIC_ID" + ")"
				+ " group by "
				+ "DOCUMENT_TERM_TOPIC.DOCUMENT_ID" + ", "
				+ "DOCUMENT_TERM_TOPIC.TOPIC_ID" + ", "
				+ "DOCUMENT.NUMBER_OF_TOKENS" + ", " 
				+ "TOPIC.NUMBER_OF_TOKENS";

		database.executeUpdateQuery(sql);

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
/** OHNE_JOOQ-ENDE */ 
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentTopicCreate");
		beforeDependencies.add("DocumentTermTopicFill");
		beforeDependencies.add("DocumentFill");
		beforeDependencies.add("TopicFill");
	}	
}
