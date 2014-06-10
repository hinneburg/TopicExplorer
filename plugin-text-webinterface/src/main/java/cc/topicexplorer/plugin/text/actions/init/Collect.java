package cc.topicexplorer.plugin.text.actions.init;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		SelectMap documentMap, topicMap;

		documentMap = context.get("DOCUMENT_QUERY", SelectMap.class);
		documentMap.select.add("DOCUMENT.TEXT$TITLE");
		documentMap.select.add("CONCAT(SUBSTRING(DOCUMENT.TEXT$FULLTEXT FROM 1 FOR 150), '...') AS TEXT$SNIPPET");
		context.rebind("DOCUMENT_QUERY", documentMap);

		topicMap = context.get("TOPIC_QUERY", SelectMap.class);
		topicMap.select.add("TOPIC.TEXT$TOPIC_LABEL");
		context.rebind("TOPIC_QUERY", topicMap);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("InitCoreGenerateSQL");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("InitCoreCreate");
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
