package cc.topicexplorer.plugin.time.actions.search;

import java.util.ArrayList;
import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.actions.search.Search;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		Search searchAction = context.get("SEARCH_ACTION", Search.class);
		searchAction.addSearchColumn("DOCUMENT.TIME$TIME_STAMP", "TIME$TIME_STAMP");
		if(context.containsKey("sorting")) {
			String sorting = context.getString("sorting");
			if (sorting.equals("TIME")) {
				ArrayList<String> orderBy = new ArrayList<String>();
				orderBy.add("DOCUMENT.TIME$TIME_STAMP");
				searchAction.setOrderBy(orderBy);
			}
		}
		context.rebind("SEARCH_ACTION", searchAction);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("SearchCoreGenerateSQL");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("SearchCoreCreate");
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
