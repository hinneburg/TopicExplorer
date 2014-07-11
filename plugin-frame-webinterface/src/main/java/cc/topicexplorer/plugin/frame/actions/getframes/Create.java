package cc.topicexplorer.plugin.frame.actions.getframes;

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
		String topicId = context.getString("TOPIC_ID");
		String frameType = context.getString("FRAME_TYPE");
		int offset = context.getInteger("OFFSET");

		context.bind("FRAME_ACTION", new Frames(this.database, pw, logger, topicId, frameType, offset));
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("FrameGenerateSQL");
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
