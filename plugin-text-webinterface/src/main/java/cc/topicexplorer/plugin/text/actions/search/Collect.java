package cc.topicexplorer.plugin.text.actions.search;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

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
		
		String searchString = searchAction.getSearchWord();
		String searchStringParts[] = searchString.split(" ");
		
		String searchStrict = context.getString("SEARCH_STRICT");
		
		if(searchStrict.equals("true")) {
			searchString = "+" + StringUtils.join(searchStringParts, " +");
		}
		
		if (properties.getProperty("plugins").contains("fulltext")) {
			searchAction.addSearchColumn("(LENGTH(DOCUMENT.FULLTEXT$FULLTEXT) + 2 "
					+ "- LENGTH(REPLACE(CONCAT(' ', DOCUMENT.FULLTEXT$FULLTEXT, ' '), ' " + searchStringParts[0] + " ', ''))) / " 
					+ (searchStringParts[0].length() + 2), "COUNT0");
			for(int i = 1; i < searchStringParts.length; i++) {
				searchAction.addSearchColumn("(LENGTH(DOCUMENT.FULLTEXT$FULLTEXT) + 2 "
						+ "- LENGTH(REPLACE(CONCAT(' ', DOCUMENT.FULLTEXT$FULLTEXT, ' '), ' " + searchStringParts[i] + " ', ''))) / " 
						+ (searchStringParts[i].length() + 2), "COUNT" + i);				
			}
			searchAction.addWhereClause("MATCH(DOCUMENT.FULLTEXT$FULLTEXT) AGAINST ('" + searchString
					+ "' IN BOOLEAN MODE)");
		} else {
			searchAction.addSearchColumn("(LENGTH(DOCUMENT.TEXT$FULLTEXT) "
					+ "- LENGTH(REPLACE(DOCUMENT.TEXT$FULLTEXT, ' " + searchStringParts[0] + " ', ''))) / " 
					+ (searchStringParts[0].length() + 2), "COUNT0");
			for(int i = 0; i < searchStringParts.length; i++) {
				searchAction.addSearchColumn("(LENGTH(DOCUMENT.TEXT$FULLTEXT) "
						+ "- LENGTH(REPLACE(DOCUMENT.TEXT$FULLTEXT, ' " + searchStringParts[i] + " ', ''))) / " 
						+ (searchStringParts[i].length() + 2), "COUNT" + i);
			}
			searchAction.addWhereClause("MATCH(DOCUMENT.TEXT$FULLTEXT) AGAINST ('" + searchString
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
