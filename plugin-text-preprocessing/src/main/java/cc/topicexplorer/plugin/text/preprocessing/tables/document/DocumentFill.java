package cc.topicexplorer.plugin.text.preprocessing.tables.document;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;

import com.google.common.collect.Sets;

/**
 * MIT-JOOQ-START import static jooq.generated.Tables.DOCUMENT; MIT-JOOQ-ENDE
 */

public class DocumentFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(DocumentFill.class);

	@Override
	public void fillTable() {

		/**
		 * MIT-JOOQ-START if (Boolean.parseBoolean(properties.getProperty("plugin_text"))) {
		 * database.executeUpdateQuery("UPDATE " + DOCUMENT.getName() + " d, " + properties.getProperty("OrgTableName")
		 * + " org SET d." + DOCUMENT.TEXT$FULLTEXT.getName() + " = org." + properties.getProperty("Text_OrgTableTxt") +
		 * ", " + DOCUMENT.TEXT$TITLE.getName() + " = org." + properties.getProperty("Text_OrgTableTitle") + " WHERE d."
		 * + DOCUMENT.DOCUMENT_ID.getName() + " = org." + properties.getProperty("OrgTableId"));
		 * database.executeUpdateQuery("ALTER IGNORE TABLE `" + DOCUMENT.getName() + "` ADD KEY TEXT$TITLE_IDX (" +
		 * DOCUMENT.TEXT$TITLE.getName() + ")," + " ADD FULLTEXT KEY TEXT$FULLTEXT_IDX (" +
		 * DOCUMENT.TEXT$FULLTEXT.getName() + ");"); } MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		if (Boolean.parseBoolean(properties.getProperty("plugin_text"))) {
			try {
				database.executeUpdateQuery("UPDATE " + "DOCUMENT" + " d, " + properties.getProperty("OrgTableName")
						+ " org SET d." + "TEXT$FULLTEXT" + " = org." + properties.getProperty("Text_OrgTableTxt")
						+ ", " + "d.TEXT$TITLE" + " = org." + properties.getProperty("Text_OrgTableTitle")
						+ " WHERE d." + "DOCUMENT_ID" + " = org." + properties.getProperty("OrgTableId"));
				database.executeUpdateQuery("ALTER IGNORE TABLE " + "DOCUMENT" + " ADD KEY TEXT$TITLE_IDX ("
						+ "TEXT$TITLE" + ")," + " ADD FULLTEXT KEY TEXT$FULLTEXT_IDX (" + "TEXT$FULLTEXT" + ");");
			} catch (SQLException e) {
				logger.error("Table " + this.tableName + " could not be filled properly.");
				throw new RuntimeException(e);
			}
		}
		/** OHNE_JOOQ-ENDE */

	}

	@Override
	public void setTableName() {
		tableName = "DOCUMENT";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentFill", "Text_DocumentCreate");
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
