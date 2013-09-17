package cc.topicexplorer.chain.commands;

import java.sql.SQLException;

import org.apache.commons.chain.Context;

public abstract class TableCreateCommand extends TableCommand {

	@Override
	public void tableExecute(Context context) {
		try {
			dropTable();
			createTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public abstract void createTable() throws SQLException;

	public void dropTable() throws SQLException {
		database.dropTable(this.tableName);
	}
}
