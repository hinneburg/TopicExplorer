package cc.topicexplorer.plugin.link.actions.search;


import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

import cc.topicexplorer.actions.search.Search;
import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;

public class Collect extends TableSelectCommand {
	@Override
	public void tableExecute(Context context) {

		Search searchAction = context.get("SEARCH_ACTION", Search.class);

		searchAction.addSearchColumn("DOCUMENT.LINK$URL", "LINK$URL");
		
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
		return Collections.emptySet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Collections.emptySet();
	}
}
