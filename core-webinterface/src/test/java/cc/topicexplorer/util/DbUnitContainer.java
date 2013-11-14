package cc.topicexplorer.util;

import java.sql.Connection;
import java.util.Properties;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;

import cc.topicexplorer.database.Database;

public class DbUnitContainer {

	private static final Database topicExplorerDatabase = getTopicExplorerDatabase();
	private static Connection jdbcConnection;
	private static IDatabaseConnection dbunitConnection;

	private DbUnitContainer() {
		throw new UnsupportedOperationException();
	}

	public static IDatabaseConnection getDbunitConnection() {
		if (dbunitConnection == null) {
			try {
				dbunitConnection = new DatabaseConnection(getJdbcConnection(), null, true);
			} catch (DatabaseUnitException e) {
				throw new RuntimeException(e);
			}
		}
		return dbunitConnection;
	}

	public static Database getTopicExplorerDatabase() {
		try {
			if (topicExplorerDatabase == null) {
				return new Database(getPropertiesForTeDatabaseObject());
			} else {
				return topicExplorerDatabase;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Properties getPropertiesForTeDatabaseObject() {
		Properties properties = new Properties();
		properties.setProperty("database.DbLocation", "localhost:3306");
		properties.setProperty("database.DbUser", "root");
		properties.setProperty("database.DbPassword", "TopicExplorer");
		properties.setProperty("database.DB", "test_maerchen");
		properties.setProperty("malletNumTopics", "1");
		return properties;
	}

	private static Connection getJdbcConnection() {
		if (jdbcConnection == null) {
			jdbcConnection = topicExplorerDatabase.getConnection();
		}
		return jdbcConnection;
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			dbunitConnectionClose();
			jdbcConnectionClose();
		} catch (Throwable t) {
			throw t;
		} finally {
			super.finalize();
		}
	}

	private void jdbcConnectionClose() {
		try {
			jdbcConnection.close();
		} catch (Exception e) {
		}
		jdbcConnection = null;
	}

	private void dbunitConnectionClose() {
		try {
			dbunitConnection.close();
		} catch (Exception e) {
		}
		dbunitConnection = null;
	}
}
