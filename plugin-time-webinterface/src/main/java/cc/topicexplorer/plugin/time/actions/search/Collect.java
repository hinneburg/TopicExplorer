package cc.topicexplorer.plugin.time.actions.search;

import java.util.ArrayList;

import org.apache.commons.chain.Context;

import cc.topicexplorer.actions.search.Search;
import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		Search searchAction = (Search) communicationContext
				.get("SEARCH_ACTION");
		
		searchAction.addSearchColumn("DOCUMENT.TIME$TIME_STAMP", "TIME$TIME_STAMP");
		Object sorting = communicationContext.get("sorting");
		if(sorting != null) {
			if(sorting.equals("TIME")) {
				ArrayList<String> orderBy = new ArrayList<String>();
				orderBy.add("DOCUMENT.TIME$TIME_STAMP");
				searchAction.setOrderBy(orderBy);
			}
		}
		
		
		communicationContext.put("SEARCH_ACTION", searchAction);
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("SearchCoreCreate");
		afterDependencies.add("SearchCoreGenerateSQL");
	}
}
