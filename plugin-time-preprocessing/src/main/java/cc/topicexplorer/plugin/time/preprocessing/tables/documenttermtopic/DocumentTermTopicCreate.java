package cc.topicexplorer.plugin.time.preprocessing.tables.documenttermtopic;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableCreateCommand;

public class DocumentTermTopicCreate extends TableCreateCommand {

	/**
	 * @throws SQLException
	 *             if a database access error occurs or the given SQL statement
	 *             produces a ResultSet object
	 */
	@Override
	public void createTable() {
		try {
			database.executeUpdateQuery("ALTER IGNORE TABLE `" + this.tableName
					+ "` ADD COLUMN TIME$WEEK INT(11) NOT NULL DEFAULT 1");
		} catch (SQLException e) {
			logger.error(String
					.format("Table %s could not be altered. Column TIME$WEEK was not added.", this.tableName));
			throw new RuntimeException(e);
		}
	}

	@Override
	public void dropTable() {
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName + " DROP COLUMN TIME$WEEK");
		} catch (SQLException e) {
			if (e.getErrorCode() != 1091) { // MySQL Error code for 'Can't DROP
											// ..; check that column/key exists
				logger.error("DocumentTermTopic.dropColumns: Cannot drop column.");
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void setTableName() {
		tableName = "DOCUMENT_TERM_TOPIC";
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentTermTopicCreate");
	}
}