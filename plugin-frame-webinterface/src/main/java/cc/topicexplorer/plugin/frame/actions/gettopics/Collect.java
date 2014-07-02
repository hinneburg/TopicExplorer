package cc.topicexplorer.plugin.frame.actions.gettopics;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.actions.gettopics.GetTopics;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		GetTopics getTopicsAction = context.get("GET_TOPICS_ACTION", GetTopics.class);

		getTopicsAction.addTopicColumn("TOPIC.FRAME$FRAME_COUNT", "FRAME$FRAME_COUNT");
		getTopicsAction.addTopicColumn("TOPIC.FRAME$UNIQUE_FRAME_COUNT", "FRAME$UNIQUE_FRAME_COUNT");
		
		context.rebind("GET_TOPICS_ACTION", getTopicsAction);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("GetTopicsCoreGenerateSQL");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("GetTopicsCoreCreate");
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
