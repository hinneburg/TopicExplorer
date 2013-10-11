package cc.topicexplorer.actions.gettopics;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class Create extends TableSelectCommand {
	
	@Override
	public void tableExecute(Context context) {
		SelectMap preQueryMap, innerQueryMap, mainQueryMap;
		
		CommunicationContext communicationContext = (CommunicationContext) context;
		
		preQueryMap = new SelectMap();
		
		communicationContext.put("PRE_QUERY", preQueryMap);
		
		innerQueryMap = new SelectMap();
		
		communicationContext.put("INNER_QUERY", innerQueryMap);
		
		mainQueryMap = new SelectMap();
		communicationContext.put("MAIN_QUERY", mainQueryMap);
	}
	
	@Override
	public void addDependencies() {
		afterDependencies.add("GetTopicsCoreCollect");
	}	

}

