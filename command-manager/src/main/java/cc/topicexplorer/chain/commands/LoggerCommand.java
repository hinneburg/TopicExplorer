package cc.topicexplorer.chain.commands;

import java.io.IOException;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class LoggerCommand implements Command {
	private Logger logger;

	/**
	 * @throws IOException
	 *             if there are problems handling the file
	 *             'logs/Preprocessing.log'
	 */
	@Override
	public boolean execute(Context context) {

		this.logger = Logger.getRootLogger();

		PatternLayout layout = new PatternLayout("%d-%p-%C-%M-%m%n");

		try {
			this.logger.addAppender(new FileAppender(layout, "logs/Preprocessing.log", false));
		} catch (IOException e) {
			logger.warn("FileAppender with log file 'logs/Preprocessing.log' could not be constructed.");
			throw new RuntimeException(e);
		}

		// ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
		this.logger.setLevel(Level.INFO);

		this.logger.info("Current Command : [ " + getClass().toString() + " ]");

		return false;
	}

}
