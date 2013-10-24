package cc.topicexplorer.actions.showdoc;


import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		
		SelectMap documentMap = (SelectMap) communicationContext.get("DOCUMENT_QUERY");
		
		String docId = (String) communicationContext.get("SHOW_DOC_ID");
		
		documentMap.select.add("DOCUMENT_ID");
		documentMap.from.add("DOCUMENT");
		documentMap.where.add("DOCUMENT_ID=" + docId);
		
		communicationContext.put("DOCUMENT_QUERY", documentMap);
		
		SelectMap topicMap = (SelectMap) communicationContext.get("TOPIC_QUERY");
		
		topicMap.select.add("TOPIC_ID");
		topicMap.select.add("POSITION_OF_TOKEN_IN_DOCUMENT"); 
		topicMap.select.add("TOKEN");
		topicMap.select.add("TOPIC_ID");
		topicMap.from.add("DOCUMENT_TERM_TOPIC");
		topicMap.where.add("DOCUMENT_ID=" + docId);
		
		communicationContext.put("TOPIC_QUERY", topicMap);
		
	}
	@Override
	public void addDependencies() {
		beforeDependencies.add("ShowDocCoreCreate"); 
		afterDependencies.add("ShowDocCoreGenerateSQL"); 
	}

}
