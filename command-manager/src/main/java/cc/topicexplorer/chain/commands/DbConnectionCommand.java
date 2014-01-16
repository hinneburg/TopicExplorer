package cc.topicexplorer.chain.commands;

import java.util.Properties;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.database.Database;

public class DbConnectionCommand extends DependencyCommand {

	private final Logger logger = Logger.getRootLogger();

	@Override
	public void specialExecute(Context context) {

		logger.info("Current Command : [ " + getClass() + " ]");

		CommunicationContext communicationContext = (CommunicationContext) context;
		Database database = new Database((Properties) communicationContext.get("properties"), false);
		communicationContext.put("database", database);
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("Properties");
	}
}
