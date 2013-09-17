package cc.topicexplorer.plugin.hierarchicaltopic.preprocessing.tables.documenttopic;

/** MIT-JOOQ-START 
import static jooq.generated.Tables.TOPIC;
import static jooq.generated.Tables.DOCUMENT_TOPIC;
MIT-JOOQ-ENDE */ 
import java.sql.ResultSet;
import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableFillCommand;
/**
 * angefangen von Mattes weiterverarbeitet von Gert Kommaersetzung, Pfadangabe
 * eingefügt, Tabellenname mit Jooq verknüpft
 * 
 */
public class DocumentTopicFill extends TableFillCommand {

	@Override
	public void fillTable() throws SQLException {
		/** MIT-JOOQ-START 
		ResultSet rsTopics = this.database.executeQuery("SELECT * FROM " 
				+ TOPIC.getName() + " WHERE " 
				+ TOPIC.HIERARCHICAL_TOPIC$START.getName() + "<" 
				+ TOPIC.HIERARCHICAL_TOPIC$END.getName());
		MIT-JOOQ-ENDE */ 
		/** OHNE_JOOQ-START */ 
		ResultSet rsTopics = this.database.executeQuery("SELECT * FROM " 
				+ "TOPIC" + " WHERE " 
				+ "TOPIC.HIERARCHICAL_TOPIC$START" + "<" 
				+ "TOPIC.HIERARCHICAL_TOPIC$END");
		/** OHNE_JOOQ-ENDE */ 
		rsTopics.last();
		int rowCount = rsTopics.getRow();
		
		int topicIds[] = new int[rowCount];
		int numTokens[] = new int[rowCount];
		int clusterStart[] = new int[rowCount];
		int clusterEnd[] = new int[rowCount];
		
		int i = 0;
		rsTopics.beforeFirst();
		/** MIT-JOOQ-START 
		while(rsTopics.next()) {
			topicIds[i] = rsTopics.getInt(TOPIC.TOPIC_ID.getName());
			numTokens[i] = rsTopics.getInt(TOPIC.NUMBER_OF_TOKENS.getName());
			clusterStart[i] = rsTopics.getInt(TOPIC.HIERARCHICAL_TOPIC$START.getName());
			clusterEnd[i] = rsTopics.getInt(TOPIC.HIERARCHICAL_TOPIC$END.getName());
			i++;
		}
		
		for(i = 0; i < rowCount; i++) {
			database.executeUpdateQuery("INSERT INTO " 
					+ DOCUMENT_TOPIC.getName() + " (" 
					+ DOCUMENT_TOPIC.TOPIC_ID.getName() + ","
					+ DOCUMENT_TOPIC.DOCUMENT_ID.getName() + ","
					+ DOCUMENT_TOPIC.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT.getName() + ","
					+ DOCUMENT_TOPIC.PR_TOPIC_GIVEN_DOCUMENT.getName() + "," 
					+ DOCUMENT_TOPIC.PR_DOCUMENT_GIVEN_TOPIC.getName() + ") SELECT "
					+ topicIds[i] + ","
					+ DOCUMENT_TOPIC.DOCUMENT_ID.getName() + ","
					+ "SUM(" + DOCUMENT_TOPIC.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT.getName() + ")," 
					+ "SUM(" + DOCUMENT_TOPIC.PR_TOPIC_GIVEN_DOCUMENT.getName() + ")," 
					+ "SUM(" + DOCUMENT_TOPIC.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT.getName() + ") / T." 
					+ TOPIC.NUMBER_OF_TOKENS.getName()
					+ " FROM "
					+ DOCUMENT_TOPIC.getName() + "," 
					+ TOPIC.getName() + " TB,"
					+ TOPIC.getName() + " T WHERE "
					+ DOCUMENT_TOPIC.getName() + "." 
					+ DOCUMENT_TOPIC.TOPIC_ID.getName() + "=TB."
					+ TOPIC.TOPIC_ID.getName() + " AND "
					+ "TB." + TOPIC.HIERARCHICAL_TOPIC$START.getName() + "="
					+ "TB." + TOPIC.HIERARCHICAL_TOPIC$END.getName() + " AND "
					+ "TB." + TOPIC.HIERARCHICAL_TOPIC$START.getName() + ">="
					+ clusterStart[i] + " AND "
					+ "TB." + TOPIC.HIERARCHICAL_TOPIC$END.getName() + "<="
					+ clusterEnd[i] + " AND "
					+ "T." + TOPIC.TOPIC_ID.getName() + "="
					+ topicIds[i]
					+ " GROUP BY " + DOCUMENT_TOPIC.DOCUMENT_ID.getName()
					+ ", T." + TOPIC.NUMBER_OF_TOKENS.getName());
			
		}	
		MIT-JOOQ-ENDE */ 
		/** OHNE_JOOQ-START */
		while(rsTopics.next()) {
			topicIds[i] = rsTopics.getInt("TOPIC.TOPIC_ID");
			numTokens[i] = rsTopics.getInt("TOPIC.NUMBER_OF_TOKENS");
			clusterStart[i] = rsTopics.getInt("TOPIC.HIERARCHICAL_TOPIC$START");
			clusterEnd[i] = rsTopics.getInt("TOPIC.HIERARCHICAL_TOPIC$END");
			i++;
		}
		
		for(i = 0; i < rowCount; i++) {
			database.executeUpdateQuery("INSERT INTO " 
					+ "DOCUMENT_TOPIC" + " (" 
					+ "DOCUMENT_TOPIC.TOPIC_ID" + ","
					+ "DOCUMENT_TOPIC.DOCUMENT_ID" + ","
					+ "DOCUMENT_TOPIC.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT" + ","
					+ "DOCUMENT_TOPIC.PR_TOPIC_GIVEN_DOCUMENT" + "," 
					+ "DOCUMENT_TOPIC.PR_DOCUMENT_GIVEN_TOPIC" + ") SELECT "
					+ topicIds[i] + ","
					+ "DOCUMENT_TOPIC.DOCUMENT_ID" + ","
					+ "SUM(" + "DOCUMENT_TOPIC.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT" + ")," 
					+ "SUM(" + "DOCUMENT_TOPIC.PR_TOPIC_GIVEN_DOCUMENT" + ")," 
					+ "SUM(" + "DOCUMENT_TOPIC.NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT" + ") / T." 
					+ "TOPIC.NUMBER_OF_TOKENS"
					+ " FROM "
					+ "DOCUMENT_TOPIC" + "," 
					+ "TOPIC" + " TB,"
					+ "TOPIC" + " T WHERE "
					+ "DOCUMENT_TOPIC" + "." 
					+ "DOCUMENT_TOPIC.TOPIC_ID" + "=TB."
					+ "TOPIC.TOPIC_ID" + " AND "
					+ "TB." + "TOPIC.HIERARCHICAL_TOPIC$START" + "="
					+ "TB." + "TOPIC.HIERARCHICAL_TOPIC$END" + " AND "
					+ "TB." + "TOPIC.HIERARCHICAL_TOPIC$START" + ">="
					+ clusterStart[i] + " AND "
					+ "TB." + "TOPIC.HIERARCHICAL_TOPIC$END" + "<="
					+ clusterEnd[i] + " AND "
					+ "T." + "TOPIC.TOPIC_ID" + "="
					+ topicIds[i]
					+ " GROUP BY " + "DOCUMENT_TOPIC.DOCUMENT_ID"
					+ ", T." + "TOPIC.NUMBER_OF_TOKENS");
			
		}
		/** OHNE_JOOQ-ENDE */ 
	}	

	@Override
	public void setTableName() {
	/** MIT-JOOQ-START 
		this.tableName = DOCUMENT_TOPIC.getName();
	MIT-JOOQ-ENDE */
	/** OHNE_JOOQ-START */ 	
		this.tableName = "DOCUMENT_TOPIC";
	/** OHNE_JOOQ-ENDE */ 
	}
	
	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentTopicFill");
		beforeDependencies.add("HierarchicalTopic_TopicFill");
	}	
}
