package cc.topicexplorer.plugin.frame.actions.getframes;

import java.io.PrintWriter;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		PrintWriter pw = (PrintWriter) communicationContext.get("SERVLET_WRITER");
		String topicId = (String) communicationContext.get("TOPIC_ID");
		int offset = (Integer) communicationContext.get("OFFSET");
		

		communicationContext.put("FRAME_ACTION", new Frames(this.database, pw, this.logger, topicId, offset));
	}

	@Override
	public void addDependencies() {
		afterDependencies.add("FrameGenerateSQL");
	}

}