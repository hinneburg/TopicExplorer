package cc.topicexplorer.actions.init;


import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		
		SelectMap documentMap = (SelectMap) communicationContext.get("DOCUMENT_QUERY");
		
		documentMap.select.add("DOCUMENT_ID");
		documentMap.from.add("DOCUMENT");
		documentMap.limit = Integer.parseInt(properties.getProperty("DocBrowserLimit"));;
		
		communicationContext.put("DOCUMENT_QUERY", documentMap);

		SelectMap topicMap = (SelectMap) communicationContext.get("TOPIC_QUERY");
		
		topicMap.select.add("TOPIC.TOPIC_ID");
		topicMap.from.add("TOPIC");
		
		communicationContext.put("TOPIC_QUERY", topicMap);
		
	}
	@Override
	public void addDependencies() {
		beforeDependencies.add("InitCoreCreate"); 
		afterDependencies.add("InitCoreGenerateSQL"); 
	}

}
