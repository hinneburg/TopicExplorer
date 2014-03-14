package cc.topicexplorer.plugin.colortopic.actions.init;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class Collect extends TableSelectCommand {
	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		SelectMap topicMap;

		topicMap = (SelectMap) communicationContext.get("TOPIC_QUERY");

		topicMap.select.add("TOPIC.COLOR_TOPIC$COLOR");

		communicationContext.put("TOPIC_QUERY", topicMap);
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("InitCoreCreate");
		afterDependencies.add("InitCoreGenerateSQL");
	}
}
