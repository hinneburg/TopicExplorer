package cc.topicexplorer.plugin.frame.actions.bestdocs;

import org.apache.commons.chain.Context;

import cc.topicexplorer.actions.bestdocs.BestDocumentsForGivenTopic;
import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;

		Object frameObject = communicationContext.get("frame");
		if(frameObject != null) {
			BestDocumentsForGivenTopic bestDocAction = (BestDocumentsForGivenTopic) communicationContext
				.get("BEST_DOC_ACTION");

			String frame = (String) frameObject;
			
			bestDocAction.addFrom("FRAMES");
			
			bestDocAction.addWhereClause("FRAMES.DOCUMENT_ID = DOCUMENT.DOCUMENT_ID");
			bestDocAction.addWhereClause("FRAMES.FRAME='" + frame + "'");
					
			communicationContext.put("BEST_DOC_ACTION", bestDocAction);
		}
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("BestDocsCoreCreate");
		afterDependencies.add("BestDocsCoreGenerateSQL");
	}
}
