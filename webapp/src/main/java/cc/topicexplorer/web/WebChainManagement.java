package cc.topicexplorer.web;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import cc.commandmanager.core.ChainManagement;
import cc.commandmanager.core.CommunicationContext;
import cc.commandmanager.core.DependencyCollector;
import cc.commandmanager.core.DependencyCommand;
import cc.topicexplorer.commands.DbConnectionCommand;
import cc.topicexplorer.commands.PropertiesCommand;

public class WebChainManagement {

	private static ChainManagement chainManagement = null;
	private static Logger logger = Logger.getRootLogger();
	private static boolean isInitialized = false;

	private WebChainManagement() {
		throw new UnsupportedOperationException();
	}

	public static void init() {
		if (!isInitialized) {
			initializeLogger("logs/webapp.log");

			CommunicationContext context = new CommunicationContext();
			executeInitialCommands(context);
			chainManagement = new ChainManagement(context);

			isInitialized = true;
		}
	}

	private static void initializeLogger(String logfileName) {
		try {
			logger.addAppender(new FileAppender(new PatternLayout("%d-%p-%C-%M-%m%n"), logfileName, false));
			logger.setLevel(Level.INFO); // ALL | DEBUG | INFO | WARN | ERROR |
			// FATAL | OFF:
		} catch (IOException e) {
			logger.error("FileAppender with log file " + logfileName + " could not be constructed.");
			throw new RuntimeException(e);
		}
	}

	private static void executeInitialCommands(CommunicationContext context) {
		try {
			DependencyCommand propertiesCommand = new PropertiesCommand();
			propertiesCommand.execute(context);

			DependencyCommand dbConnectionCommand = new DbConnectionCommand();
			dbConnectionCommand.execute(context);
		} catch (RuntimeException rntmEx) {
			logger.error("Initialization abborted, due to a critical exception");
			throw rntmEx;
		}
	}

	public static void setCatalog(String catalog) {
		chainManagement.setCatalog(catalog);
	}

	public static CommunicationContext getCommunicationContext() {
		return chainManagement.getCommunicationContext().clone();
	}

	public static List<String> getOrderedCommands(Set<String> startCommands, Set<String> endCommands) {
		return chainManagement.getOrderedCommands(new DependencyCollector(chainManagement.catalog).getDependencies(),
				startCommands, endCommands);
	}

	public static void executeCommands(List<String> commands, CommunicationContext communicationContext) {
		try {
			chainManagement.executeCommands(commands, communicationContext);
		} catch (RuntimeException e) {
			logger.error("A command caused a RuntimeException.", e);
		}
	}

}
