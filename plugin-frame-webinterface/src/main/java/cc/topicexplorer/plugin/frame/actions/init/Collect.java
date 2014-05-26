package cc.topicexplorer.plugin.frame.actions.init;

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

		topicMap.select.add("FRAME$FRAME_COUNT");
		topicMap.select.add("FRAME$UNIQUE_FRAME_COUNT");
		
		communicationContext.put("TOPIC_QUERY", topicMap);
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("InitCoreCreate");
		afterDependencies.add("InitCoreGenerateSQL");
	}
}
