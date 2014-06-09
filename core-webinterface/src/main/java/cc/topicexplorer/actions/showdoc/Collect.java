package cc.topicexplorer.actions.showdoc;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		SelectMap documentMap = context.get("DOCUMENT_QUERY", SelectMap.class);
		String docId = context.getString("SHOW_DOC_ID");
		documentMap.select.add("DOCUMENT_ID");
		documentMap.from.add("DOCUMENT");
		documentMap.where.add("DOCUMENT_ID=" + docId);
		context.rebind("DOCUMENT_QUERY", documentMap);

		SelectMap topicMap = context.get("TOPIC_QUERY", SelectMap.class);
		topicMap.select.add("TOPIC_ID");
		topicMap.select.add("POSITION_OF_TOKEN_IN_DOCUMENT");
		topicMap.select.add("TOKEN");
		topicMap.from.add("DOCUMENT_TERM_TOPIC");
		topicMap.where.add("DOCUMENT_ID=" + docId);
		topicMap.orderBy.add("POSITION_OF_TOKEN_IN_DOCUMENT DESC");
		context.rebind("TOPIC_QUERY", topicMap);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("ShowDocCoreGenerateSQL");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("ShowDocCoreCreate");
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
