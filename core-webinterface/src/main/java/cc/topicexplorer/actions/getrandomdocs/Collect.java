package cc.topicexplorer.actions.getrandomdocs;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class Collect extends TableSelectCommand {
	
	@Override
	public void tableExecute(Context context) {
		SelectMap preQueryMap, innerQueryMap, mainQueryMap;
		
		CommunicationContext communicationContext = (CommunicationContext) context;
		
		preQueryMap = (SelectMap) communicationContext.get("PRE_QUERY");
		
		preQueryMap.select.add("COUNT(*) AS COUNT"); 
		preQueryMap.from.add("DOCUMENT"); 
		
		communicationContext.put("PRE_QUERY", preQueryMap);
		
		innerQueryMap = (SelectMap) communicationContext.get("INNER_QUERY");
		
		innerQueryMap.select.add("DOCUMENT.DOCUMENT_ID"); 
		innerQueryMap.from.add("DOCUMENT"); 
		innerQueryMap.limit = 20; 
		
		communicationContext.put("INNER_QUERY", innerQueryMap);
		
		mainQueryMap = (SelectMap) communicationContext.get("MAIN_QUERY");
		
		mainQueryMap.select.add("*"); 
		mainQueryMap.from.add("DOCUMENT_TOPIC y"); 
		mainQueryMap.where.add("x.DOCUMENT_ID=y.DOCUMENT_ID"); 
		mainQueryMap.orderBy.add("x.DOCUMENT_ID"); 
		mainQueryMap.orderBy.add("y.TOPIC_ID"); 
		
		communicationContext.put("MAIN_QUERY", mainQueryMap);
	}
	
	@Override
	public void addDependencies() {
		beforeDependencies.add("GetRandomDocsCoreCreate");
		afterDependencies.add("GetRandomDocsCoreGenerateSQL");
	}	

}
