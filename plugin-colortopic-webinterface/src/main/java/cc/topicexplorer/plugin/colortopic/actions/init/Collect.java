package cc.topicexplorer.plugin.colortopic.actions.init;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		SelectMap topicMap = context.get("TOPIC_QUERY", SelectMap.class);
		topicMap.select.add("TOPIC.COLOR_TOPIC$COLOR");
		context.rebind("TOPIC_QUERY", topicMap);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("InitCoreGenerateSQL");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("InitCoreCreate");
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
