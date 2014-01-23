package cc.topicexplorer.plugin.duplicates.preprocessing.command;

import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.DependencyCommand;
import cc.topicexplorer.database.Database;
import cc.topicexplorer.plugin.duplicates.preprocessing.implementation.Duplicates;

public class Duplicates_Command extends DependencyCommand {
	private Properties properties;
	protected cc.topicexplorer.database.Database database;

	private final Duplicates duplicates = new Duplicates();

	@Override
	public void specialExecute(Context context) {
		logger.info("[ " + getClass() + " ] - " + "detecting duplicates");

		CommunicationContext communicationContext = (CommunicationContext) context;
		properties = (Properties) communicationContext.get("properties");
		database = (Database) communicationContext.get("database");

		duplicates.setLogger(logger);
		duplicates.setDB(database);

		String inFilePath = properties.getProperty("InCSVFile");
		duplicates.setCsvFilePath(inFilePath);

		int frameSize = Integer.parseInt(properties.getProperty("Duplicates_frameSize"));
		duplicates.setFrameSize(frameSize);

		duplicates.findDuplicates();
		try {
			duplicates.writeDuplicatesToDB();
		} catch (SQLException sqlEx) {
			logger.error("Could not write duplicates to db.");
			throw new RuntimeException(sqlEx);
		}
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentTermTopicCreate");
		afterDependencies.add("InFilePreparation");
		optionalAfterDependencies.add("Prune");
	}

}
