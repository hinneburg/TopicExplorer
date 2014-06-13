package cc.topicexplorer.plugin.link.actions.search;

import org.apache.commons.chain.Context;

import cc.topicexplorer.actions.search.Search;
import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;

public class Collect extends TableSelectCommand {
	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;

		Search searchAction = (Search) communicationContext.get("SEARCH_ACTION");

		searchAction.addSearchColumn("DOCUMENT.DOCUMENT.LINK$URL", "DOCUMENT.LINK$URL");
		
		communicationContext.put("SEARCH_ACTION", searchAction);
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("SearchCoreCreate");
		afterDependencies.add("SearchCoreGenerateSQL");
	}
}
