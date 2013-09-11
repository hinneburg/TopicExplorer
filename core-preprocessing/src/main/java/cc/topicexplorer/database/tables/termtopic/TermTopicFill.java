package cc.topicexplorer.database.tables.termtopic;


/** MIT-JOOQ-START 
import static jooq.generated.Tables.TERM;
import static jooq.generated.Tables.TERM_TOPIC;
import static jooq.generated.Tables.DOCUMENT_TERM_TOPIC;
import static jooq.generated.Tables.TOPIC; 
 MIT-JOOQ-ENDE */ 

import java.sql.SQLException;
import java.util.ArrayList;

import cc.topicexplorer.chain.commands.TableFillCommand;

public class TermTopicFill extends TableFillCommand {
	@Override
	public void setTableName() {
/** MIT-JOOQ-START 
		tableName = TERM_TOPIC.getName();
 MIT-JOOQ-ENDE */ 
/** OHNE_JOOQ-START */ 
		tableName = "TERM_TOPIC";
/** OHNE_JOOQ-ENDE */ 
	}
	@Override
	public void fillTable() throws SQLException {


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
/** OHNE_JOOQ-START */ 	
		database.executeUpdateQuery("insert into " + "TERM_TOPIC" + " ( "
				+ "TERM_TOPIC.TOPIC_ID" + ", "
				+ "TERM_TOPIC.TERM_ID" + ", "
				+ "TERM_TOPIC.NUMBER_OF_TOKEN_TOPIC" + ", "
				+ "TERM_TOPIC.PR_TOPIC_GIVEN_TERM" + ", "
				+ "TERM_TOPIC.PR_TERM_GIVEN_TOPIC" + ") " + " select "
				+ "DOCUMENT_TERM_TOPIC" + "."
				+ "DOCUMENT_TERM_TOPIC.TOPIC_ID" + ", "
				+ "TERM" + "." + "TERM.TERM_ID" + ", "
				+ "count(*) as NUMBER_OF_TOKEN_TOPIC, "
				+ " cast(count(*) AS DECIMAL(65,30)) / cast(" + "TERM"
				+ "." + "TERM.CORPUS_FREQUENCY"
				+ " AS DECIMAL(65,30)), "
				+ " cast(count(*) AS DECIMAL(65,30)) / cast(" + "TOPIC"
				+ "." + "TOPIC.NUMBER_OF_TOKENS"
				+ " AS DECIMAL(65,30)) " + " from DOCUMENT_TERM_TOPIC "
				+ " join " + "TERM" + " on ("
				+ "DOCUMENT_TERM_TOPIC" + "."
				+ "DOCUMENT_TERM_TOPIC.TERM" + " = " + "TERM"
				+ "." + "TERM.TERM_NAME" + " ) " + " join "
				+ "TOPIC" + " on ( " + "DOCUMENT_TERM_TOPIC"
				+ "." + "DOCUMENT_TERM_TOPIC.TOPIC_ID" + " = "
				+ "TOPIC" + "." + "TOPIC.TOPIC_ID" + " ) "
				+ " group by " + "DOCUMENT_TERM_TOPIC" + "."
				+ "DOCUMENT_TERM_TOPIC.TERM" + ", "
				+ "DOCUMENT_TERM_TOPIC" + "."
				+ "DOCUMENT_TERM_TOPIC.TOPIC_ID" + ", "
				+ "TERM" + "." + "TERM.TERM_ID" + ", "
				+ "TERM" + "." + "TERM.CORPUS_FREQUENCY" + ", "
				+ "TOPIC" + "." + "TOPIC.NUMBER_OF_TOKENS");
		database.executeUpdateQuery("ALTER TABLE " + "TERM_TOPIC"
				+ " ADD KEY PRIMARY_IDX (" + "TERM_TOPIC.TERM_ID" + ", "
				+ "TERM_TOPIC.TOPIC_ID" + "), "
				+ " ADD KEY `TOPIC_TERM_IDX` (" + "TERM_TOPIC.TOPIC_ID"
				+ ", " + "TERM_TOPIC.TERM_ID" + ")");
/** OHNE_JOOQ-ENDE */ 
	}
	
	@Override
	public void addDependencies() {
		beforeDependencies.add("TermTopicCreate");
		beforeDependencies.add("DocumentTermTopicFill");
		beforeDependencies.add("DocumentFill");
		beforeDependencies.add("TopicFill");
	}
}
