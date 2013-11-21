package cc.topicexplorer.actions.autocomplete;

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

		Autocomplete autocompleteAction = new Autocomplete(searchWord, database, pw);
		communicationContext.put("AUTOCOMPLETE_ACTION", autocompleteAction);
	}

	@Override
	public void addDependencies() {
		afterDependencies.add("AutocompleteCoreGenerateSQL");
	}

}