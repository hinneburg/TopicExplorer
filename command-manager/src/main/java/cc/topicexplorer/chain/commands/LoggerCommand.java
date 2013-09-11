package cc.topicexplorer.chain.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import org.apache.log4j.*;

import cc.topicexplorer.chain.LoggerContext;

public class LoggerCommand implements Command {
	private Logger logger;

	@Override
	public boolean execute(Context context) throws Exception {

		logger = Logger.getRootLogger();
		
		PatternLayout layout = new PatternLayout("%d-%p-%C-%M-%m%n");
//		ConsoleAppender consoleAppender = new ConsoleAppender(layout);
//		logger.addAppender(consoleAppender);
		FileAppender fileAppender = new FileAppender(layout,
				"logs/Preprocessing.log", false);
		logger.addAppender(fileAppender);
		// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
		logger.setLevel(Level.INFO);

		logger.info("Current Command : [ " + getClass().toString()
				+ " ]");
		
		LoggerContext loggerContext = (LoggerContext) context;

		loggerContext.setLogger(logger);
		return false;
	}

}
