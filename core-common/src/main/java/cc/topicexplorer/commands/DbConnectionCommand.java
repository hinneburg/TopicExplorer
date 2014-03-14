package cc.topicexplorer.commands;

import java.util.Properties;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.commandmanager.core.DependencyCommand;
import cc.topicexplorer.database.Database;

public class DbConnectionCommand extends DependencyCommand {

	@Override
	public void specialExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		Database database = new Database((Properties) communicationContext.get("properties"), false);
		communicationContext.put("database", database);
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("PropertiesCommand");
	}
}
