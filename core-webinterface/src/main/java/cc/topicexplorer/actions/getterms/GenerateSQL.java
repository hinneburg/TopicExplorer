package cc.topicexplorer.actions.getterms;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class GenerateSQL extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		AllTerms allTermsAction = context.get("ALL_TERMS_ACTION", AllTerms.class);
		allTermsAction.readAllTermsAndGenerateJson();
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("AllTermsCoreCreate");
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
