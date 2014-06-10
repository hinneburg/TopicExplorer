package cc.topicexplorer.plugin.text.actions.showdoc;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		SelectMap documentMap = context.get("DOCUMENT_QUERY", SelectMap.class);

		documentMap.select.add("DOCUMENT.TEXT$TITLE");
		documentMap.select.add("DOCUMENT.TEXT$FULLTEXT");

		context.rebind("DOCUMENT_QUERY", documentMap);
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
