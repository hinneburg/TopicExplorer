package cc.topicexplorer.plugin.frame.actions.getbestframes;

import java.io.PrintWriter;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		PrintWriter pw = (PrintWriter) communicationContext.get("SERVLET_WRITER");

		communicationContext.put("FRAME_ACTION", new BestFrames(this.database, pw, this.logger));
	}

	@Override
	public void addDependencies() {
		afterDependencies.add("BestFrameGenerateSQL");
	}

}