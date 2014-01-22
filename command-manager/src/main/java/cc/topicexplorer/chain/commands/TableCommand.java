package cc.topicexplorer.chain.commands;

import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.database.Database;

public abstract class TableCommand extends DependencyCommand {

	protected Properties properties;
	protected cc.topicexplorer.database.Database database;

	protected String tableName;
	protected String dependencies = "";

	@Override
	public void specialExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		this.properties = (Properties) communicationContext.get("properties");
		this.database = (Database) communicationContext.get("database");

		setTableName();
		tableExecute(context);
	}

	/**
	 * Executes the logic, specified in the concrete {@link TableCommand}
	 * 
	 * @throws SQLException
	 *             if such an exception occurs in the concrete
	 *             {@link TableCommand} implementation. See the database
	 *             specification for detailed information on the error code
	 */
	public abstract void tableExecute(Context context);

	public abstract void setTableName();
}
