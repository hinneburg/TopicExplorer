package cc.topicexplorer.initcorpus;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.commandmanager.core.ResultState;

import com.google.common.collect.Sets;

public class CreateDB implements Command {
	private static final Logger logger = Logger.getLogger(CreateDB.class);

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public ResultState execute(Context context) {
		Properties properties = (Properties) context.get("properties");
		Connection crawlManagerConnection = (Connection) context.get("CrawlManagmentConnection");

		String dbName = properties.getProperty("database.DB");
		try {
			Statement createDBStmt = crawlManagerConnection.createStatement();
			createDBStmt.executeUpdate("CREATE DATABASE " + dbName
					+ " DEFAULT CHARACTER SET = utf8mb4 DEFAULT COLLATE = utf8mb4_bin;");
		} catch (SQLException e) {
			logger.error("Database " + dbName + " could not be created.");
			throw new RuntimeException(e);
		}
		return ResultState.success();
	}
}
