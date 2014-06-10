package cc.topicexplorer.plugin.time.preprocessing.tables.documenttermtopic;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;

import com.google.common.collect.Sets;

/**
 * MIT-JOOQ-START import static jooq.generated.Tables.DOCUMENT_TERM_TOPIC; MIT-JOOQ-ENDE
 */

public class DocumentTermTopicFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(DocumentTermTopicFill.class);

	@Override
	public void fillTable() {

		if (timePluginIsActivated()) {
			/**
			 * MIT-JOOQ-START database.executeUpdateQuery("UPDATE " + DOCUMENT_TERM_TOPIC.getName() + " tta, " +
			 * properties.getProperty("OrgTableName") + " org SET tta." + DOCUMENT_TERM_TOPIC.TIME$WEEK.getName() +
			 * " = YEARWEEK(FROM_UNIXTIME(org." + properties.getProperty("Time_OrgTableTstamp") +
			 * " - 86400)) WHERE tta." + DOCUMENT_TERM_TOPIC.DOCUMENT_ID.getName() + " = org." +
			 * properties.getProperty("OrgTableId")); MIT-JOOQ-ENDE
			 */
			/** OHNE_JOOQ-START */
			try {
				database.executeUpdateQuery("UPDATE " + "DOCUMENT_TERM_TOPIC" + " tta, "
						+ properties.getProperty("OrgTableName") + " org SET tta." + "TIME$WEEK"
						+ " = YEARWEEK(FROM_UNIXTIME(org." + properties.getProperty("Time_OrgTableTstamp")
						+ " - 86400)) WHERE tta." + "DOCUMENT_ID" + " = org." + properties.getProperty("OrgTableId"));
			} catch (SQLException e) {
				logger.error("Table " + this.tableName + " could not be filled properly.");
				throw new RuntimeException(e);
			}
			/** OHNE_JOOQ-ENDE */
			createIndex();
		}
	}

	private boolean timePluginIsActivated() {
		return Boolean.parseBoolean(this.properties.getProperty("plugin_time"));
	}

	private void createIndex() {
		try {
			database.executeUpdateQuery("create index TIME$DOCUMENT_TERM_TOPIC$TOPIC_WEEK_IDX "
					+ "ON DOCUMENT_TERM_TOPIC(TOPIC_ID,TIME$WEEK) USING BTREE");
		} catch (SQLException e) {
			logger.error("Time-Topic-Index for Table " + this.tableName + " could not be created properly.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		/**
		 * MIT-JOOQ-START tableName = DOCUMENT_TERM_TOPIC.getName(); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		this.tableName = "DOCUMENT_TERM_TOPIC";
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentTermTopicFill", "Time_DocumentTermTopicCreate");
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
