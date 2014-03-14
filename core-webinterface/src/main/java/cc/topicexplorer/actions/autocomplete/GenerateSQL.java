package cc.topicexplorer.actions.autocomplete;

import java.sql.SQLException;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;

public class GenerateSQL extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		Autocomplete autocompleteAction = (Autocomplete) communicationContext.get("AUTOCOMPLETE_ACTION");

		try {
			logger.info("QUERY will be executed: " + autocompleteAction.getQueryForExecute());
			autocompleteAction.executeQuery();
		} catch (SQLException sqlEx) {
			logger.error("A problem occured while executing the query.");
			throw new RuntimeException(sqlEx);
		}
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("AutocompleteCoreCreate");
	}
}