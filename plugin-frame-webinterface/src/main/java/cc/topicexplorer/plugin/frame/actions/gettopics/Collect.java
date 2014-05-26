package cc.topicexplorer.plugin.frame.actions.gettopics;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class Collect extends TableSelectCommand {
	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		SelectMap mainQueryMap;

		mainQueryMap = (SelectMap) communicationContext.get("MAIN_QUERY");

		mainQueryMap.select.add("FRAME$FRAME_COUNT");
		mainQueryMap.select.add("FRAME$UNIQUE_FRAME_COUNT");
		
		communicationContext.put("MAIN_QUERY", mainQueryMap);
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("GetTopicsCoreCreate");
		afterDependencies.add("GetTopicsCoreGenerateSQL");
	}
}
