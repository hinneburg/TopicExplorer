package cc.topicexplorer.actions.gettopics;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		SelectMap preQueryMap, innerQueryMap, mainQueryMap;

		preQueryMap = context.get("PRE_QUERY", SelectMap.class);
		preQueryMap.select.add("TOPIC_ID");
		preQueryMap.from.add("TOPIC");
		context.rebind("PRE_QUERY", preQueryMap);

		innerQueryMap = context.get("INNER_QUERY", SelectMap.class);
		innerQueryMap.select.add("COUNT(*) AS words");
		innerQueryMap.select.add("SUM(PR_TERM_GIVEN_TOPIC) AS maxRelevanz");
		innerQueryMap.select.add("TOPIC_ID");
		innerQueryMap.from.add("TERM_TOPIC");
		context.rebind("INNER_QUERY", innerQueryMap);

		mainQueryMap = context.get("MAIN_QUERY", SelectMap.class);
		mainQueryMap.select.add("(PR_TERM_GIVEN_TOPIC/maxRelevanz) AS relevanz");
		mainQueryMap.select.add("TERM.TERM_NAME");
		mainQueryMap.from.add("TERM_TOPIC");
		mainQueryMap.from.add("TERM");
		mainQueryMap.from.add("TOPIC");
		mainQueryMap.where.add("CountRelevanz.TOPIC_ID = TERM_TOPIC.TOPIC_ID");
		mainQueryMap.where.add("TERM_TOPIC.TERM_ID = TERM.TERM_ID");
		mainQueryMap.where.add("TERM_TOPIC.TOPIC_ID = TOPIC.TOPIC_ID");
		mainQueryMap.orderBy.add("relevanz DESC");
		mainQueryMap.limit = 40;
		context.rebind("MAIN_QUERY", mainQueryMap);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("GetTopicsCoreGenerateSQL");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("GetTopicsCoreCreate");
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
