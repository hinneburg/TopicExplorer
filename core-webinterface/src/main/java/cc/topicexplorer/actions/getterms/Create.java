package cc.topicexplorer.actions.getterms;

import java.io.PrintWriter;
import java.util.Properties;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;

		Properties properties = (Properties) communicationContext.get("properties");

		PrintWriter pw = (PrintWriter) communicationContext.get("SERVLET_WRITER");

		AllTerms allTermsAction = new AllTerms(database, pw);

		communicationContext.put("ALL_TERMS_ACTION", allTermsAction);
	}

	@Override
	public void addDependencies() {
		afterDependencies.add("AllTermsCoreGenerateSQL");
	}

}