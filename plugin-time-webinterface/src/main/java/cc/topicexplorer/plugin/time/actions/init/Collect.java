package cc.topicexplorer.plugin.time.actions.init;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		SelectMap documentMap = (SelectMap) communicationContext.get("DOCUMENT_QUERY");
		System.out.println(documentMap.getSQLString());
		
		documentMap.select.add("DOCUMENT.TIME$TIME_STAMP");
		
		communicationContext.put("DOCUMENT_QUERY", documentMap);
		
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("InitCoreCreate");
		afterDependencies.add("InitCoreGenerateSQL");
	}
}
