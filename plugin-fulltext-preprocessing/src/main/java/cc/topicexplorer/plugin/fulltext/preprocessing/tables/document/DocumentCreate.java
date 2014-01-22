package cc.topicexplorer.plugin.fulltext.preprocessing.tables.document;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableCreateCommand;

/**
 * @author user
 * 
 */
public class DocumentCreate extends TableCreateCommand {

	@Override
	public void createTable() {
		try {
			database.executeUpdateQuery("ALTER IGNORE TABLE `" + this.tableName
					+ "` ADD COLUMN FULLTEXT$FULLTEXT TEXT COLLATE UTF8_BIN;");
		} catch (SQLException e) {
			logger.error("Column FULLTEXT$FULLTEXT could not be added to table " + this.tableName);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void dropTable() {
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName
					+ " DROP COLUMN ADD COLUMN FULLTEXT$FULLTEXT");
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