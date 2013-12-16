package cc.topicexplorer.plugin.frame.actions.getframes;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;

public class GenerateSQL extends TableSelectCommand {
	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		Frames frameAction = (Frames) communicationContext
				.get("FRAME_ACTION");
		frameAction.getFrames();
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("FrameCreate");
	}
}
