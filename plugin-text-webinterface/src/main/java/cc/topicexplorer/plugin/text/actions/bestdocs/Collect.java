package cc.topicexplorer.plugin.text.actions.bestdocs;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import cc.commandmanager.core.Context;
import cc.topicexplorer.actions.bestdocs.BestDocumentsForGivenTopic;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		BestDocumentsForGivenTopic bestDocAction = context.get("BEST_DOC_ACTION", BestDocumentsForGivenTopic.class);

		bestDocAction.addDocumentColumn("DOCUMENT.TEXT$TITLE", "TEXT$TITLE", context.containsKey("term"));
		bestDocAction.addDocumentColumn("CONCAT(SUBSTRING(DOCUMENT.TEXT$FULLTEXT FROM 1 FOR 150), '...')",
				"TEXT$SNIPPET", context.containsKey("term"));

		if(context.containsKey("filter")) {
			JSONObject filter;
			
			String searchColumn = "";
			if (properties.getProperty("plugins").contains("fulltext")) {
				searchColumn = "DOCUMENT.FULLTEXT$FULLTEXT";
			}else {
				searchColumn = "DOCUMENT.TEXT$FULLTEXT";
			}
			
			try {
				filter = new JSONObject(context.getString("filter"));
				if(filter.has("word")) {
					String word = filter.getString("word");
					if(!word.isEmpty()) {
						bestDocAction.addWhereClause("MATCH(" + searchColumn + ") AGAINST ('" + word	+ "' IN BOOLEAN MODE)");
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		context.rebind("BEST_DOC_ACTION", bestDocAction);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("BestDocsCoreGenerateSQL");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("BestDocsCoreCreate");
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
