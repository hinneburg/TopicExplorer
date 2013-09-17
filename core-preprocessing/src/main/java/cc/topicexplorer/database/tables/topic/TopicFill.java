package cc.topicexplorer.database.tables.topic;
/** MIT-JOOQ-START 
import static jooq.generated.Tables.TOPIC;
import static jooq.generated.Tables.DOCUMENT_TERM_TOPIC; 
 MIT-JOOQ-ENDE */ 
import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableFillCommand;

public class TopicFill extends TableFillCommand {
	@Override
	public void setTableName() {
/** MIT-JOOQ-START 
		tableName = TOPIC.getName();
 MIT-JOOQ-ENDE */ 
/** OHNE_JOOQ-START */ 
		tableName = "TOPIC";
/** OHNE_JOOQ-ENDE */ 
	}
	
	@Override
	public void fillTable() throws SQLException {
/** MIT-JOOQ-START 
		database.executeUpdateQuery("insert into `" + TOPIC.getName()
				+ "` (`" + TOPIC.TOPIC_ID.getName()
				+ "`, `" + TOPIC.NUMBER_OF_TOKENS.getName()
				+ "`) select `" +  DOCUMENT_TERM_TOPIC.TOPIC_ID.getName()
				+ "`, count(*) from `" + DOCUMENT_TERM_TOPIC.getName()
				+ "` group by `" + DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + "`");
		database.executeUpdateQuery("ALTER TABLE " + TOPIC.getName()
				+ " ADD KEY IDX0 (`" + TOPIC.TOPIC_ID.getName() + "`)");
 MIT-JOOQ-ENDE */ 
/** OHNE_JOOQ-START */ 
		database.executeUpdateQuery("insert into `" + "TOPIC"
				+ "` (`" + "TOPIC.TOPIC_ID"
				+ "`, `" + "TOPIC.NUMBER_OF_TOKENS"
				+ "`) select `" +  "DOCUMENT_TERM_TOPIC.TOPIC_ID"
				+ "`, count(*) from `" + "DOCUMENT_TERM_TOPIC"
				+ "` group by `" + "DOCUMENT_TERM_TOPIC.TOPIC_ID" + "`");
		database.executeUpdateQuery("ALTER TABLE " + "TOPIC"
				+ " ADD KEY IDX0 (`" + "TOPIC.TOPIC_ID" + "`)");
/** OHNE_JOOQ-ENDE */ 

	}
	
	@Override
	public void addDependencies() {
		beforeDependencies.add("TopicCreate");
		beforeDependencies.add("DocumentTermTopicFill");
	}	
}
