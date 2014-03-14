package cc.topicexplorer.plugin.frame.actions.getframes;

import java.sql.SQLException;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;

public class GenerateSQL extends TableSelectCommand {
	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		Frames frameAction = (Frames) communicationContext.get("FRAME_ACTION");

		try {
			frameAction.getFrames();
		} catch (SQLException sqlEx) {
			logger.error("A problem occured while executing the query.");
			throw new RuntimeException(sqlEx);
		}
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("FrameCreate");
	}
}
