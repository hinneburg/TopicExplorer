package cc.topicexplorer.chain.commands;

import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.jooq.impl.Factory;

import cc.topicexplorer.chain.DatabaseContext;
import cc.topicexplorer.chain.DependencyContext;
import cc.topicexplorer.database.Database;

//import org.apache.log4j.*;

public abstract class TableCommand extends DependencyCommand {
	// private static Logger logger = Logger.getRootLogger();

	protected Properties properties;
	protected cc.topicexplorer.database.Database database;
	protected Factory create;

	protected String tableName;
	protected String dependencies = "";

	public void specialExecute(Context context) throws Exception {
		long startTime = System.currentTimeMillis();

		DatabaseContext databaseContext = (DatabaseContext) context;

		properties = databaseContext.getProperties();
		database = databaseContext.getDatabase();
		create = databaseContext.getCreate();
		setTableName();
		
		tableExecute(context);
		
		logger.info(System.currentTimeMillis() - startTime + " ms");
	}
	
	public abstract void tableExecute(Context context);

	public abstract void setTableName();
}
