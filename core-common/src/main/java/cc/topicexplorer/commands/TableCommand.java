package cc.topicexplorer.commands;

import java.sql.SQLException;
import java.util.Properties;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.topicexplorer.database.Database;

public abstract class TableCommand implements Command {

	protected Properties properties;
	protected Database database;

	protected String tableName;
	protected String dependencies = "";

	@Override
	public void execute(Context context) {
		properties = context.get("properties", Properties.class);
		database = context.get("database", Database.class);

		setTableName();
		tableExecute(context);
	}

	/**
	 * Executes the logic, specified in the concrete {@link TableCommand}
	 * 
	 * @throws SQLException
	 *             if such an exception occurs in the concrete {@link TableCommand} implementation. See the database
	 *             specification for detailed information on the error code
	 */
	public abstract void tableExecute(Context context);

	public abstract void setTableName();
}
