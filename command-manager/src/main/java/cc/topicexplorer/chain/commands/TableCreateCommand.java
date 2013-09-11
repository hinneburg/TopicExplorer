package cc.topicexplorer.chain.commands;

import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.chain.Context;
import org.jooq.impl.Factory;

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
