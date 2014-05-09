package cc.topicexplorer.plugin.time.preprocessing.tables.documenttermtopic;

import java.sql.SQLException;

import cc.topicexplorer.commands.TableFillCommand;

/**
 * MIT-JOOQ-START import static jooq.generated.Tables.DOCUMENT_TERM_TOPIC;
 * MIT-JOOQ-ENDE
 */

public class DocumentTermTopicFill extends TableFillCommand {
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
	public void fillTable() {

		if (Boolean.parseBoolean(this.properties.getProperty("plugin_time"))) {
			/**
			 * MIT-JOOQ-START database.executeUpdateQuery("UPDATE " +
			 * DOCUMENT_TERM_TOPIC.getName() + " tta, " +
			 * properties.getProperty("OrgTableName") + " org SET tta." +
			 * DOCUMENT_TERM_TOPIC.TIME$WEEK.getName() +
			 * " = YEARWEEK(FROM_UNIXTIME(org." +
			 * properties.getProperty("Time_OrgTableTstamp") +
			 * " - 86400)) WHERE tta." +
			 * DOCUMENT_TERM_TOPIC.DOCUMENT_ID.getName() + " = org." +
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

	@Override
	public void setTableName() {
		/**
		 * MIT-JOOQ-START tableName = DOCUMENT_TERM_TOPIC.getName();
		 * MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		this.tableName = "DOCUMENT_TERM_TOPIC";
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentTermTopicFill");
		beforeDependencies.add("Time_DocumentTermTopicCreate");
	}

}