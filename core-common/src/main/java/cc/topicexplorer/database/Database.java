package cc.topicexplorer.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Database {
	private Connection connection;
	private Statement statement;
	private Properties properties;

	private int limit = -1; // limit for select queries
	private int numberOfRetries = 3; // this number gets overwritten by
										// respective property

	private String dbUser;
	private String dbPassword;
	private String dbLocation;

	private enum ExecutionType {
		EXECUTE_QUERY, EXECUTE_UPDATE_QUERY, EXECUTE_UPDATE_QUERY_FOR_UPDATE
	};

	private static final Logger logger = Logger.getLogger(Database.class);

	/**
	 * 
	 * @param prop
	 *            A {@link Properties} object which holds all the properties,
	 *            needed for the database to initialize
	 * @throws SQLException
	 *             if a database access error occurs
	 * @throws ClassNotFoundException
	 *             if the database driver could not be found
	 * @throws InstantiationException
	 *             if the instantiation fails for multiple reasons
	 * @throws IllegalAccessException
	 *             if the database driver or its default constructor is not
	 *             accessible
	 */
	public Database(Properties prop) {
		startUp(prop, false);
	}

	/**
	 * 
	 * @param prop
	 *            A {@link Properties} object which holds all the properties,
	 *            needed for the database to initialize
	 * @param otherDatabase
	 *            determines whether the another database than the default one
	 *            should be loaded. Property 'database.other' will be used to
	 *            define the dbLocation of the other database
	 * @throws SQLException
	 *             if a database access error occurs
	 * @throws ClassNotFoundException
	 *             if the database driver could not be found
	 * @throws InstantiationException
	 *             if the instantiation fails for different possible reasons
	 * @throws IllegalAccessException
	 *             if the database driver or its default constructor is not
	 *             accessible
	 */
	public Database(Properties prop, Boolean otherDatabase) {
		startUp(prop, otherDatabase);
	}

	private void startUp(Properties prop, Boolean otherDatabase) {
		this.properties = prop;

		this.setProperties(otherDatabase);
		this.loadDatabaseDriver();
		this.connect();
		this.prepareConnection();
	}

	private void prepareConnection() {
		try {
			this.statement = this.connection.createStatement();
			this.statement.setFetchSize(100);
		} catch (SQLException e) {
			logger.error("The statement caused an exception. SQL-State-Error-Code: " + e.getSQLState());
			throw new RuntimeException(e);
		}

		try {
			this.connection.setAutoCommit(true);
		} catch (SQLException e) {
			logger.error("AutoCommit could not be set.");
			throw new RuntimeException(e);
		}

	}

	private void setProperties(Boolean otherDatabase) {
		this.dbLocation = this.properties.getProperty("database.DbLocation");

		if (otherDatabase) {
			dbLocation = dbLocation + "/" + this.properties.getProperty("database.other");
		} else {
			dbLocation = dbLocation + "/" + this.properties.getProperty("database.DB");
		}

		this.dbUser = this.properties.getProperty("database.DbUser");
		this.dbPassword = this.properties.getProperty("database.DbPassword");

		this.numberOfRetries = Integer.parseInt(this.properties.getProperty("database.NumberOfRetries"));
	}

	private void loadDatabaseDriver() {
		// load Database-Driver
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (ClassNotFoundException e1) {
			logger.error("Current Command : [ " + getClass() + " ]" + " Database-Driver class could not be found");
			throw new RuntimeException(e1);
		} catch (InstantiationException e2) {
			logger.error("Current Command : [ " + getClass() + " ]"
					+ " Database-Driver instantiation fails for different possible reasons");
			throw new RuntimeException(e2);
		} catch (IllegalAccessException e3) {
			logger.error("Current Command : [ " + getClass() + " ]"
					+ " Database-Driver database driver or its default constructor is not accessible");
			throw new RuntimeException(e3);
		}
	}

	/**
	 * 
	 * @throws SQLException
	 *             if a database access error occurs. See the MySQL
	 *             specification for detailed information on the error code
	 */
	public void shutdownDB() throws SQLException {
		if (!this.connection.isClosed()) {
			this.connection.close();
		}
	}

	private Object executeQueriesWithSilentRetry(String query, ExecutionType execMethod) throws SQLException {
		boolean queryCompleted = false;
		int retryCount = this.numberOfRetries;
		Object result = null;
		do {
			try {

				switch (execMethod) {

				case EXECUTE_QUERY: {
					this.statement = this.connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
					result = this.statement.executeQuery(query);

					queryCompleted = true;
					break;
				}
				case EXECUTE_UPDATE_QUERY: {
					// for manipulation , executeQuery couldn't manipulate
					// database
					result = this.statement.executeUpdate(query);

					queryCompleted = true;
					break;
				}
				case EXECUTE_UPDATE_QUERY_FOR_UPDATE: {
					// useful to keep resultset open,
					// else keywords_themen2 or tables.keytopic2 are not working
					// exception (cannot perfom ... rs is closed) would be
					// thrown
					this.statement = this.connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
					result = this.statement.executeUpdate(query);

					queryCompleted = true;
					break;
				}
				}

			} catch (SQLException e) {
				//
				// 'Retry-able' SQL-State is 08S01 for Communication Error
				// Retry only if error due to stale or dead connection
				//

				String sqlState = e.getSQLState();

				if ("08S01".equals(sqlState)) {
					retryCount--;
					connection.close();
					this.connect();
					this.prepareConnection();
				} else {
					logger.error("The statement " + query + " caused in (re)try "
							+ (this.numberOfRetries - retryCount + 1)
							+ " an SQLException exception that is not retryable. " + "Error-Code: " + e.getErrorCode()
							+ ", SQL-State-Error-Code: " + e.getSQLState());
					retryCount = 0;
					throw e;
				}
			}
		} while (!queryCompleted && (retryCount > 0));

		if (!queryCompleted && (retryCount == 0)) {
			logger.error("The statement  " + query + " caused an retryable SQL exception and was "
					+ this.numberOfRetries + " times retried. "
					+ "Retryable Exceptions are those with SQL State 08S01.");
			throw new RuntimeException();
		}

		if (result == null) {
			logger.error("The statement  " + query + " caused result Object that is NULL ");
			throw new RuntimeException();
		}
		return result;
	}

	/**
	 * @param query
	 *            SQL query that will be executed
	 * @return a ResultSet object that contains the data produced by the given
	 *         query; The statement might be sent numberOfRetries times to the
	 *         server if an connection error occurred (SQL-State-Error-Code
	 *         08S01).
	 * @throws SQLException
	 *             if a database access error occurs,
	 *             <p>
	 *             this method is called on a closed statement,
	 *             <p>
	 *             the given SQL statement produces anything other than a single
	 *             ResultSet object, never null
	 *             <p>
	 *             the method is called on a PreparedStatement or
	 *             CallableStatement or
	 *             <p>
	 *             See the MySQL specification for detailed information on the
	 *             error code
	 */
	public ResultSet executeQuery(String query) throws SQLException {
		return (ResultSet) executeQueriesWithSilentRetry(query, ExecutionType.EXECUTE_QUERY);
	}

	/**
	 * for manipulation of database without "closing" the connection/ resultset
	 * for insert and update in loops
	 * 
	 * incl statement = connection..
	 * 
	 * @param query
	 *            SQL query that will be executed
	 * @return either (1) the row count for SQL Data Manipulation Language (DML)
	 *         statements or (2) 0 for SQL statements that return nothing
	 * @throws SQLException
	 *             if a database access error occurs,
	 *             <p>
	 *             this method is called on a closed statement,
	 *             <p>
	 *             the given SQL statement produces a ResultSet object,
	 *             <p>
	 *             the method is called on a PreparedStatement or
	 *             CallableStatement or
	 *             <p>
	 *             See the MySQL specification for detailed information on the
	 *             error code
	 */
	public int executeUpdateQueryForUpdate(String query) throws SQLException {
		return (Integer) executeQueriesWithSilentRetry(query, ExecutionType.EXECUTE_UPDATE_QUERY_FOR_UPDATE);
	}

	private void connect() {
		// connect database
		try {
			logger.info("Current Command : [ " + getClass() + " ]" + " Trying connect to database");

			this.connection = DriverManager.getConnection("jdbc:mysql://" + this.dbLocation
					+ "?useUnicode=true&characterEncoding=UTF-8&useCursorFetch=true", this.dbUser, this.dbPassword);
			logger.info("Current Command : [ " + getClass() + " ]" + " Database connection established");

		} catch (SQLException e) {
			logger.error("Current Command : [ " + getClass() + " ]" + " DB-DriverManager error");
			throw new RuntimeException(e);
		}

	}

	/**
	 * for manipulation of database incl. "closing" connection/ resultset excl.
	 * statement
	 * <p>
	 * different to executeUpdateQueryForUpdate()
	 * 
	 * @param query
	 *            SQL query that will be executed
	 * @return row count or 0 for SQL statements that return nothing
	 * @throws SQLException
	 *             if a database access error occurs, the given SQL query
	 *             produces a {@link ResultSet} object or if the
	 *             {@link Statement} used for updating is already closed. See
	 *             the MySQL specification for detailed information on the error
	 *             code.
	 */
	public int executeUpdateQuery(String query) throws SQLException {
		return (Integer) executeQueriesWithSilentRetry(query, ExecutionType.EXECUTE_UPDATE_QUERY);
	}

	public Connection getConnection() {
		return this.connection;
	}

	public Properties getProperties() {
		return this.properties;
	}

	public int getLimit() {
		return this.limit;
	}

	public void setLimit(int value) {
		this.limit = value;
	}

	/**
	 * @param tableName
	 *            name of the table which will be dropped
	 * @throws SQLException
	 *             if a database access error occurs, the given SQL query
	 *             produces a {@link ResultSet} object or if the
	 *             {@link Statement} used for updating is already closed. See
	 *             the MySQL specification for detailed information on the error
	 *             code.
	 **/
	public void dropTable(String tableName) throws SQLException {
		this.executeUpdateQuery("DROP TABLE IF EXISTS " + tableName);
	}

	/**
	 * @throws SQLException
	 *             if a database access error occurs, the analyze
	 *             {@link Statement} produces a {@link ResultSet object or if
	 *             the {@link Statement} used for updating is already closed.
	 *             See the MySQL specification for detailed information on the
	 *             error code.
	 */
	public void analyzeTable(String tableName) throws SQLException {
		this.executeUpdateQuery("ANALYZE TABLE  " + tableName);
	}

	/**
	 * @throws SQLException
	 *             if a database access error occurs, the optimize
	 *             {@link Statement} produces a {@link ResultSet object or if
	 *             the {@link Statement} used for updating is already closed.
	 *             See the MySQL specification for detailed information on the
	 *             error code.
	 */
	public void optimizeTable(String tableName) throws SQLException {
		this.executeUpdateQuery("OPTIMIZE TABLE  " + tableName);
	}

}
