package cc.topicexplorer.plugin.category.preprocessing.tables.document;

import java.sql.SQLException;

import cc.topicexplorer.commands.TableCreateCommand;

public class DocumentCreate extends TableCreateCommand {

	@Override
	public void createTable() {
		try {
			database.executeUpdateQuery("ALTER IGNORE TABLE " + this.tableName
					+ " ADD COLUMN CATEGORY$CATEGORY_ID INTEGER(11) NOT NULL ");
		} catch (SQLException e) {
			logger.error("Column CATEGORY$CATEGORY_ID could not be added to table " + this.tableName);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void dropTable() {
		try {
			database.executeUpdateQuery("ALTER IGNORE TABLE " + this.tableName + " DROP COLUMN CATEGORY$CATEGORY_ID");
		} catch (SQLException e) {
			if (e.getErrorCode() != 1091) { // MySQL Error code for 'Can't DROP
											// ..; check that column/key exists
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
