package cc.topicexplorer.plugin.text.actions.search;

import org.apache.commons.chain.Context;

import cc.topicexplorer.actions.search.Search;
import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;

public class Collect extends TableSelectCommand {
	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		
		Search searchAction = (Search) communicationContext.get("SEARCH_ACTION");
		
		searchAction.addSearchColumn("DOCUMENT.TEXT$TITLE", "TEXT$TITLE");
		searchAction
				.addSearchColumn("CONCAT(SUBSTRING(DOCUMENT.TEXT$FULLTEXT FROM 1 FOR 150), '...')", "TEXT$FULLTEXT");
		if (properties.getProperty("plugins").contains("fulltext")) {
			searchAction.addWhereClause("MATCH(DOCUMENT.FULLTEXT$FULLTEXT) AGAINST ('" + searchAction.getSearchWord()
					+ "' IN BOOLEAN MODE)");
		} else {
			searchAction.addWhereClause("MATCH(DOCUMENT.TEXT$FULLTEXT) AGAINST ('" + searchAction.getSearchWord()
					+ "' IN BOOLEAN MODE)");
		}
		communicationContext.put("SEARCH_ACTION", searchAction);
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("SearchCoreCreate");
		afterDependencies.add("SearchCoreGenerateSQL");
	}
}
