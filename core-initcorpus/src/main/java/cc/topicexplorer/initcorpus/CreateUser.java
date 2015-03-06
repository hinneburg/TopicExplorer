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
		try {
			Statement createUserStmt = crawlManagerConnection.createStatement();
			createUserStmt.executeUpdate("grant all privileges on " +  dbName + ".* to '" + dbUser + "'@'localhost' identified by '" + dbPassword + "';");
			createUserStmt.executeUpdate("grant file on *.* to '" + dbUser + "'@'localhost';");
			createUserStmt.executeUpdate("grant all privileges on " +  dbName + ".* to '" + dbUser + "'@'topicexplorer.informatik.uni-halle.de' identified by '" + dbPassword + "';");
		} catch (SQLException e) {
			logger.error("DB user " + dbUser + " could not be created.");
			throw new RuntimeException(e);
		}
	}

}
