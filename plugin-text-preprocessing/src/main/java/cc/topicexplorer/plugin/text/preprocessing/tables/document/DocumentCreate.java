package cc.topicexplorer.plugin.text.preprocessing.tables.document;

import java.sql.SQLException;

import cc.topicexplorer.commands.TableCreateCommand;

public class DocumentCreate extends TableCreateCommand {

	@Override
	public void createTable() {
		try {
			this.database.executeUpdateQuery("ALTER IGNORE TABLE " + this.tableName
					+ " ADD COLUMN TEXT$FULLTEXT TEXT COLLATE UTF8_BIN,"
					+ " ADD COLUMN TEXT$TITLE VARCHAR(255) COLLATE UTF8_BIN");
		} catch (SQLException e) {
			logger.error("Columns TEXT$FULLTEXT, $TITLE could not be added to table " + this.tableName);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void dropTable() {
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName + " DROP COLUMN TEXT$FULLTEXT,"
					+ " DROP COLUMN TEXT$TITLE");
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
		tableName = "DOCUMENT";
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentCreate");
	}
}