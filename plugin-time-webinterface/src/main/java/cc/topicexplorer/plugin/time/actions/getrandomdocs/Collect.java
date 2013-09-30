package cc.topicexplorer.plugin.time.actions.getrandomdocs;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class Collect extends TableSelectCommand {
	@Override
	public void tableExecute(Context context) {		
		CommunicationContext communicationContext = (CommunicationContext) context;
		SelectMap innerQueryMap;
		
		innerQueryMap = (SelectMap) communicationContext.get("INNER_QUERY");
		
		innerQueryMap.select.add("DOCUMENT.TIME$TIME_STAMP");
		
		communicationContext.put("INNER_QUERY", innerQueryMap);
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("RandomDocsCollect");
		afterDependencies.add("CreateSQL");
	}
}
