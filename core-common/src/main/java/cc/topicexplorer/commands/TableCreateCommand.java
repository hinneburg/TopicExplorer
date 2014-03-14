package cc.topicexplorer.commands;

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
			if (!exceptionIsTolerated(e.getErrorCode())) {
				logger.error("Document.dropTable: Cannot drop table " + this.tableName);
				throw new RuntimeException(e);
			}
		}
	}

	private boolean exceptionIsTolerated(int errorCode) {
		return errorCode == 1091;
	}

}
