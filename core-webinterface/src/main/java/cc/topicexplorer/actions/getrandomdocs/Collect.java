package cc.topicexplorer.actions.getrandomdocs;

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
		preQueryMap.select.add("COUNT(*) AS COUNT");
		preQueryMap.from.add("DOCUMENT");
		context.rebind("PRE_QUERY", preQueryMap);

		innerQueryMap = context.get("INNER_QUERY", SelectMap.class);
		innerQueryMap.select.add("DOCUMENT.DOCUMENT_ID");
		innerQueryMap.from.add("DOCUMENT");
		innerQueryMap.limit = 20;
		context.rebind("INNER_QUERY", innerQueryMap);

		mainQueryMap = context.get("MAIN_QUERY", SelectMap.class);
		mainQueryMap.select.add("*");
		mainQueryMap.from.add("DOCUMENT_TOPIC y");
		mainQueryMap.where.add("x.DOCUMENT_ID=y.DOCUMENT_ID");
		mainQueryMap.orderBy.add("x.DOCUMENT_ID");
		mainQueryMap.orderBy.add("y.TOPIC_ID");
		context.rebind("MAIN_QUERY", mainQueryMap);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("GetRandomDocsCoreGenerateSQL");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("GetRandomDocsCoreCreate");
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
