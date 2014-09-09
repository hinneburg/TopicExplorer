package cc.topicexplorer.actions.gettopics;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class GenerateSQL extends TableSelectCommand {
	@Override
	public void tableExecute(Context context) {
		GetTopics getTopicsAction = context.get("GET_TOPICS_ACTION", GetTopics.class);
		getTopicsAction.executeQueriesAndWriteOutTopics(Integer.parseInt((String) properties.get("TopicBestItemLimit")));
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
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
