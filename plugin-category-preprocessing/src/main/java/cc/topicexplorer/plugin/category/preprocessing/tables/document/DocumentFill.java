package cc.topicexplorer.plugin.category.preprocessing.tables.document;

import java.sql.SQLException;
/** MIT-JOOQ-START 
 import static jooq.generated.Tables.DOCUMENT; 
 MIT-JOOQ-ENDE */
import cc.topicexplorer.chain.commands.TableFillCommand;

public class DocumentFill extends TableFillCommand {

	@Override
	public void fillTable() {
		/**
		 * MIT-JOOQ-START if
		 * (Boolean.parseBoolean(properties.getProperty("plugin_category"))) {
		 * database.executeUpdateQuery("UPDATE " + this.tableName + " d, " +
		 * properties.getProperty("OrgTableName") + " org SET d." +
		 * DOCUMENT.CATEGORY$CATEGORY_ID.getName() + " = org." +
		 * properties.getProperty("Category_OrgTableCat") + " WHERE d." +
		 * DOCUMENT.DOCUMENT_ID.getName() + " = org." +
		 * properties.getProperty("OrgTableId"));
		 * 
		 * 
		 * database.executeUpdateQuery("ALTER IGNORE TABLE " + this.tableName +
		 * " ADD KEY CATEGORY_DOCUMENT_IDX (`" + DOCUMENT.DOCUMENT_ID.getName()
		 * + "`,`" + DOCUMENT.CATEGORY$CATEGORY_ID.getName() + "`) "); }
		 * MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		if (Boolean.parseBoolean(properties.getProperty("plugin_category"))) {
			try {
				database.executeUpdateQuery("UPDATE " + this.tableName + " d, "
						+ properties.getProperty("OrgTableName") + " org SET d." + "DOCUMENT.CATEGORY$CATEGORY_ID"
						+ " = org." + properties.getProperty("Category_OrgTableCat") + " WHERE d."
						+ "DOCUMENT.DOCUMENT_ID" + " = org." + properties.getProperty("OrgTableId"));

				database.executeUpdateQuery("ALTER IGNORE TABLE " + this.tableName
						+ " ADD KEY CATEGORY_DOCUMENT_IDX (`" + "DOCUMENT.DOCUMENT_ID" + "`,`"
						+ "DOCUMENT.CATEGORY$CATEGORY_ID" + "`) ");
			} catch (SQLException e) {
				logger.error("Table " + this.tableName + " could not be filled properly.");
				throw new RuntimeException(e);
			}
		}
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public void setTableName() {
		/**
		 * MIT-JOOQ-START tableName = DOCUMENT.getName(); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		tableName = "DOCUMENT";
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentFill");
		beforeDependencies.add("Category_DocumentCreate");
	}
}
