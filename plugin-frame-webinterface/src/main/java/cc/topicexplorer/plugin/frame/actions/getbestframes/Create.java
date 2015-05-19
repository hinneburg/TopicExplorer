package cc.topicexplorer.plugin.frame.actions.getbestframes;

import java.io.PrintWriter;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Create extends TableSelectCommand {

	private static final Logger logger = Logger.getLogger(Create.class);

	@Override
	public void tableExecute(Context context) {
		PrintWriter pw = context.get("SERVLET_WRITER", PrintWriter.class);
		context.bind("cc.topicexplorer.plugin.frame.actions.getbestframes.BestFrames", new BestFrames(this.database, pw, logger));
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("BestFrameGenerateSQL");
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
