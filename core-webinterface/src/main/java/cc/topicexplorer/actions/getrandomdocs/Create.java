package cc.topicexplorer.actions.getrandomdocs;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		communicationContext.put("PRE_QUERY", new SelectMap());
		communicationContext.put("INNER_QUERY", new SelectMap());
		communicationContext.put("MAIN_QUERY", new SelectMap());
	}

	@Override
	public void addDependencies() {
		afterDependencies.add("GetRandomDocsCoreCollect");
	}

}
