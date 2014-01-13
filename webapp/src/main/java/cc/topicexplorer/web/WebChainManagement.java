package cc.topicexplorer.web;

import java.util.List;
import java.util.Set;

import cc.topicexplorer.chain.ChainManagement;
import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.DependencyCollector;
import cc.topicexplorer.exceptions.CatalogNotInstantiableException;

public class WebChainManagement {

	private static ChainManagement chainManagement = null;

	private WebChainManagement() {

	}

	public static void init() {
		try {
			if (chainManagement == null) {
				chainManagement = new ChainManagement();
				chainManagement.init();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setCatalog(String catalog) {
		try {
			chainManagement.setCatalog(catalog);
		} catch (CatalogNotInstantiableException e) {
			e.printStackTrace();
		}
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
			// TODO find a channel where the message below can be printed to.
			// The RuntimeException e should be printed as well.
			// message:
			// "A command caused an exception and could not be processed."
		}
	}

}
