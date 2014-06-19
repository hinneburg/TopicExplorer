package cc.topicexplorer.plugin.pos.preprocessing.tables;

import java.sql.SQLException;
import cc.topicexplorer.commands.TableCreateCommand;


public class PosCreate extends TableCreateCommand {

	@Override
	public void createTable() {
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName
					+ " ADD COLUMN TOPIC.COLOR_TOPIC$COLOR VARCHAR(10) COLLATE utf8_bin");
		} catch (SQLException e) {
			logger.error("Column TOPIC.COLOR_TOPIC$COLOR could not be added to table " + this.tableName);
			throw new RuntimeException(e);
		}
	}

	private void dropColumns() {
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName + " DROP COLUMN TOPIC.COLOR_TOPIC$COLOR");
		} catch (SQLException e) {
			if (e.getErrorCode() != 1091) { // MySQL Error code for 'Can't DROP
				// ..; check that column/key exists
				logger.error("TopicMetaData.dropColumns: Cannot drop column.");
				throw new RuntimeException(e);
			} else {
				logger.info("dropColumns: ignored SQL-Exception with error code 1091.");
			}
		}
	}

	@Override
	public void dropTable() {
		this.dropColumns();
	}

	@Override
	public void setTableName() {
		this.tableName = "TOPIC";
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("PosCreate");
	}

}
