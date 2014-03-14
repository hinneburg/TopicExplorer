package cc.topicexplorer.plugin.hierarchicaltopic.actions.gettopics;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class Collect extends TableSelectCommand {
	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		SelectMap mainQueryMap, preQueryMap;

		mainQueryMap = (SelectMap) communicationContext.get("MAIN_QUERY");

		mainQueryMap.select.add("TOPIC.HIERARCHICAL_TOPIC$START");
		mainQueryMap.select.add("TOPIC.HIERARCHICAL_TOPIC$CLUSTER_MEMBERSHIP");

		communicationContext.put("MAIN_QUERY", mainQueryMap);

		preQueryMap = (SelectMap) communicationContext.get("PRE_QUERY");

		preQueryMap.where.add("TOPIC.HIERARCHICAL_TOPIC$START=TOPIC.HIERARCHICAL_TOPIC$END");

		communicationContext.put("PRE_QUERY", preQueryMap);
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("GetTopicsCoreCreate");
		afterDependencies.add("GetTopicsCoreGenerateSQL");
	}
}
