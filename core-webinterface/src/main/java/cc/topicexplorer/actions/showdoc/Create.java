package cc.topicexplorer.actions.showdoc;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		communicationContext.put("DOCUMENT_QUERY", new SelectMap());
		communicationContext.put("TOPIC_QUERY", new SelectMap());
	}

	@Override
	public void addDependencies() {
		afterDependencies.add("ShowDocCoreCollect");
	}

}
