package cc.topicexplorer.plugin.text.actions.search;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.actions.search.Search;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		Search searchAction = context.get("SEARCH_ACTION", Search.class);

		searchAction.addSearchColumn("DOCUMENT.TEXT$TITLE", "TEXT$TITLE");
		searchAction.addSearchColumn("CONCAT(SUBSTRING(DOCUMENT.TEXT$FULLTEXT FROM 1 FOR 150), '...')", "TEXT$SNIPPET");
		if (properties.getProperty("plugins").contains("fulltext")) {
			searchAction.addWhereClause("MATCH(DOCUMENT.FULLTEXT$FULLTEXT) AGAINST ('" + searchAction.getSearchWord()
					+ "' IN BOOLEAN MODE)");
		} else {
			searchAction.addWhereClause("MATCH(DOCUMENT.TEXT$FULLTEXT) AGAINST ('" + searchAction.getSearchWord()
					+ "' IN BOOLEAN MODE)");
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
