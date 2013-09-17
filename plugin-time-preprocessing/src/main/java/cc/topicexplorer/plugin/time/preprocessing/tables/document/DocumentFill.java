package cc.topicexplorer.plugin.time.preprocessing.tables.document;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableFillCommand;
/** MIT-JOOQ-START 
import static jooq.generated.Tables.DOCUMENT;
MIT-JOOQ-ENDE */ 
	

/**
 * @author user
 * 
 */
public class DocumentFill extends TableFillCommand {

	@Override
	public void fillTable() throws SQLException {

		if (Boolean.parseBoolean(properties.getProperty("plugin_time"))) {
			/** MIT-JOOQ-START 
			database.executeUpdateQuery("UPDATE " + this.tableName + " d, "
					+ properties.getProperty("OrgTableName") + " org SET d."
					+ DOCUMENT.TIME$TIME_STAMP.getName() + " = org."
					+ properties.getProperty("Time_OrgTableTstamp")
					+ " WHERE d." + DOCUMENT.DOCUMENT_ID.getName() + " = org."
					+ properties.getProperty("OrgTableId"));
					MIT-JOOQ-ENDE */ 
			/** OHNE_JOOQ-START */ 	
			database.executeUpdateQuery("UPDATE " + this.tableName + " d, "
					+ properties.getProperty("OrgTableName") + " org SET d."
					+ "DOCUMENT.TIME$TIME_STAMP" + " = org."
					+ properties.getProperty("Time_OrgTableTstamp")
					+ " WHERE d." + "DOCUMENT.DOCUMENT_ID" + " = org."
					+ properties.getProperty("OrgTableId"));
			/** OHNE_JOOQ-ENDE */ 
		}

	}

	@Override
	public void setTableName() {
	/** MIT-JOOQ-START 
		tableName = DOCUMENT.getName();
		MIT-JOOQ-ENDE */ 
	/** OHNE_JOOQ-START */ 	
		this.tableName = "DOCUMENT";
	/** OHNE_JOOQ-ENDE */ 
	}
	
	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentFill");
		beforeDependencies.add("Time_DocumentCreate");
	}	

}