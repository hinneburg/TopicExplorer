package cc.topicexplorer.plugin.time.preprocessing.tables.documenttermtopic;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableFillCommand;
/** MIT-JOOQ-START 
import static jooq.generated.Tables.DOCUMENT_TERM_TOPIC;
MIT-JOOQ-ENDE */ 


/**
 * @author user
 * 
 */
public class DocumentTermTopicFill extends TableFillCommand {

	@Override
	public void fillTable() throws SQLException {

		if(Boolean.parseBoolean(this.properties.getProperty("plugin_time"))) {
			/** MIT-JOOQ-START 
			database.executeUpdateQuery("UPDATE " + DOCUMENT_TERM_TOPIC.getName() + " tta, " 
					+ properties.getProperty("OrgTableName") 
					+ " org SET tta." + DOCUMENT_TERM_TOPIC.TIME$WEEK.getName()
					+ " = YEARWEEK(FROM_UNIXTIME(org." + properties.getProperty("Time_OrgTableTstamp")
					+ ")) WHERE tta." + DOCUMENT_TERM_TOPIC.DOCUMENT_ID.getName()
					+ " = org." + properties.getProperty("OrgTableId"));				
		MIT-JOOQ-ENDE */ 
	/** OHNE_JOOQ-START */ 	
			database.executeUpdateQuery("UPDATE " + "DOCUMENT_TERM_TOPIC" + " tta, " 
					+ properties.getProperty("OrgTableName") 
					+ " org SET tta." + "DOCUMENT_TERM_TOPIC.TIME$WEEK"
					+ " = YEARWEEK(FROM_UNIXTIME(org." + properties.getProperty("Time_OrgTableTstamp")
					+ ")) WHERE tta." + "DOCUMENT_TERM_TOPIC.DOCUMENT_ID"
					+ " = org." + properties.getProperty("OrgTableId"));
	/** OHNE_JOOQ-ENDE */ 
		}
	}

	@Override
	public void setTableName() {
	/** MIT-JOOQ-START 
		tableName = DOCUMENT_TERM_TOPIC.getName();
		MIT-JOOQ-ENDE */ 
	/** OHNE_JOOQ-START */ 	
		this.tableName = "DOCUMENT_TERM_TOPIC";
	/** OHNE_JOOQ-ENDE */ 
	}
	
	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentTermTopicFill");
		beforeDependencies.add("Time_DocumentTermTopicCreate");
	}	

}