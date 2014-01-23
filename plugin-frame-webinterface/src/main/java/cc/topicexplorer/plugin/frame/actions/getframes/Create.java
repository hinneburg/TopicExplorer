package cc.topicexplorer.plugin.frame.actions.getframes;

import java.io.PrintWriter;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		PrintWriter pw = (PrintWriter) communicationContext.get("SERVLET_WRITER");

		communicationContext.put("FRAME_ACTION", new Frames(this.database, pw, this.logger));
	}

	@Override
	public void addDependencies() {
		afterDependencies.add("FrameGenerateSQL");
	}

}