package cc.topicexplorer.utils;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoggerUtil {

	private static Logger logger = Logger.getLogger(LoggerUtil.class);
	private static final String NO_PREFIX = "";

	private LoggerUtil() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Initializes a system wide log4j instance. Will look for log4j.global.properties in the core-common's classpath.
	 * Global properties then will be overwritten with required log4j.local.properties located in
	 * core-common/local/main/resources. See Apache manual (https://logging.apache.org/log4j/1.2/manual.html) for
	 * information on how to structure a log4j property file.
	 */
	public static void initializeLogger() {
		Properties loggerProperties = PropertiesUtil.loadMandatoryProperties("log4j", NO_PREFIX);
		PropertyConfigurator.configure(loggerProperties);
		logger.info("Logger successfully initialized");
	}

}
