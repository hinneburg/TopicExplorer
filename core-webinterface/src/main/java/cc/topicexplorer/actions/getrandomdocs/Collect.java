package cc.topicexplorer.actions.getrandomdocs;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;

public class Collect extends TableSelectCommand {
	
	@Override
	public void tableExecute(Context context) {
		HashMap<String, ArrayList<String>> preQueryMap, innerQueryMap, mainQueryMap;
		
		CommunicationContext communicationContext = (CommunicationContext) context;
		
		preQueryMap = this.newSelectMap();
		
		preQueryMap.get("SELECT").add("COUNT(*) AS COUNT"); 
		preQueryMap.get("FROM").add("DOCUMENT"); 
		
		communicationContext.put("PRE_QUERY", preQueryMap);
		
		innerQueryMap = this.newSelectMap();
		
		innerQueryMap.get("SELECT").add("DOCUMENT.DOCUMENT_ID"); 
		innerQueryMap.get("FROM").add("DOCUMENT"); 
		innerQueryMap.get("LIMIT").add("20"); 
		
		communicationContext.put("INNER_QUERY", innerQueryMap);
		
		mainQueryMap = this.newSelectMap();
		
		mainQueryMap.get("SELECT").add("*"); 
		mainQueryMap.get("FROM").add("DOCUMENT_TOPIC y"); 
		mainQueryMap.get("WHERE").add("x.DOCUMENT_ID=y.DOCUMENT_ID"); 
		mainQueryMap.get("ORDERBY").add("x.DOCUMENT_ID"); 
		mainQueryMap.get("ORDERBY").add("y.TOPIC_ID"); 
		
		communicationContext.put("MAIN_QUERY", mainQueryMap);
	}
	
	@Override
	public void addDependencies() {
		afterDependencies.add("CreateSQL");
	}	

}
