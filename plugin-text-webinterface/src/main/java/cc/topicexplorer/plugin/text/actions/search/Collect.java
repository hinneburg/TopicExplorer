package cc.topicexplorer.plugin.text.actions.search;

import java.util.ArrayList;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
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
		String searchStringParts[] = searchString.split("[\\u0009-\\u000D\\u0020\\u0085\\u00A0\\u1680\\u180E\\u2000-\\u200A\\u2028\\u2029\\u202F\\u205F\\u3000]");
		String searchStrict = context.getString("SEARCH_STRICT");
		
		if(searchStrict.equals("true")) {
			searchString = "+" + StringUtils.join(searchStringParts, " +");
		}
		String searchColumn = "";
		if (properties.getProperty("plugins").contains("fulltext")) {
			searchColumn = "DOCUMENT.FULLTEXT$FULLTEXT";
		}else {
			searchColumn = "DOCUMENT.TEXT$FULLTEXT";
		}
		searchAction.addSearchColumn("(LENGTH(" + searchColumn + ") + 2 "
				+ "- LENGTH(REPLACE(CONCAT(' ', " + searchColumn + ", ' '), ' " + searchStringParts[0] + " ', ''))) / " 
				+ (searchStringParts[0].length() + 2), "COUNT0");
		for(int i = 1; i < searchStringParts.length; i++) {
			searchAction.addSearchColumn("(LENGTH(" + searchColumn + ") + 2 "
					+ "- LENGTH(REPLACE(CONCAT(' ', " + searchColumn + ", ' '), ' " + searchStringParts[i] + " ', ''))) / " 
					+ (searchStringParts[i].length() + 2), "COUNT" + i);				
		}
		searchAction.addWhereClause("MATCH(" + searchColumn + ") AGAINST ('" + searchString
				+ "' IN BOOLEAN MODE)");
		
		if(context.containsKey("sorting")) {
			String sorting = context.getString("sorting");
			if (sorting.equals("RELEVANCE")) {
				searchAction.addSearchColumn("MATCH(" + searchColumn + ") AGAINST ('" + searchString + "')", 
						"RELEVANCE");
				ArrayList<String> orderBy = searchAction.getOrderBy();
				orderBy.add("RELEVANCE DESC");
				searchAction.setOrderBy(orderBy);
			}
		} else {
			searchAction.addSearchColumn("MATCH(" + searchColumn + ") AGAINST ('" + searchString + "')", 
					"RELEVANCE");
			ArrayList<String> orderBy = new ArrayList<String>();
			orderBy.add("RELEVANCE DESC");
			searchAction.setOrderBy(orderBy);
		}
		if(context.containsKey("filter")) {
			JSONObject filter;
			try {
				filter = new JSONObject(context.getString("filter"));
				if(filter.has("word")) {
					String word = filter.getString("word");
					if(!word.isEmpty()) {
						searchAction.addWhereClause("MATCH(" + searchColumn + ") AGAINST ('" + word	+ "' IN BOOLEAN MODE)");
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
