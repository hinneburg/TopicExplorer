package wikiParser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Database {
	private Connection connection;
	private Statement statement;
	private Properties properties;

	private int limit = -1; // limit for select queries

	public Database(Properties prop) throws SQLException, InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		startUp(prop, false);
	}

	public Database(Properties prop, Boolean otherDatabase) throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		startUp(prop, otherDatabase);
	}

	private void startUp(Properties prop, Boolean otherDatabase) throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		properties = prop;

		String dbLocation = properties.getProperty("database.DbLocation");

		if (otherDatabase) {
			dbLocation = dbLocation + "/" + properties.getProperty("Wiki_database.other");
		} else {
			dbLocation = dbLocation + "/" + properties.getProperty("Wiki_database.wikidb");
		}

		String dbUser = properties.getProperty("database.DbUser");
		String dbPassword = properties.getProperty("database.DbPassword");

		Class.forName("com.mysql.jdbc.Driver").newInstance();

		// connect database
		try {

			connection = DriverManager.getConnection("jdbc:mysql://" + dbLocation
					+ "?useUnicode=true&characterEncoding=UTF-8&useCursorFetch=true", dbUser, dbPassword);

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("DB-DriverManager error");
		}

		statement = connection.createStatement();
		statement.setFetchSize(100);
		connection.setAutoCommit(true);
	}

	public void shutdownDB() throws SQLException {
		if (!connection.isClosed()) {
			connection.close();
		}
	}

	/**
	 * @param query
	 * @return ResultSet
	 * 
	 */
	public ResultSet executeQuery(String query) throws SQLException {

		// System.out.println(query);
		statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
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
		// System.out.println(query);
		statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
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

	public Properties getProperties() {
		return properties;
	}

	public int getLimit() {
		return this.limit;
	}

	public void setLimit(int value) {
		this.limit = value;
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

	// /**
	// * for updating blob-data
	// * wikitext is saved as blob
	// *
	// * @param statementTextWith2Varis
	// * @param blobText / first parameter
	// * @param vari_id / second parameter
	// * @throws SQLException
	// */
	// public void excecutePreparedStatementForUpdatingBlob(String
	// statementTextWith2Varis, String blobText, int vari_id) throws
	// SQLException
	// {
	// PreparedStatement stmt =
	// connection.prepareStatement(statementTextWith2Varis);
	// stmt.setBytes(1, blobText.getBytes());
	// stmt.setInt(2, vari_id);
	// stmt.execute();
	// stmt.close();
	// }

	public Connection getConnection() {
		return connection;
	}

}
