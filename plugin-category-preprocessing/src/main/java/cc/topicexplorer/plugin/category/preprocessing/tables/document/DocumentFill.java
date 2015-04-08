package cc.topicexplorer.plugin.category.preprocessing.tables.document;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

/** MIT-JOOQ-START 
 import static jooq.generated.Tables.DOCUMENT; 
 MIT-JOOQ-ENDE */
import cc.topicexplorer.commands.TableFillCommand;

import com.google.common.collect.Sets;

public class DocumentFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(DocumentFill.class);

	@Override
	public void fillTable() {
		/**
		 * MIT-JOOQ-START if (Boolean.parseBoolean(properties.getProperty("plugin_category"))) {
		 * database.executeUpdateQuery("UPDATE " + this.tableName + " d, " + properties.getProperty("OrgTableName") +
		 * " org SET d." + DOCUMENT.CATEGORY$CATEGORY_ID.getName() + " = org." +
		 * properties.getProperty("Category_OrgTableCat") + " WHERE d." + DOCUMENT.DOCUMENT_ID.getName() + " = org." +
		 * properties.getProperty("OrgTableId"));
		 * 
		 * 
		 * database.executeUpdateQuery("ALTER IGNORE TABLE " + this.tableName + " ADD KEY CATEGORY_DOCUMENT_IDX (`" +
		 * DOCUMENT.DOCUMENT_ID.getName() + "`,`" + DOCUMENT.CATEGORY$CATEGORY_ID.getName() + "`) "); } MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		if (Boolean.parseBoolean(properties.getProperty("plugin_category"))) {
			try {
				database.executeUpdateQuery("UPDATE " + this.tableName + " d, "
						+ "orgTable_meta org SET d.DOCUMENT.CATEGORY$CATEGORY_ID"
						+ " = org.CATEGORY WHERE d."
						+ "DOCUMENT.DOCUMENT_ID=org.DOCUMENT_ID");

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
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentFill", "Category_DocumentCreate");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}
}
