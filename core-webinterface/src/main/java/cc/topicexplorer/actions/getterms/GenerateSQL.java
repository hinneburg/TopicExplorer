package cc.topicexplorer.actions.getterms;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;

public class GenerateSQL extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;

		AllTerms allTermsAction = (AllTerms) communicationContext.get("ALL_TERMS_ACTION");

		allTermsAction.readAllTermsAndGenerateJson();
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("AllTermsCoreCreate");
	}

}