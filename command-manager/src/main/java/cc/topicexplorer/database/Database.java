package cc.topicexplorer.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.jooq.SQLDialect;
import org.jooq.impl.Factory;

import org.apache.log4j.Logger;

/**
 * @author user
 * 
 */

public class Database {
	private Connection connection;
	private Statement statement;
	private Properties properties;
	private int topicCount;

	private int limit = -1; // limit for select queries

	private Factory create = null;

	private Logger logger = Logger.getRootLogger();

	public Database(Properties prop) throws Exception {
		startUp(prop, false);
	}

	public Database(Properties prop, Boolean otherDatabase) throws Exception {
		startUp(prop, otherDatabase);
	}

	private void startUp(Properties prop, Boolean otherDatabase)
			throws Exception {
		properties = prop;

		String dbLocation = properties.getProperty("database.DbLocation");

		if (otherDatabase) {
			dbLocation = dbLocation + "/" + properties.getProperty("database.other");
		} else {
			dbLocation = dbLocation + "/" + properties.getProperty("database.DB");
		}

		String dbUser = properties.getProperty("database.DbUser");
		String dbPassword = properties.getProperty("database.DbPassword");
		this.topicCount = Integer.parseInt(properties
				.getProperty("malletNumTopics"));

		// load Database-Driver
		try {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Current Command : [ " + getClass() + " ]" +" Database-Driver not found" );
		}

		// connect database
		try {
			logger.info("Current Command : [ " + getClass() + " ]" +" Trying connect to database" );

			connection = DriverManager.getConnection("jdbc:mysql://" + dbLocation
					+ "?useUnicode=true&characterEncoding=UTF-8&useCursorFetch=true", dbUser,
					dbPassword);
			logger.info("Current Command : [ " + getClass() + " ]" +" Database connection established" );

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Current Command : [ " + getClass() + " ]" +" DB-DriverManager error" );
		}

		// connect with jooq
		try {
			// Class.forName("com.mysql.jdbc.Driver").newInstance();
			this.create = new Factory(connection, SQLDialect.MYSQL);

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("DB-Driver-Jooq-Error");
		}

		statement = connection.createStatement();
		statement.setFetchSize(100);
		connection.setAutoCommit(true);
	}

	public void shutdownDB() throws SQLException {
		if (!connection.isClosed())
			connection.close();
	}

	/**
	 * @param query
	 * @return ResultSet
	 * 
	 */
	public ResultSet executeQuery(String query) throws SQLException {

//		System.out.println(query);
		statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		return statement.executeQuery(query);
	}

	/**
	 * for manipulation of database without "closing" the connection/ resultset
	 * for insert and update in loop's
	 * 
	 * incl statement = connection..
	 * 
	 * @param query
	 * @return excuteUpdateQuery
	 */
	public int executeUpdateQueryForUpdate(String query) throws SQLException {
		// damit das RS offen bleibt, sonst funktionierten die alten
		// keywords_themen2 bzw tables.keytopic2 nicht
		// da es zur exception gekommen ist (cannot perfom ... rs is closed)
//		System.out.println(query);
		statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		return statement.executeUpdate(query);
	}

	/**
	 * for manipulation of database incl. "closing" connection/ resultset
	 * 
	 * excl. statement - different to executeUpdateQueryForUpdate
	 * 
	 * @param query
	 * @return excuteUpdateQuery
	 */
	public int executeUpdateQuery(String query) throws SQLException {
		// for manipulation , executeQuery couldn't manipulate database
		// System.out.println(query);

		return statement.executeUpdate(query);
	}

	/**
	 * for the use of jooq
	 * 
	 * @return Factory create
	 */
	public Factory getCreateJooq() {
		return this.create;
	}
	
	public Connection getConnection() {
		return this.connection;
	}

	public Properties getProperties() throws SQLException {
		return properties;
	}

	public int getLimit() {
		return this.limit;
	}

	public void setLimit(int value) {
		this.limit = value;
	}

	public int getTopicCount() {
		return this.topicCount;
	}

	public void dropTable(String table) throws SQLException {
		this.executeUpdateQuery("DROP TABLE IF EXISTS " + table);
	}
	
	public void analyseTable(String table) throws SQLException {
		this.executeUpdateQuery("ANALYZE TABLE  " + table);
	}
	
	public void optimizeTable(String table) throws SQLException {
		this.executeUpdateQuery("OPTIMIZE TABLE  " + table);
	}

}
