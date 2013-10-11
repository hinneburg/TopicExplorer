package cc.topicexplorer.actions.gettopics;

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
		
		preQueryMap.select.add("TOPIC_ID");
		preQueryMap.from.add("TOPIC");
		
		communicationContext.put("PRE_QUERY", preQueryMap);
	
		innerQueryMap = (SelectMap) communicationContext.get("INNER_QUERY");
		
		innerQueryMap.select.add("COUNT(*) AS words");
		innerQueryMap.select.add("SUM(PR_TERM_GIVEN_TOPIC) AS maxRelevanz");
		innerQueryMap.select.add("TOPIC_ID");
		innerQueryMap.from.add("TERM_TOPIC");
		
		communicationContext.put("INNER_QUERY", innerQueryMap);
		
		mainQueryMap = (SelectMap) communicationContext.get("MAIN_QUERY");
		
		mainQueryMap.select.add("(PR_TERM_GIVEN_TOPIC/maxRelevanz) AS relevanz");
		mainQueryMap.select.add("TERM.TERM_NAME");
//		mainQueryMap.select.add("maxRelevanz");
		mainQueryMap.from.add("TERM_TOPIC");
		mainQueryMap.from.add("TERM");
		mainQueryMap.from.add("TOPIC");
		mainQueryMap.where.add("CountRelevanz.TOPIC_ID = TERM_TOPIC.TOPIC_ID");
		mainQueryMap.where.add("TERM_TOPIC.TERM_ID = TERM.TERM_ID");
		mainQueryMap.where.add("TERM_TOPIC.TOPIC_ID = TOPIC.TOPIC_ID");
		mainQueryMap.orderBy.add("relevanz DESC");
		mainQueryMap.limit = 40;
		
		communicationContext.put("MAIN_QUERY", mainQueryMap);
		
	}
	
	@Override
	public void addDependencies() {
		beforeDependencies.add("GetTopicsCoreCreate");
		afterDependencies.add("GetTopicsCoreGenerateSQL");
	}
}
