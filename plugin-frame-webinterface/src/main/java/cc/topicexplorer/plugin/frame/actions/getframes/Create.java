package cc.topicexplorer.plugin.frame.actions.getframes;

import java.io.PrintWriter;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.utils.MySQLEncoder;

import com.google.common.collect.Sets;

public class Create extends TableSelectCommand {

	private static final Logger logger = Logger.getLogger(Create.class);

	@Override
	public void tableExecute(Context context) {
		MySQLEncoder me = new MySQLEncoder();
		
		PrintWriter pw = context.get("SERVLET_WRITER", PrintWriter.class);
		int topicId = context.getInteger("TOPIC_ID");
		String frameType = me.encode(context.getString("FRAME_TYPE"));
		int offset = context.getInteger("OFFSET");
		int limit = Integer.parseInt((String) properties.get("TopicBestItemLimit"));

		context.bind("FRAME_ACTION", new Frames(this.database, pw, logger, topicId, frameType, offset, limit));
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
