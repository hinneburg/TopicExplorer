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
import cc.topicexplorer.exceptions.CatalogNotInstantiableException;

import com.google.common.annotations.VisibleForTesting;

/**
 * This class is used for the controlled execution of commands. Commands to be
 * executed are declared in an catalog. Those commands will be ordered and then
 * executed.
 * <p>
 * This class executes specified initial commands needed for further tasks.
 * Arguments will be parsed from command line and may then be accessed.
 * 
 * @author Sebastian Baer
 * 
 */
public class ChainManagement {
	@VisibleForTesting
	public Catalog catalog;
	private final CommunicationContext communicationContext;
	private DependencyCollector dependencyCollector;
	private static Logger logger = Logger.getRootLogger();

	public ChainManagement() {
		this(new CommunicationContext());
	}

	public ChainManagement(CommunicationContext context) {
		this.communicationContext = context;
	}

	/**
	 * This method takes a location to retrieve a catalog. If there is a valid
	 * catalog at the given location, it will set the global catalog variable in
	 * this class.
	 * 
	 * @param catalogLocation
	 * @throws Exception
	 */
	public void setCatalog(String catalogLocation) throws CatalogNotInstantiableException {
		ConfigParser configParser = new ConfigParser();

		try {
			logger.info("this.getClass().getResource(catalogLocation)" + this.getClass().getResource(catalogLocation));

			configParser.parse(this.getClass().getResource(catalogLocation));
			this.catalog = CatalogFactoryBase.getInstance().getCatalog();

		} catch (Exception e) {
			logger.error("There is no valid catalog at the given path: " + catalogLocation, e);
			throw new CatalogNotInstantiableException();
		}
	}

	/**
	 * Executes the commands, needed for initialization. It contains commands
	 * that should be executed before other commands or tasks. Information is
	 * saved in the databaseContext.
	 * 
	 * @throws RuntimeException
	 *             if one of command, needed for initialization, throws a
	 *             {@link RuntimeException}
	 */
	public void init() {
		try {
			Command loggerCommand = new LoggerCommand();
			Command propertiesCommand = new PropertiesCommand();
			Command dbConnectionCommand = new DbConnectionCommand();

			loggerCommand.execute(this.communicationContext);
			propertiesCommand.execute(this.communicationContext);
			dbConnectionCommand.execute(this.communicationContext);
		} catch (RuntimeException e1) {
			logger.error("Initialization abborted, due to a critical exception");
			throw e1;
		} catch (Exception e2) {
			logger.warn("Initialization caused a non critical exception", e2);
		}
	}

	public Map<String, Set<String>> getDependencies() {
		return this.dependencyCollector.getDependencies();
	}

	/**
	 * Returns a {@linkplain List<String>} with all commands of a given map of
	 * dependencies in an ordered sequence.
	 * 
	 * @return An ordered {@linkplain List<String>} containing the commands of
	 *         the catalog.
	 */
	public List<String> getOrderedCommands() {
		return getOrderedCommands(new HashSet<String>(), new HashSet<String>());
	}

	/**
	 * Returns a {@linkplain List<String>} with all commands of a given map of
	 * dependencies in an ordered sequence.
	 */
	public List<String> getOrderedCommands(Map<String, Set<String>> dependencies) {
		return getOrderedCommands(dependencies, new HashSet<String>(), new HashSet<String>());
	}

	/**
	 * Returns a {@linkplain List<String>} with all commands of a given map of
	 * dependencies in an ordered sequence.
	 */
	public List<String> getOrderedCommands(Set<String> startCommands, Set<String> endCommands) {
		this.dependencyCollector = new DependencyCollector(this.catalog);

		Map<String, Set<String>> dependencies = getDependencies();

		Map<String, Set<String>> strongComponents = this.dependencyCollector.getStrongComponents(dependencies,
				startCommands, endCommands);

		return this.dependencyCollector.orderCommands(strongComponents);
	}

	public List<String> getOrderedCommands(Map<String, Set<String>> dependencies, Set<String> startCommands,
			Set<String> endCommands) {

		this.dependencyCollector = new DependencyCollector();

		dependencies = this.dependencyCollector.getStrongComponents(dependencies, startCommands, endCommands);

		return this.dependencyCollector.orderCommands(dependencies);
	}

	/**
	 * Takes a {@linkplain List} of commands and executes them in the list's
	 * sequence
	 */
	public void executeCommands(List<String> commands) {
		this.executeCommands(commands, this.communicationContext);
	}

	/**
	 * Takes a {@linkplain List} of commands and executes them in the list's
	 * sequence, using the specified {@linkplain CommunicationContext}
	 */
	public void executeCommands(List<String> commands, CommunicationContext localCommunicationContext) {
		for (String commandName : commands) {
			try {
				Command command;
				command = this.catalog.getCommand(commandName);
				command.execute(localCommunicationContext);
			} catch (RuntimeException e1) {
				logger.error(String.format("The current command %s caused a critical exception", commandName));
				throw e1;
			} catch (Exception e2) {
				logger.warn(String.format("The current command %s caused a non critical exception.", commandName), e2);
			}
		}
	}

	public CommunicationContext getCommunicationContext() {
		return this.communicationContext;
	}

	public static void main(String[] args) {
		ChainManagement chainManager = new ChainManagement();
		ChainCommandLineParser commandLineParser;

		try {
			commandLineParser = new ChainCommandLineParser(args);
		} catch (RuntimeException e) {
			logger.error("Problems occured while parsing the command line tokens.");
			throw e;
		}

		List<String> orderedCommands;
		String catalogLocation;
		chainManager.init();

		catalogLocation = commandLineParser.getCatalogLocation();
		chainManager.setCatalog(catalogLocation);
		orderedCommands = chainManager.getOrderedCommands(commandLineParser.getStartCommands(),
				commandLineParser.getEndCommands());

		logger.info("ordered commands: " + orderedCommands);
		if (!commandLineParser.getOnlyDrawGraph()) {
			chainManager.executeCommands(orderedCommands);
		}
	}
}