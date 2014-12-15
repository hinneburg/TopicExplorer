package cc.topicexplorer.actions.showdoc;

import java.io.PrintWriter;

import java.util.Set;

import cc.commandmanager.core.Context;

import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		int docId = context.getInteger("SHOW_DOC_ID");
		PrintWriter pw = context.get("SERVLET_WRITER", PrintWriter.class);

		ShowDoc showDocAction = new ShowDoc(docId, database, pw, properties);
		context.bind("SHOW_DOC_ACTION", showDocAction);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("ShowDocCoreGenerateSQL");
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
