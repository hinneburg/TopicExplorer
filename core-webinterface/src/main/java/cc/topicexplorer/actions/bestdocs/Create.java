package cc.topicexplorer.actions.bestdocs;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;

		SelectMap documentMap = new SelectMap();

		communicationContext.put("DOCUMENT_QUERY", documentMap);
	}

	@Override
	public void addDependencies() {
		afterDependencies.add("BestDocsCoreCollect");
	}

}