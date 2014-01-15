package cc.topicexplorer.web;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.chain.ChainManagement;
import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.DependencyCollector;

public class WebChainManagement {

	private static ChainManagement chainManagement = null;
	private static Logger logger = Logger.getRootLogger();

	private WebChainManagement() {
	}

	public static void init() {
		if (chainManagement == null) {
			chainManagement = new ChainManagement();
			chainManagement.init();
		}
	}

	public static void setCatalog(String catalog) {
		chainManagement.setCatalog(catalog);
	}

	public static CommunicationContext getCommunicationContext() {
		return chainManagement.getCommunicationContext().clone();
	}

	public static List<String> getOrderedCommands(Set<String> startCommands, Set<String> endCommands) {
		return chainManagement.getOrderedCommands(new DependencyCollector(chainManagement.catalog).getDependencies(),
				startCommands, endCommands);
	}

	public static void executeCommands(List<String> commands, CommunicationContext communicationContext) {
		try {
			chainManagement.executeCommands(commands, communicationContext);
		} catch (RuntimeException e) {
			logger.error("A command caused a RuntimeException.", e);
		}
	}

}
