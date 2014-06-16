package cc.topicexplorer.plugin.frame.actions.bestdocs;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.actions.bestdocs.BestDocumentsForGivenTopic;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		if(context.containsKey("frame")) {
			String frame = context.getString("frame");
	
			BestDocumentsForGivenTopic bestDocAction = context.get("BEST_DOC_ACTION", BestDocumentsForGivenTopic.class);
			bestDocAction.addFrom("FRAMES");
			bestDocAction.addWhereClause("FRAMES.DOCUMENT_ID = DOCUMENT.DOCUMENT_ID");
			bestDocAction.addWhereClause("FRAMES.FRAME='" + frame + "'");
			bestDocAction.addWhereClause("FRAMES.TOPIC_ID = DOCUMENT_TOPIC.TOPIC_ID");
	
			context.rebind("BEST_DOC_ACTION", bestDocAction);
		}
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
