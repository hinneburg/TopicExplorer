package cc.topicexplorer.actions.search;

import java.io.PrintWriter;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;

		String searchWord = (String) communicationContext.get("SEARCH_WORD");
		PrintWriter pw = (PrintWriter) communicationContext.get("SERVLET_WRITER");

		Search searchAction = new Search(searchWord, database, pw);
		communicationContext.put("SEARCH_ACTION", searchAction);
	}

	@Override
	public void addDependencies() {
		afterDependencies.add("SearchCoreGenerateSQL");
	}

}