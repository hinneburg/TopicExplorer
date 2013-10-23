package cc.topicexplorer.web;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cc.topicexplorer.chain.ChainManagement;
import cc.topicexplorer.chain.CommunicationContext;

public class WebChainManagement {

	private static ChainManagement chainManagement = null;
	private static Map<String, Set<String>> dependencies;

	private WebChainManagement() {

	}

	public static void init() {
		try {
			if (chainManagement == null) {
				chainManagement = new ChainManagement();
				chainManagement.init();
				chainManagement.setCatalog("/catalog.xml");
				dependencies = chainManagement.getDependencies();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static CommunicationContext getCommunicationContext() {

		CommunicationContext newCommunicationContext = new CommunicationContext();

		newCommunicationContext
				.clone(chainManagement.getCommunicationContext());

		return newCommunicationContext;
	}

	public static List<String> getOrderedCommands(Set<String> startCommands,
			Set<String> endCommands) {
		return chainManagement.getOrderedCommands(dependencies, startCommands,
				endCommands);
	}

	public static void executeCommands(List<String> commands,
			CommunicationContext communicationContext) {
		chainManagement.executeCommands(commands, communicationContext);
	}

}
