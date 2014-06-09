package cc.topicexplorer.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cc.commandmanager.core.Context;

public abstract class TableFillCommand extends TableCommand {

	@Override
	public void tableExecute(Context context) {
		fillTable();
	}

	/**
	 * Analyzes the table with the corresponding table name, specified in this {@link TableFillCommand}
	 * 
	 * @throws SQLException
	 *             if a database access error occurs or the analyze {@link Statement} produces a {@link ResultSet}. See
	 *             the MySQL specification for detailed information on the error code.
	 */
	public void analyzeMainTableOfCommand() throws SQLException {
		this.database.analyzeTable(this.tableName);
	}

	/**
	 * Optimizes the table with the corresponding table name, specified in this {@link TableFillCommand}
	 * 
	 * @throws SQLException
	 *             if a database access error occurs or the analyze {@link Statement} produces a {@link ResultSet}. See
	 *             the MySQL specification for detailed information on the error code.
	 */
	public void optimizeMainTableOfCommand() throws SQLException {
		this.database.optimizeTable(this.tableName);
	}

	public abstract void fillTable();
}
