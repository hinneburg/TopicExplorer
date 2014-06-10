package cc.topicexplorer.plugin.time.actions.getdates;

import java.io.PrintWriter;
import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		PrintWriter pw = context.get("SERVLET_WRITER", PrintWriter.class);
		context.bind("GETDATES_ACTION", new GetDates(database, pw));
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("GetDatesTimeGenerateSQL");
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
