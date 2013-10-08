package cc.topicexplorer.chain;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.config.ConfigParser;
import org.apache.commons.chain.impl.CatalogFactoryBase;
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
	private Catalog catalog;
	private CommunicationContext communicationContext;
	private static Logger logger = Logger.getRootLogger();

	public ChainManagement() {
		communicationContext = new CommunicationContext();
	}
	
	public ChainManagement(CommunicationContext context) {
		communicationContext = context;
	}

	/**
	 * This method takes a location to retrieve a catalog. If there is a valid
	 * catalog at the given location, it will set the global catalog variable in
	 * this class.
	 * 
	 * @param catalogLocation
	 * @throws Exception
	 */
	public void setCatalog(String catalogLocation) throws Exception {
		ConfigParser configParser = new ConfigParser();

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
			Command loggerCommand = new LoggerCommand();
			Command propertiesCommand = new PropertiesCommand();
			Command dbConnectionCommand = new DbConnectionCommand();

			loggerCommand.execute(communicationContext);
			propertiesCommand.execute(communicationContext);
			dbConnectionCommand.execute(communicationContext);
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	/**
	 * Returns a Set with all commands of the catalog in an ordered sequence.
	 * 
	 * @return A ordered Set containing the commands of the catalog.
	 */	
	public List<String> getOrderedCommands() {

		return getOrderedCommands(new HashSet<String>(),
				new HashSet<String>());
	}
	
	/**
	 * Returns a Set with all commands of a given map of dependencies in an ordered sequence.
	 * 
	 * @return A ordered Set containing the commands of the catalog.
	 */	
	public List<String> getOrderedCommands(Map<String, Set<String>> dependencies) {

		return getOrderedCommands(dependencies, new HashSet<String>(),
				new HashSet<String>());
	}
	
	public List<String> getOrderedCommands(Set<String> startCommands, 
			Set<String> endCommands) {

		DependencyCollector dependencyCollector = new DependencyCollector(catalog);
		Map<String, Set<String>> dependencies;
		
		
		dependencies = dependencyCollector.getDependencies();
		dependencies = dependencyCollector.getStrongComponents(dependencies, startCommands, endCommands);
		
		communicationContext.put("dependencies", dependencies);
		
		return dependencyCollector.orderCommands(dependencies);
	}
	
	public List<String> getOrderedCommands(Map<String, Set<String>> dependencies, Set<String> startCommands,
			 Set<String> endCommands) {

		DependencyCollector dependencyCollector = new DependencyCollector();
		
		dependencies = dependencyCollector.getStrongComponents(dependencies, startCommands, endCommands);
		
		communicationContext.put("dependencies", dependencies);
		
		return dependencyCollector.orderCommands(dependencies);
	}

	/**
	 * Takes a Set of commands and executes them in the sequence of the Set
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

	public void executeOrderedCommands(List<String> commandList,
			CommunicationContext localCommunicationContext) {
		try {
			Command command;
			for (String commandName : commandList) {
				command = catalog.getCommand(commandName);
				command.execute(localCommunicationContext);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	public CommunicationContext getInitialCommunicationContext() {		
		
		return communicationContext;
	}

	public static void main(String[] args) throws Exception {
		ChainManagement chainManager = new ChainManagement();
		ChainCommandLineParser commandLineParser = new ChainCommandLineParser(
				args);
		List<String> orderedCommands;
		String catalogLocation;
		chainManager.init();

		catalogLocation = commandLineParser.getCatalogLocation();

		chainManager.setCatalog(catalogLocation);

		orderedCommands = chainManager.getOrderedCommands(
				commandLineParser.getStartCommands(),
				commandLineParser.getEndCommands());

		logger.info("ordered commands: " + orderedCommands);

		if (!commandLineParser.getOnlyDrawGraph()) {
			chainManager.executeOrderedCommands(orderedCommands);
		}
	}
}