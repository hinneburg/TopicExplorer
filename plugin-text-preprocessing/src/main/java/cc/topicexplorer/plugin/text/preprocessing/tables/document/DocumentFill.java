package cc.topicexplorer.plugin.text.preprocessing.tables.document;

import java.sql.SQLException;
import java.util.ArrayList;

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
		if (Boolean.parseBoolean(properties.getProperty("plugin_text"))) {
			database.executeUpdateQuery("UPDATE " + DOCUMENT.getName() + " d, "
					+ properties.getProperty("OrgTableName") + " org SET d."
					+ DOCUMENT.TEXT$FULLTEXT.getName() + " = org."
					+ properties.getProperty("Text_OrgTableTxt") + ", "
					+ DOCUMENT.TEXT$TITLE.getName() + " = org."
					+ properties.getProperty("Text_OrgTableTitle") 					
					+ " WHERE d." + DOCUMENT.DOCUMENT_ID.getName() + " = org."
					+ properties.getProperty("OrgTableId"));
			database.executeUpdateQuery("ALTER IGNORE TABLE `" + DOCUMENT.getName()
					+ "` ADD KEY TEXT$TITLE_IDX (" + DOCUMENT.TEXT$TITLE.getName() + "),"
					+ " ADD FULLTEXT KEY TEXT$FULLTEXT_IDX (" + DOCUMENT.TEXT$FULLTEXT.getName() + ");");
		}
 MIT-JOOQ-ENDE */
/** OHNE_JOOQ-START */ 
		if (Boolean.parseBoolean(properties.getProperty("plugin_text"))) {
			database.executeUpdateQuery("UPDATE " + "DOCUMENT" + " d, "
					+ properties.getProperty("OrgTableName") + " org SET d."
					+ "DOCUMENT.TEXT$FULLTEXT" + " = org."
					+ properties.getProperty("Text_OrgTableTxt") + ", "
					+ "DOCUMENT.TEXT$TITLE" + " = org."
					+ properties.getProperty("Text_OrgTableTitle") 					
					+ " WHERE d." + "DOCUMENT.DOCUMENT_ID" + " = org."
					+ properties.getProperty("OrgTableId"));
			database.executeUpdateQuery("ALTER IGNORE TABLE `" + "DOCUMENT"
					+ "` ADD KEY TEXT$TITLE_IDX (" + "DOCUMENT.TEXT$TITLE" + "),"
					+ " ADD FULLTEXT KEY TEXT$FULLTEXT_IDX (" + "DOCUMENT.TEXT$FULLTEXT" + ");");
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
		beforeDependencies.add("Text_DocumentCreate");
	}	
}