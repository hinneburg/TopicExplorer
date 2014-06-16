package cc.topicexplorer.plugin.time.actions.bestdocs;

import java.util.ArrayList;
import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.actions.bestdocs.BestDocumentsForGivenTopic;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		BestDocumentsForGivenTopic bestDocAction = context.get("BEST_DOC_ACTION", BestDocumentsForGivenTopic.class);

		bestDocAction.addDocumentColumn("DOCUMENT.TIME$TIME_STAMP", "TIME$TIME_STAMP");
		if(context.containsKey("week")) {
			int startTstamp = Integer.valueOf(context.getString("week"));
			int endTstamp = startTstamp + 604800;
	
			bestDocAction.addWhereClause("DOCUMENT.TIME$TIME_STAMP > " + startTstamp);
			bestDocAction.addWhereClause("DOCUMENT.TIME$TIME_STAMP < " + endTstamp);
		}
		if(context.containsKey("sorting")) {
			String sorting = context.getString("sorting");
			if (sorting.equals("TIME")) {
				ArrayList<String> orderBy = new ArrayList<String>();
				orderBy.add("DOCUMENT.TIME$TIME_STAMP");
				bestDocAction.setOrderBy(orderBy);
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
