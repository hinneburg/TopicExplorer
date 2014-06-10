package cc.topicexplorer.plugin.time.actions.getrandomdocs;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		SelectMap innerQueryMap = context.get("INNER_QUERY", SelectMap.class);
		innerQueryMap.select.add("DOCUMENT.TIME$TIME_STAMP");
		context.rebind("INNER_QUERY", innerQueryMap);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("GetRandomDocsCoreGenerateSQL");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("GetRandomDocsCoreCreate");
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
