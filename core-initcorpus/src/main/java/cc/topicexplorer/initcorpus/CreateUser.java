package cc.topicexplorer.initcorpus;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

import cc.commandmanager.core.Context;
import cc.commandmanager.core.Command;

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
	public void execute(Context context) {
		Properties properties = (Properties) context.get("properties");
		Connection crawlManagerConnection = (Connection) context.get("CrawlManagmentConnection");
		
		String dbName = properties.getProperty("database.DB");
		String dbUser = properties.getProperty("database.DbUser");
		String dbPassword = properties.getProperty("database.DbPassword");
		String[] allowFrom = properties.getProperty("database.AllowFrom").split(",");
		String[] allowFileFrom = properties.getProperty("database.AllowFileFrom").split(",");
		try {
			Statement createUserStmt = crawlManagerConnection.createStatement();
			for(int i = 0; i < allowFrom.length; i++) {
				createUserStmt.executeUpdate("grant all privileges on " +  dbName + ".* to '" + dbUser + "'@'" + allowFrom[i] + "' identified by '" + dbPassword + "';");
			}
			for(int i = 0; i < allowFileFrom.length; i++) {
				createUserStmt.executeUpdate("grant file on *.* to '" + dbUser + "'@'" + allowFileFrom[i] + "';");
			}
		} catch (SQLException e) {
			logger.error("DB user " + dbUser + " could not be created.");
			throw new RuntimeException(e);
		}
	}

}
