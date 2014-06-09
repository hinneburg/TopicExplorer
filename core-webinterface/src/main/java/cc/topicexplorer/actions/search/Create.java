package cc.topicexplorer.actions.search;

import java.io.PrintWriter;
import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		String searchWord = context.getString("SEARCH_WORD");
		int offset = context.getInteger("OFFSET");
		PrintWriter pw = context.get("SERVLET_WRITER", PrintWriter.class);

		int limit = Integer.parseInt(properties.getProperty("DocBrowserLimit"));
		int numberOfTopics = Integer.parseInt(properties.getProperty("malletNumTopics"));

		Search searchAction = new Search(searchWord, database, pw, limit, offset, numberOfTopics);
		context.bind("SEARCH_ACTION", searchAction);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("SearchCoreGenerateSQL");
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
