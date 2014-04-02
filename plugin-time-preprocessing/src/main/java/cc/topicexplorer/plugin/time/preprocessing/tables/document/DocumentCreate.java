package cc.topicexplorer.plugin.time.preprocessing.tables.document;

import java.sql.SQLException;

import cc.topicexplorer.commands.TableCreateCommand;

public class DocumentCreate extends TableCreateCommand {

	@Override
	public void createTable() {
		try {
			database.executeUpdateQuery("ALTER IGNORE TABLE `" + this.tableName
					+ "` ADD COLUMN TIME$TIME_STAMP INT(11)");
		} catch (SQLException e) {
			logger.error("Column TIME$TIME_STAMP could not be added to the table" + this.tableName);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void dropTable() {
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName + " DROP COLUMN TIME$TIME_STAMP");
		} catch (SQLException e) {
			if (e.getErrorCode() != 1091) { // MySQL Error code for: 'Can't //
											// DROP..; check that column/key //
											// exists
				logger.error("Document.dropColumns: Cannot drop column.");
				throw new RuntimeException(e);
			} else {
				logger.info("dropColumns: ignored SQL-Exception with error code 1091.");
			}
		}
	}

	@Override
	public void setTableName() {
		tableName = "DOCUMENT";
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentCreate");
	}
}