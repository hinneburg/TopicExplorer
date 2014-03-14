package cc.topicexplorer.actions.getterms;

import java.io.PrintWriter;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;

		PrintWriter pw = (PrintWriter) communicationContext.get("SERVLET_WRITER");

		AllTerms allTermsAction = new AllTerms(database, pw);

		communicationContext.put("ALL_TERMS_ACTION", allTermsAction);
	}

	@Override
	public void addDependencies() {
		afterDependencies.add("AllTermsCoreGenerateSQL");
	}

}