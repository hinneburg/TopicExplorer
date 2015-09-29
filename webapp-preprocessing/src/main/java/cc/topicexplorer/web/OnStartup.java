package cc.topicexplorer.web;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.DbConnectionCommand;
import cc.topicexplorer.commands.PropertiesCommand;
import cc.topicexplorer.utils.LoggerUtil;

public class OnStartup implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(OnStartup.class);
	private static boolean hasBeenInitialized = false;

	@Override
	public void contextDestroyed(final ServletContextEvent arg0) {
		// This manually deregisters JDBC driver, which prevents Tomcat 7 from
		// complaining about memory leaks wrto this class
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
				logger.log(Level.INFO, String.format("deregistering jdbc driver: %s", driver));
			} catch (SQLException e) {
				logger.log(Level.ERROR, String.format("Error deregistering driver %s", driver), e);
			}
		}
//        try {
//            AbandonedConnectionCleanupThread.shutdown();
//        } catch (InterruptedException e) {
//            logger.warn("SEVERE problem cleaning up: " + e.getMessage());
//            e.printStackTrace();
//        }
	}

	@Override
	public void contextInitialized(final ServletContextEvent arg0) {
		if (!hasBeenInitialized) {

			System.out.println("Do on startup.");
			Context context = new Context();
			executeInitialCommands(context);
			LoggerUtil.initializeLogger();

			WebChainManagement.init(context);
			hasBeenInitialized = true;
		}
	}

	private static void executeInitialCommands(Context context) {
		try {
			Command propertiesCommand = new PropertiesCommand();
			propertiesCommand.execute(context);

			Command dbConnectionCommand = new DbConnectionCommand();
			dbConnectionCommand.execute(context);
		} catch (RuntimeException rntmEx) {
			logger.error("Initialization abborted, due to a critical exception", rntmEx);
			throw rntmEx;
		}
	}
}
