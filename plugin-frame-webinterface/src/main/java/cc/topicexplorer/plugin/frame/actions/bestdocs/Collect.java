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
			String frameType = context.getString("frameType");
			
			BestDocumentsForGivenTopic bestDocAction = context.get("BEST_DOC_ACTION", BestDocumentsForGivenTopic.class);
			bestDocAction.addFrom("FRAME$FRAMES");
			bestDocAction.addWhereClause("FRAME$FRAMES.DOCUMENT_ID = DOCUMENT.DOCUMENT_ID");
			bestDocAction.addWhereClause("FRAME$FRAMES.FRAME='" + frame + "'");
			bestDocAction.addWhereClause("FRAME$FRAMES.FRAME_TYPE='" + frameType + "'");
			bestDocAction.addWhereClause("FRAME$FRAMES.TOPIC_ID = DOCUMENT_TOPIC.TOPIC_ID");
	
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
