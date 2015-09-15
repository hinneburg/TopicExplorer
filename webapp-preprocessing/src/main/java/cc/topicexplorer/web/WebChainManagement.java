package cc.topicexplorer.web;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.commandmanager.core.CommandClass;
import cc.commandmanager.core.CommandGraph;
import cc.commandmanager.core.CommandManager;
import cc.commandmanager.core.Context;
import cc.commandmanager.core.Try;

public class WebChainManagement {

	private static CommandManager commandManager;
	private static final Logger logger = Logger.getLogger(WebChainManagement.class);
	private static boolean isInitialized = false;
	private static Context context;

	private WebChainManagement() {
		throw new UnsupportedOperationException();
	}

	public static void init(Context context, String catalogLocation) {
		if (!isInitialized) {
			WebChainManagement.context = context;
			File catalogfile = new File(catalogLocation);
			Try<CommandGraph> commandgraph = CommandGraph.fromXml(catalogfile);
			commandManager = new CommandManager(commandgraph.get());
			isInitialized = true;
		} else {
			throw new IllegalStateException("Class has already been initialized.");
		}
	}

	public static Context getContext() {
		if (isInitialized) {
			return context;
		} else {
			throw new IllegalStateException("Class must be initialized before getContext can be called.");
		}
	}

	@SuppressWarnings("unchecked")
	public static List<CommandClass> getOrderedCommands(Set<String> startCommands) {
		if (isInitialized) {
			return commandManager.getCommandGraph().topologicalOrderOfAllCommands();
		} else {
			throw new IllegalStateException("Class must be initialized before getOrderedCommands can be called.");
		}
	}

	public static void executeCommands(List<String> commands, Context context) {
		if (isInitialized) {
			try {
				commandManager.executeCommands(commands, context);
			} catch (RuntimeException e) {
				logger.error("A command caused a RuntimeException.", e);
			}
		} else {
			throw new IllegalStateException("Class must be initialized before executeCommands can be called.");
		}
	}

}
