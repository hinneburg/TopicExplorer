package cc.topicexplorer.web;

import java.io.File;

import org.apache.log4j.Logger;

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
			if (!commandgraph.isPresent()) {
				logger.error("CommandGraph not present");
			}
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



	public static void executeCommands(Iterable<String> commands, Context context) {
		if (isInitialized) {
			try {
				commandManager.executeConnectedComponentsContaining(commands, context);
			} catch (RuntimeException e) {
				logger.error("A command caused a RuntimeException.", e);
			}
		} else {
			throw new IllegalStateException("Class must be initialized before executeCommands can be called.");
		}
	}

}
