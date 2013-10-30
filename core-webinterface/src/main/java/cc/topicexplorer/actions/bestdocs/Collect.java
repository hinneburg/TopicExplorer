package cc.topicexplorer.actions.bestdocs;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;

		SelectMap documentMap = (SelectMap) communicationContext.get("DOCUMENT_QUERY");
		String topicId = (String) communicationContext.get("TOPIC_ID");

		documentMap.select.add("DOCUMENT.DOCUMENT_ID");
		documentMap.from.add("DOCUMENT");
		documentMap.from.add("DOCUMENT_TOPIC");
		documentMap.where.add("DOCUMENT.DOCUMENT_ID=DOCUMENT_TOPIC.DOCUMENT_ID");
		documentMap.where.add("DOCUMENT_TOPIC.TOPIC_ID=" + topicId);
		documentMap.orderBy.add("PR_DOCUMENT_GIVEN_TOPIC");
		documentMap.limit = 20;

		communicationContext.put("DOCUMENT_QUERY", documentMap);
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("BestDocsCoreCreate");
		afterDependencies.add("BestDocsCoreGenerateSQL");
	}

}
