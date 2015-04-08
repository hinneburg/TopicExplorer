package cc.topicexplorer.plugin.time.preprocessing.tables.document;

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

		if (Boolean.parseBoolean(properties.getProperty("plugin_time"))) {
			/**
			 * MIT-JOOQ-START database.executeUpdateQuery("UPDATE " + this.tableName + " d, " +
			 * properties.getProperty("OrgTableName") + " org SET d." + DOCUMENT.TIME$TIME_STAMP.getName() + " = org." +
			 * properties.getProperty("Time_OrgTableTstamp") + " WHERE d." + DOCUMENT.DOCUMENT_ID.getName() + " = org."
			 * + properties.getProperty("OrgTableId")); MIT-JOOQ-ENDE
			 */
			/** OHNE_JOOQ-START */
			try {
				database.executeUpdateQuery("UPDATE " + this.tableName + " d, orgTable_meta "
						+ "org SET d.TIME$TIME_STAMP=UNIX_TIMESTAMP(org.DOCUMENT_DATE) "
						+ "WHERE d.DOCUMENT_ID=org.DOCUMENT_ID");
			} catch (SQLException e) {
				logger.error("Table " + this.tableName + " could not be filled properly.");
				throw new RuntimeException(e);
			}
			/** OHNE_JOOQ-ENDE */
		}

	}

	@Override
	public void setTableName() {
		/**
		 * MIT-JOOQ-START tableName = DOCUMENT.getName(); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		this.tableName = "DOCUMENT";
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentFill", "Time_DocumentCreate");
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
