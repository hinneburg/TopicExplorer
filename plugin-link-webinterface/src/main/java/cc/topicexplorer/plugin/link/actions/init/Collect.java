package cc.topicexplorer.plugin.link.actions.init;


import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		SelectMap documentMap;

		documentMap =  context.get("DOCUMENT_QUERY", SelectMap.class);

		documentMap.select.add("DOCUMENT.LINK$URL");
		
		context.rebind("DOCUMENT_QUERY", documentMap);
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
		return Collections.emptySet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Collections.emptySet();
	}
}
