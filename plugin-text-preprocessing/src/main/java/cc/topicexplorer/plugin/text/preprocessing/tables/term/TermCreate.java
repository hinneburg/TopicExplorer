package cc.topicexplorer.plugin.text.preprocessing.tables.term;

import java.sql.SQLException;

import cc.topicexplorer.commands.TableCreateCommand;

public class TermCreate extends TableCreateCommand {

	@Override
	public void createTable() {
		try {
			this.database.executeUpdateQuery("ALTER IGNORE TABLE " + this.tableName
					+ " ADD COLUMN TEXT$WORD_TYPE VARCHAR(255) COLLATE UTF8_BIN");
		} catch (SQLException e) {
			logger.error("Column TEXT$WORD_TYPE could not be added to table " + this.tableName);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void dropTable() {
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName + " DROP COLUMN TEXT$WORD_TYPE");
		} catch (SQLException e) {
			if (e.getErrorCode() != 1091) { // MySQL Error code for: 'Can't //
											// DROP..; check that column/key //
											// exists
				logger.error("Document.dropColumns: Cannot drop column.");
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void setTableName() {
		tableName = "TERM";
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("TermCreate");
	}
}
