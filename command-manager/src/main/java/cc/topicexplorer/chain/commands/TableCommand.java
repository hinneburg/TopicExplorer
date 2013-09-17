package cc.topicexplorer.chain.commands;

import java.util.Properties;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.database.Database;

//import org.apache.log4j.*;

public abstract class TableCommand extends DependencyCommand {
	// private static Logger logger = Logger.getRootLogger();

	protected Properties properties;
	protected cc.topicexplorer.database.Database database;

	protected String tableName;
	protected String dependencies = "";

	public void specialExecute(Context context) throws Exception {
		long startTime = System.currentTimeMillis();

		CommunicationContext communicationContext = (CommunicationContext) context;
		properties = (Properties) communicationContext.get("properties");
		database = (Database) communicationContext.get("database");
		
		setTableName();
		
		tableExecute(context);
		
		logger.info(System.currentTimeMillis() - startTime + " ms");
	}
	
	public abstract void tableExecute(Context context);

	public abstract void setTableName();
}
