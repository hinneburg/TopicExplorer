package cc.topicexplorer.actions.bestdocs;

import java.io.PrintWriter;
import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {

		String topicId = context.getString("TOPIC_ID");
		int offset = context.getInteger("OFFSET");
		PrintWriter pw = context.get("SERVLET_WRITER", PrintWriter.class);
		int limit = Integer.parseInt(properties.getProperty("DocBrowserLimit"));
		int numberOfTopics = Integer.parseInt(properties.getProperty("malletNumTopics"));

		BestDocumentsForGivenTopic bestDocAction = new BestDocumentsForGivenTopic(topicId, limit, offset, database, pw,
				numberOfTopics);
		context.bind("BEST_DOC_ACTION", bestDocAction);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("BestDocsCoreGenerateSQL");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}

}
