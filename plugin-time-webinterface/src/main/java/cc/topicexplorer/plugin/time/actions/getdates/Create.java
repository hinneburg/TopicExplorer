package cc.topicexplorer.plugin.time.actions.getdates;

import java.io.PrintWriter;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;

		PrintWriter pw = (PrintWriter) communicationContext.get("SERVLET_WRITER");
		
		GetDates getDateAction = new GetDates(database, pw);
		communicationContext.put("GETDATES_ACTION", getDateAction);
	}

	@Override
	public void addDependencies() {
		afterDependencies.add("GetDatesTimeGenerateSQL");
	}

}