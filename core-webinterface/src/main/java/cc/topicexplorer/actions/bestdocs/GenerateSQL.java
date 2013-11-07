package cc.topicexplorer.actions.bestdocs;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;

public class GenerateSQL extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;

		BestDocumentsForGivenTopic bestDocAction = (BestDocumentsForGivenTopic) communicationContext
				.get("BEST_DOC_ACTION");

		bestDocAction.executeQueriesAndWriteOutBestDocumentsForGivenTopic();
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("BestDocsCoreCreate");
	}

}