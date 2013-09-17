package cc.topicexplorer.plugin.link.preprocessing.tables.document;

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
		
		if (Boolean.parseBoolean(properties.getProperty("plugin_link"))) {
			/** MIT-JOOQ-START
			database.executeUpdateQuery("UPDATE " + this.tableName + " d, "
					+ " (SELECT " + properties.getProperty("Link_OrgTableIndegree")
					+ " AS indeg, " + properties.getProperty("Link_OrgTableLink")
					+ " AS link, " + properties.getProperty("OrgTableId")
					+ " AS id FROM " + properties.getProperty("OrgTableName")
					+ " ) AS org SET "
					+ DOCUMENT.LINK$URL.getName() + " = org.link, "
					+ DOCUMENT.LINK$IN_DEGREE.getName() + " = org.indeg"
					+ " WHERE org.id = d." + DOCUMENT.DOCUMENT_ID.getName());
			MIT-JOOQ-ENDE */ 
			/** OHNE_JOOQ-START */ 	
			database.executeUpdateQuery("UPDATE " + this.tableName + " d, "
					+ " (SELECT " + properties.getProperty("Link_OrgTableIndegree")
					+ " AS indeg, " + properties.getProperty("Link_OrgTableLink")
					+ " AS link, " + properties.getProperty("OrgTableId")
					+ " AS id FROM " + properties.getProperty("OrgTableName")
					+ " ) AS org SET "
					+ "DOCUMENT.LINK$URL" + " = org.link, "
					+ "DOCUMENT.LINK$IN_DEGREE" + " = org.indeg"
					+ " WHERE org.id = d." + "DOCUMENT.DOCUMENT_ID");
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
		beforeDependencies.add("Link_DocumentCreate");
	}	
}