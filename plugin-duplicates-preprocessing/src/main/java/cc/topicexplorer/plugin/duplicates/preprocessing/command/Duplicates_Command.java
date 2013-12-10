package cc.topicexplorer.plugin.duplicates.preprocessing.command;

import java.util.Properties;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.DependencyCommand;
import cc.topicexplorer.database.Database;
import cc.topicexplorer.plugin.duplicates.preprocessing.implementation.Duplicates;

public class Duplicates_Command extends DependencyCommand {
	private Properties properties;
	protected cc.topicexplorer.database.Database database;
	
	private Duplicates duplicates = new Duplicates();
	

	@Override
	public void specialExecute(Context context) throws Exception {
		// TODO Auto-generated method stub
		logger.info("[ " + getClass() + " ] - " + "detecting duplicates");

		CommunicationContext communicationContext = (CommunicationContext) context;
		properties = (Properties) communicationContext.get("properties");
		database = (Database) communicationContext.get("database");

		duplicates.setLogger(logger);
		duplicates.setDB( database );

		String inFilePath = properties.getProperty("InCSVFile");
		duplicates.setCsvFilePath(inFilePath);
		
		int frameSize = Integer.parseInt(properties.getProperty("Duplicates_frameSize"));
		duplicates.setFrameSize(frameSize);
		
		duplicates.findDuplicates();
		duplicates.writeDuplicatesToDB();
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentTermTopicCreate");
		afterDependencies.add("InFilePreparation");
		optionalAfterDependencies.add("Prune");
	}

}
