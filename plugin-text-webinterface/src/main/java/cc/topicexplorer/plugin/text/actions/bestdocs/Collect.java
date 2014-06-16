package cc.topicexplorer.plugin.text.actions.bestdocs;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.actions.bestdocs.BestDocumentsForGivenTopic;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		BestDocumentsForGivenTopic bestDocAction = context.get("BEST_DOC_ACTION", BestDocumentsForGivenTopic.class);

		bestDocAction.addDocumentColumn("DOCUMENT.TEXT$TITLE", "TEXT$TITLE");
		bestDocAction.addDocumentColumn("CONCAT(SUBSTRING(DOCUMENT.TEXT$FULLTEXT FROM 1 FOR 150), '...')",
				"TEXT$SNIPPET");

		context.rebind("BEST_DOC_ACTION", bestDocAction);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("BestDocsCoreGenerateSQL");
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
