package cc.topicexplorer.plugin.text.actions.bestdocs;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		SelectMap documentMap;

		documentMap = (SelectMap) communicationContext.get("DOCUMENT_QUERY");

		documentMap.select.add("DOCUMENT.TEXT$TITLE");
		documentMap.select.add("CONCAT(SUBSTRING(DOCUMENT.TEXT$FULLTEXT FROM 1 FOR 150), '...') AS TEXT$FULLTEXT");

		communicationContext.put("DOCUMENT_QUERY", documentMap);
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("BestDocsCoreCreate");
		afterDependencies.add("BestDocsCoreGenerateSQL");
	}
}
