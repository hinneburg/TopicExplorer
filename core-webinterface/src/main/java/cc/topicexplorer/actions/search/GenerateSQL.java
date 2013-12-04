package cc.topicexplorer.actions.search;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;

public class GenerateSQL extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;

		Search searchAction = (Search) communicationContext.get("SEARCH_ACTION");

		searchAction.executeQuery();
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("SearchCoreCreate");
	}

}