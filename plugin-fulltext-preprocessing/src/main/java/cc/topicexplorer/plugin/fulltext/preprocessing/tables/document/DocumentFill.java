package cc.topicexplorer.plugin.fulltext.preprocessing.tables.document;

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
		/** MIT-JOOQ-START 
		if (Boolean.parseBoolean(properties.getProperty("plugin_fulltext"))) {
			database.executeUpdateQuery("UPDATE " + DOCUMENT.getName() + " d, "
					+ properties.getProperty("OrgTableName") + " org SET d."
					+ DOCUMENT.FULLTEXT$FULLTEXT.getName() + " = org."
					+ properties.getProperty("Fulltext_OrgTableFulltext")
					+ " WHERE d." + DOCUMENT.DOCUMENT_ID.getName() + " = org."
					+ properties.getProperty("OrgTableId"));
			database.executeUpdateQuery("ALTER IGNORE TABLE `" + DOCUMENT.getName()
					+ "` ADD FULLTEXT KEY FULLTEXT$FULLTEXT_IDX (" 
					+ DOCUMENT.FULLTEXT$FULLTEXT.getName() + ")");
		}
		MIT-JOOQ-ENDE */ 
		/** OHNE_JOOQ-START */ 
		if (Boolean.parseBoolean(properties.getProperty("plugin_fulltext"))) {
			database.executeUpdateQuery("UPDATE " + "DOCUMENT" + " d, "
					+ properties.getProperty("OrgTableName") + " org SET d."
					+ "DOCUMENT.FULLTEXT$FULLTEXT" + " = org."
					+ properties.getProperty("Fulltext_OrgTableFulltext")
					+ " WHERE d." + "DOCUMENT.DOCUMENT_ID" + " = org."
					+ properties.getProperty("OrgTableId"));
			database.executeUpdateQuery("ALTER IGNORE TABLE `" + "DOCUMENT"
					+ "` ADD FULLTEXT KEY FULLTEXT$FULLTEXT_IDX (" 
					+ "DOCUMENT.FULLTEXT$FULLTEXT" + ")");
		}
		/** OHNE_JOOQ-ENDE */ 
	}

	@Override
	public void setTableName() {
		tableName = "DOCUMENT";
	}
	
	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentFill");
		beforeDependencies.add("Fulltext_DocumentCreate");
	}
}