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

public class CreateUser implements Command {
	private static final Logger logger = Logger.getLogger(CreateUser.class);

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("CreateDB");
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
		String dbUser = properties.getProperty("database.DbUser");
		String dbPassword = properties.getProperty("database.DbPassword");
		String[] allowFrom = properties.getProperty("database.AllowFrom").split(",");
		String[] allowFileFrom = properties.getProperty("database.AllowFileFrom").split(",");
		try {
			Statement createUserStmt = crawlManagerConnection.createStatement();
			for (String element : allowFrom) {
				createUserStmt.executeUpdate("grant all privileges on " +  dbName + ".* to '" + dbUser + "'@'" + element + "' identified by '" + dbPassword + "';");
			}
			for (String element : allowFileFrom) {
				createUserStmt.executeUpdate("grant file on *.* to '" + dbUser + "'@'" + element + "';");
			}
		} catch (SQLException e) {
			logger.error("DB user " + dbUser + " could not be created.");
			return ResultState.failure("DB user " + dbUser + " could not be created.", e);
		}
		return ResultState.success();
	}

}
