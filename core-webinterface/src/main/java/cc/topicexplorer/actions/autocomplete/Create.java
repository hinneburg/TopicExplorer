package cc.topicexplorer.actions.autocomplete;

import java.io.PrintWriter;
import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		String searchWord = context.getString("SEARCH_WORD");
		PrintWriter pw = context.get("SERVLET_WRITER", PrintWriter.class);
		int numberOfTopics = Integer.parseInt(properties.getProperty("malletNumTopics"));

		Autocomplete autocompleteAction = new Autocomplete(searchWord, database, pw, numberOfTopics);
		context.bind("AUTOCOMPLETE_ACTION", autocompleteAction);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("AutocompleteCoreGenerateSQL");
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
