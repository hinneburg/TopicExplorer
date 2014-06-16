package cc.topicexplorer.plugin.link.actions.getrandomdocs;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		SelectMap innerQueryMap = context.get("INNER_QUERY", SelectMap.class);
		innerQueryMap.select.add("DOCUMENT.LINK$URL");
		context.rebind("INNER_QUERY", innerQueryMap);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("GetRandomDocsCoreCreate", "GetRandomDocsCoreGenerateSQL");
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
