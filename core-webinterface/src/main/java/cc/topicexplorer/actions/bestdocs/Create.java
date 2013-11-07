package cc.topicexplorer.actions.bestdocs;

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

		String topicId = (String) communicationContext.get("TOPIC_ID");
		PrintWriter pw = (PrintWriter) communicationContext.get("SERVLET_WRITER");
		int limit = Integer.parseInt(properties.getProperty("DocBrowserLimit"));
		int numberOfTopics = Integer.parseInt(properties.getProperty("malletNumTopics"));

		BestDocumentsForGivenTopic bestDocAction = new BestDocumentsForGivenTopic(topicId, limit, database, pw,
				numberOfTopics);

		communicationContext.put("BEST_DOC_ACTION", bestDocAction);
	}

	@Override
	public void addDependencies() {
		afterDependencies.add("BestDocsCoreGenerateSQL");
	}

}