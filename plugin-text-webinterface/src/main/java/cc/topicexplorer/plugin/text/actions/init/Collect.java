package cc.topicexplorer.plugin.text.actions.init;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		SelectMap documentMap, topicMap;

		documentMap = (SelectMap) communicationContext.get("DOCUMENT_QUERY");

		documentMap.select.add("DOCUMENT.TEXT$TITLE");
		documentMap.select.add("CONCAT(SUBSTRING(DOCUMENT.TEXT$FULLTEXT FROM 1 FOR 150), '...') AS TEXT$FULLTEXT");

		communicationContext.put("DOCUMENT_QUERY", documentMap);

		topicMap = (SelectMap) communicationContext.get("TOPIC_QUERY");

		topicMap.select.add("TOPIC.TEXT$TOPIC_LABEL");

		communicationContext.put("TOPIC_QUERY", topicMap);
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("InitCoreCreate");
		afterDependencies.add("InitCoreGenerateSQL");
	}
}
