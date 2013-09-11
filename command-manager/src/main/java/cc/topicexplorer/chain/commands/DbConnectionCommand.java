package cc.topicexplorer.chain.commands;

import java.util.ArrayList;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import cc.topicexplorer.chain.DatabaseContext;
import cc.topicexplorer.chain.DependencyContext;
import cc.topicexplorer.database.Database;

public class DbConnectionCommand extends DependencyCommand {

	private Logger logger = Logger.getRootLogger();
	
	@Override
	public void specialExecute(Context context) throws Exception {

		logger.info("Current Command : [ " + getClass() + " ]");

		DatabaseContext databaseContext = (DatabaseContext) context;
		Database database = new Database(databaseContext.getProperties(), false);
		databaseContext.setDatabase(database);
	}
	
	@Override
	public void addDependencies() {
		beforeDependencies.add("Properties");
	}
}
