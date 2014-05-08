package cc.topicexplorer.plugin.time.actions.bestdocs;

import org.apache.commons.chain.Context;

import cc.topicexplorer.actions.bestdocs.BestDocumentsForGivenTopic;
import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;

		Object week = communicationContext.get("week");
		if(week != null) {
			BestDocumentsForGivenTopic bestDocAction = (BestDocumentsForGivenTopic) communicationContext
				.get("BEST_DOC_ACTION");

			int startTstamp = Integer.valueOf((String) week);
			int endTstamp =  startTstamp + 604800;
			
			bestDocAction.addWhereClause("DOCUMENT.TIME$TIME_STAMP > " + startTstamp);
			bestDocAction.addWhereClause("DOCUMENT.TIME$TIME_STAMP < " + endTstamp);
					
			communicationContext.put("BEST_DOC_ACTION", bestDocAction);
		}
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("BestDocsCoreCreate");
		afterDependencies.add("BestDocsCoreGenerateSQL");
	}
}
