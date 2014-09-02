package cc.topicexplorer.plugin.hierarchicaltopic.actions.gettopics;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.actions.gettopics.GetTopics;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		GetTopics getTopicsAction = context.get("GET_TOPICS_ACTION", GetTopics.class);

		getTopicsAction.addTopicColumn("TOPIC.HIERARCHICAL_TOPIC$START", "HIERARCHICAL_TOPIC$START");
		getTopicsAction.addTopicColumn("TOPIC.HIERARCHICAL_TOPIC$CLUSTER_MEMBERSHIP", "HIERARCHICAL_TOPIC$CLUSTER_MEMBERSHIP");
		getTopicsAction.addWhereClause("TOPIC.HIERARCHICAL_TOPIC$START=TOPIC.HIERARCHICAL_TOPIC$END");
		getTopicsAction.addOrderBy("TOPIC.HIERARCHICAL_TOPIC$START");
		
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
