package cc.topicexplorer.actions.getterms;

import java.io.PrintWriter;
import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		PrintWriter pw = context.get("SERVLET_WRITER", PrintWriter.class);
		AllTerms allTermsAction = new AllTerms(database, pw);
		context.bind("ALL_TERMS_ACTION", allTermsAction);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("AllTermsCoreGenerateSQL");
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
