package cc.topicexplorer.actions.getrandomdocs;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

import com.google.common.collect.Sets;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		context.bind("PRE_QUERY", new SelectMap());
		context.bind("INNER_QUERY", new SelectMap());
		context.bind("MAIN_QUERY", new SelectMap());
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("GetRandomDocsCoreCollect");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet();
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
