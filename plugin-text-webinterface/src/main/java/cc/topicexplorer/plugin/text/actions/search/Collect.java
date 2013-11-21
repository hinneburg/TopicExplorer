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
		searchAction.addSearchColumn("DOCUMENT.TEXT$FULLTEXT", "TEXT$FULLTEXT");
		searchAction.addWhereClause("MATCH(DOCUMENT.TEXT$FULLTEXT) AGAINST ('" + searchAction.getSearchWord()
				+ "' IN BOOLEAN MODE");

		communicationContext.put("BEST_DOC_ACTION", searchAction);
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("BestDocsCoreCreate");
		afterDependencies.add("BestDocsCoreGenerateSQL");
	}
}
