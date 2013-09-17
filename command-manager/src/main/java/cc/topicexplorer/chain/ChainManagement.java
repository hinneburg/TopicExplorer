package cc.topicexplorer.chain;

import java.util.List;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.config.ConfigParser;
import org.apache.commons.chain.impl.CatalogFactoryBase;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import cc.topicexplorer.chain.commands.DbConnectionCommand;
import cc.topicexplorer.chain.commands.LoggerCommand;
import cc.topicexplorer.chain.commands.PropertiesCommand;

/**
 * This class is used for the controlled execution of commands. Commands to be
 * executed are declared in an catalog. Those commands will be ordered and then
 * excuted.
 * <p>
 * This class executes specified initial commands needed for further tasks.
 * Arguments will be parsed from commandline and may then be accessed.
 * 
 * @author Sebastian Baer
 * 
 */
public class ChainManagement {
	private ConfigParser configParser;
	private Catalog catalog;
	private CommunicationContext communicationContext;
	private static Logger logger;

	/**
	 * This method takes a location to retrieve a catalog. If there is a valid
	 * catalog at the given location, it will set the global catalog variable in
	 * this class.
	 * 
	 * @param catalogLocation
	 * @throws Exception
	 */
	public void getCatalog(String catalogLocation) throws Exception {
		configParser = new ConfigParser();
		
		try {
			logger.info("this.getClass().getResource(catalogLocation)"
					+ this.getClass().getResource(catalogLocation));

			configParser.parse(this.getClass().getResource(catalogLocation));

			catalog = CatalogFactoryBase.getInstance().getCatalog();
			
		} catch (Exception e) {
			logger.fatal("There is no valid catalog at the given path:"
					+ catalogLocation + "." + e);
			System.exit(1);
		}
	}

	/**
	 * Executes the in this method declared commands. It contains commands that
	 * should be executed before other commands or tasks. Information are saved
	 * in the databaseContext.
	 */
	public void init() {
		try {
			
			Command propertiesCommand = new PropertiesCommand();
			Command dbConnectionCommand = new DbConnectionCommand();
			Command loggerCommand = new LoggerCommand();

			loggerCommand.execute(communicationContext);
			propertiesCommand.execute(communicationContext);
			dbConnectionCommand.execute(communicationContext);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Returns a list with all commands of the catalog in an ordered sequence.
	 * 
	 * @return A ordered list containing the commands of the catalog.
	 */
	public List<String> getOrderedCommands() {
		init();
		DependencyCollector dependencyCollector = new DependencyCollector(
				catalog);

		return dependencyCollector.getOrderedCommands();
	}

	/**
	 * Takes a list of commands and executes them in the sequence of the list
	 */
	public void executeOrderedCommands(List<String> commandList) {
		try {
			Command command;
			for (String commandName : commandList) {
				command = catalog.getCommand(commandName);
				command.execute(communicationContext);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public static void main(String[] args) throws Exception {

		ChainManagement chainManager = new ChainManagement();
		ChainCommandLineParser commandLineParser = new ChainCommandLineParser(
				args);
		List<String> orderedCommands;
		String catalogLocation;
		
		logger = Logger.getRootLogger();

		catalogLocation = commandLineParser.getCatalogLocation();

		chainManager.getCatalog(catalogLocation);

		orderedCommands = chainManager.getOrderedCommands();

		logger.info("ordered commands: " + orderedCommands);

		chainManager.executeOrderedCommands(orderedCommands);
	}

	/**
	 * Retrieves arguments from the commandline and makes them accessable via a
	 * getter method
	 * 
	 * @author Sebastian Baer
	 * 
	 */
	private static class ChainCommandLineParser {
		private Options options;

		private CommandLineParser commandLineParser;
		private CommandLine commandLine;
		private HelpFormatter helpFormatter;

		private String catalogLocation;
		private String startCommand;
		private String endCommand;

		private String[] args;
		
		private Logger logger = Logger.getRootLogger();

		/**
		 * Adds the possible arguments. Sets global args and executes the
		 * parsing of the given arguments.
		 * 
		 * @param args
		 */
		public ChainCommandLineParser(String[] args) {
			options = new Options();
			options.addOption("h", "help", false,
					"prints information about passing arguments.");
			options.addOption("c", "catalog", true,
					"determines location of catalog file");
			options.getOption("c").setArgName("string");
			options.addOption("s", "start", true, "set command to start with");
			options.getOption("s").setArgName("string");
			options.addOption("e", "end", true, "set command to end with");
			options.getOption("e").setArgName("string");

			commandLineParser = new BasicParser();
			commandLine = null;
			helpFormatter = new HelpFormatter();

			this.args = args;

			parseArguments();
		}

		/**
		 * Checks if any of the mentioned options is contained in the arguments
		 * and then sets it in the class. If the usage of arguments is wrong
		 * help is printed.
		 */
		public void parseArguments() {
			// if there is something wrong with the input, print help
			try {
				commandLine = commandLineParser.parse(options, args);
			} catch (Exception e) {
				printHelp();
				logger.fatal("Usage of arguments wrong.");
				System.exit(1);
			}

			if (commandLine.hasOption("h")) {
				printHelp();
			}

			if (commandLine.hasOption("c")) {
				catalogLocation = commandLine.getOptionValue("c");
			} else {
				logger.fatal("No catalog location given.");
				System.exit(1);
			}

			if (commandLine.hasOption("s")) {
				startCommand = commandLine.getOptionValue("s");
			}

			if (commandLine.hasOption("e")) {
				endCommand = commandLine.getOptionValue("e");
			}
		}

		public String getCatalogLocation() {
			return catalogLocation;
		}

		public String getStartCommand() {
			return startCommand;
		}

		public String getEndCommand() {
			return endCommand;
		}

		public void printHelp() {
			helpFormatter.printHelp("TopicExplorer <command> [<arg>]", options);
		}
	}
}