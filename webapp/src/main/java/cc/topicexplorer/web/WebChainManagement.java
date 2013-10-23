package cc.topicexplorer.web;

import java.util.List;
import java.util.Set;

import cc.topicexplorer.chain.ChainManagement;
import cc.topicexplorer.chain.CommunicationContext;

public class WebChainManagement {

	private static ChainManagement chainManagement = null;

	private WebChainManagement() {

	}

	public static void init() {
		try {
			if (chainManagement == null) {
				chainManagement = new ChainManagement();
				chainManagement.init();
				chainManagement.setCatalog("/catalog.xml");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static CommunicationContext getCommunicationContext() {
		return chainManagement.getCommunicationContext().clone();
	}

	public static List<String> getOrderedCommands(Set<String> startCommands,
			Set<String> endCommands) {
		return chainManagement.getOrderedCommands(
				chainManagement.getDependencies(), startCommands, endCommands);
	}

	public static void executeCommands(List<String> commands,
			CommunicationContext communicationContext) {
		chainManagement.executeCommands(commands, communicationContext);
	}

}
