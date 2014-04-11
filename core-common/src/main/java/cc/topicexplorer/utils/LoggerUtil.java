package cc.topicexplorer.utils;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.google.common.base.Preconditions;

public class LoggerUtil {

	private static Logger logger = Logger.getLogger(LoggerUtil.class);

	private LoggerUtil() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Initializes a system wide log4j instance. Will look for
	 * log4j.global.properties in the core-common's classpath. Global properties
	 * then will be overwritten with log4j.local.properties located in
	 * core-common/local/main/resources if they exist. See Apache manual
	 * (https://logging.apache.org/log4j/1.2/manual.html) for information on how
	 * to structure a log4j property file.
	 * 
	 * @param globalProperties
	 *            file name for the global log4j properties. Will be overwritten
	 *            with local properties, if they are defined. Must neither be
	 *            null nor an empty String.
	 * @param localProperties
	 *            file name for the local log4j properties. Will yet be ignored.
	 */
	public static void initializeLogger(String globalProperties, String localProperties) {
		checkPreconditions(globalProperties);
		PropertyConfigurator.configure(getProperties(globalProperties, localProperties));
		logger.info("Logger successfully initialized");
	}

	private static void checkPreconditions(String globalProperties) {
		Preconditions.checkNotNull(globalProperties);
		Preconditions.checkArgument(!globalProperties.equals(""), globalProperties);
	}

	private static Properties getProperties(String globalProperties, String localProperties) {
		Properties logProperties = new Properties();
		try {
			logProperties.load(LoggerUtil.class.getResourceAsStream("/" + globalProperties));
			return logProperties;
		} catch (IOException e) {
			logger.error("resources not found.");
			throw new RuntimeException(e);
		}
	}

}
