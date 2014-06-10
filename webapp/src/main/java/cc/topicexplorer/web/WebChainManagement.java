package cc.topicexplorer.web;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.commandmanager.core.CommandManagement;
import cc.commandmanager.core.Context;

public class WebChainManagement {

	private static CommandManagement commandManagement;
	private static final Logger logger = Logger.getLogger(WebChainManagement.class);
	private static boolean isInitialized = false;
	private static Context context;

	private WebChainManagement() {
		throw new UnsupportedOperationException();
	}

	public static void init(Context context, String catalogLocation) {
		if (!isInitialized) {
			WebChainManagement.context = context;
			commandManagement = new CommandManagement(catalogLocation, context);
			isInitialized = true;
		} else {
			throw new IllegalStateException("Class has already been initialized.");
		}
	}

	public static Context getContext() {
		if (isInitialized) {
			return new Context(context);
		} else {
			throw new IllegalStateException("Class must be initialized before getContext can be called.");
		}
	}

	@SuppressWarnings("unchecked")
	public static List<String> getOrderedCommands(Set<String> startCommands) {
		if (isInitialized) {
			return commandManagement.getOrderedCommands(startCommands, Collections.EMPTY_SET);
		} else {
			throw new IllegalStateException("Class must be initialized before getOrderedCommands can be called.");
		}
	}

	public static void executeCommands(List<String> commands, Context context) {
		if (isInitialized) {
			try {
				commandManagement.executeCommands(commands, context);
			} catch (RuntimeException e) {
				logger.error("A command caused a RuntimeException.", e);
			}
		} else {
			throw new IllegalStateException("Class must be initialized before executeCommands can be called.");
		}
	}

}
