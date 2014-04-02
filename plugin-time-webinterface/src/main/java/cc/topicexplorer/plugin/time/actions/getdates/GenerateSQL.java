package cc.topicexplorer.plugin.time.actions.getdates;

import java.sql.SQLException;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;

public class GenerateSQL extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		GetDates getDatesAction = (GetDates) communicationContext.get("GETDATES_ACTION");

		try {
			logger.info("QUERY will be executed: " + getDatesAction.getQueryForExecute());
			getDatesAction.executeQuery();
		} catch (SQLException sqlEx) {
			logger.error("A problem occured while executing the query.");
			throw new RuntimeException(sqlEx);
		}
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("GetDatesTimeCreate");
	}
}
