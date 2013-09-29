package cc.topicexplorer.plugin.time.actions.getrandomdocs;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;

public class Collect extends TableSelectCommand {

	@SuppressWarnings("unchecked")
	@Override
	public void tableExecute(Context context) {		
		CommunicationContext communicationContext = (CommunicationContext) context;
		HashMap<String, ArrayList<String>> innerQueryMap;
		
		innerQueryMap = (HashMap<String, ArrayList<String>>) communicationContext.get("INNER_QUERY");
		
		innerQueryMap.get("SELECT").add("DOCUMENT.TIME$TIME_STAMP");
		
		communicationContext.put("INNER_QUERY", innerQueryMap);
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("RandomDocsCollect");
		afterDependencies.add("CreateSQL");
	}
}
