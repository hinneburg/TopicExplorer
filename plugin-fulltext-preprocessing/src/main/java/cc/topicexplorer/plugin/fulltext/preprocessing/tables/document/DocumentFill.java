package cc.topicexplorer.plugin.fulltext.preprocessing.tables.document;

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
		 * MIT-JOOQ-START if (Boolean.parseBoolean(properties.getProperty("plugin_fulltext"))) {
		 * database.executeUpdateQuery("UPDATE " + DOCUMENT.getName() + " d, " + properties.getProperty("OrgTableName")
		 * + " org SET d." + DOCUMENT.FULLTEXT$FULLTEXT.getName() + " = org." +
		 * properties.getProperty("Fulltext_OrgTableFulltext") + " WHERE d." + DOCUMENT.DOCUMENT_ID.getName() +
		 * " = org." + properties.getProperty("OrgTableId")); database.executeUpdateQuery("ALTER IGNORE TABLE `" +
		 * DOCUMENT.getName() + "` ADD FULLTEXT KEY FULLTEXT$FULLTEXT_IDX (" + DOCUMENT.FULLTEXT$FULLTEXT.getName() +
		 * ")"); } MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		if (Boolean.parseBoolean(properties.getProperty("plugin_fulltext"))) {
			try {
				database.executeUpdateQuery("UPDATE " + this.tableName+ " d, ("
						+ "SELECT DOCUMENT_ID, GROUP_CONCAT(TOKEN SEPARATOR ' ') as FULL_TEXT "
						+ "FROM DOCUMENT_TERM GROUP BY DOCUMENT_ID ORDER BY POSITION_OF_TOKEN_IN_DOCUMENT) dt "
						+ "SET d.TEXT$FULLTEXT=dt.FULL_TEXT WHERE d.DOCUMENT_ID=dt.DOCUMENT_ID ");
				database.executeUpdateQuery("ALTER IGNORE TABLE `" + this.tableName + "` "
						+ "ADD FULLTEXT KEY FULLTEXT$FULLTEXT_IDX (FULLTEXT$FULLTEXT)");
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
		return Sets.newHashSet("DocumentFill", "Fulltext_DocumentCreate");
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
