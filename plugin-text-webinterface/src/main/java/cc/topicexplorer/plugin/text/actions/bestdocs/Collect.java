package cc.topicexplorer.plugin.text.actions.bestdocs;

import org.apache.commons.chain.Context;

import cc.topicexplorer.actions.bestdocs.BestDocumentsForGivenTopic;
import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;

		BestDocumentsForGivenTopic bestDocAction = (BestDocumentsForGivenTopic) communicationContext
				.get("BEST_DOC_ACTION");

		bestDocAction.addDocumentColumn("DOCUMENT.TEXT$TITLE", "TEXT$TITLE");
		bestDocAction.addDocumentColumn("CONCAT(SUBSTRING(DOCUMENT.TEXT$FULLTEXT FROM 1 FOR 150), '...')",
				"TEXT$FULLTEXT");
		communicationContext.put("BEST_DOC_ACTION", bestDocAction);
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("BestDocsCoreCreate");
		afterDependencies.add("BestDocsCoreGenerateSQL");
	}
}
