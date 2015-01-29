package cc.topicexplorer.actions.bestdocs;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.utils.MySQLEncoder;

import com.google.common.collect.Sets;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {

		int topicId = context.getInteger("TOPIC_ID");
		int offset = context.getInteger("OFFSET");
		PrintWriter pw = context.get("SERVLET_WRITER", PrintWriter.class);
		int limit = Integer.parseInt(properties.getProperty("DocBrowserLimit"));
		BestDocumentsForGivenTopic bestDocAction;
		boolean sortingByRelevance = true; 
		if(context.containsKey("sorting")) {
			String sorting = context.getString("sorting");
			if (!sorting.equals("RELEVANCE")) {
				sortingByRelevance = false;
			}
		}
		
		if(context.containsKey("term")) {
			MySQLEncoder me = new MySQLEncoder();
			String term = me.encode(context.getString("term"));
			bestDocAction = new BestDocumentsForGivenTopic(topicId, limit, offset, database, pw, term, Arrays.asList(properties.get("plugins").toString().split(",")).contains("hierarchicaltopic"), sortingByRelevance);
		} else {
			bestDocAction = new BestDocumentsForGivenTopic(topicId, limit, offset, database, pw, sortingByRelevance);
		}
		context.bind("BEST_DOC_ACTION", bestDocAction);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("BestDocsCoreGenerateSQL");
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
