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
		int limit = Integer.parseInt(properties.getProperty("DocBrowserLimit"));
		int numberOfTopics = Integer.parseInt(properties.getProperty("malletNumTopics"));

		Search searchAction = new Search(searchWord, database, pw, limit, numberOfTopics, logger);
		communicationContext.put("SEARCH_ACTION", searchAction);
	}

	@Override
	public void addDependencies() {
		afterDependencies.add("SearchCoreGenerateSQL");
	}

}