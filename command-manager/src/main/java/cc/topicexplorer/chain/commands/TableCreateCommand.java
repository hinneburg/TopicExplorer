package cc.topicexplorer.chain.commands;

import java.sql.SQLException;

import org.apache.commons.chain.Context;

public abstract class TableCreateCommand extends TableCommand {

	@Override
	public void tableExecute(Context context) {
		dropTable();
		createTable();
	}

	public abstract void createTable();

	public void dropTable() {
		try {
			database.dropTable(this.tableName);
		} catch (SQLException e) {
			if (e.getErrorCode() != 1091) { // MySQL Error code for: 'Can't
											// DROP..; check that column/key
											// exists
				logger.error("Document.dropTable: Cannot drop table " + this.tableName);
				throw new RuntimeException(e);
			}
		}
	}
}
