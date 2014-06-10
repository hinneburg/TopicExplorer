package cc.topicexplorer.plugin.hierarchicaltopic.actions.gettopics;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {

		SelectMap mainQueryMap, preQueryMap;

		mainQueryMap = context.get("MAIN_QUERY", SelectMap.class);
		mainQueryMap.select.add("TOPIC.HIERARCHICAL_TOPIC$START");
		mainQueryMap.select.add("TOPIC.HIERARCHICAL_TOPIC$CLUSTER_MEMBERSHIP");
		context.rebind("MAIN_QUERY", mainQueryMap);

		preQueryMap = context.get("PRE_QUERY", SelectMap.class);
		preQueryMap.where.add("TOPIC.HIERARCHICAL_TOPIC$START=TOPIC.HIERARCHICAL_TOPIC$END");
		context.rebind("PRE_QUERY", preQueryMap);
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
