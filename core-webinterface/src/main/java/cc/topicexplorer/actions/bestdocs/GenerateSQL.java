package cc.topicexplorer.actions.bestdocs;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class GenerateSQL extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		BestDocumentsForGivenTopic bestDocAction = context.get("BEST_DOC_ACTION", BestDocumentsForGivenTopic.class);
		bestDocAction.executeQueriesAndWriteOutBestDocumentsForGivenTopic();
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("BestDocsCoreCreate");
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
