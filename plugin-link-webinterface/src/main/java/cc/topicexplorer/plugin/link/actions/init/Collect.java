package cc.topicexplorer.plugin.link.actions.init;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		SelectMap documentMap;

		documentMap = (SelectMap) communicationContext.get("DOCUMENT_QUERY");

		documentMap.select.add("DOCUMENT.LINK$URL");
		
		communicationContext.put("DOCUMENT_QUERY", documentMap);
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("InitCoreCreate");
		afterDependencies.add("InitCoreGenerateSQL");
	}
}
