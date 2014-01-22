package cc.topicexplorer.plugin.prune.preprocessing.tools;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.DependencyCommand;
import cc.topicexplorer.database.Database;

public class Prune_Command extends DependencyCommand {
	private Properties properties;
	protected cc.topicexplorer.database.Database database;
	private final Prune_Ram_SortedCsv prune = new Prune_Ram_SortedCsv();

	@Override
	public void specialExecute(Context context) {
		logger.info("[ " + getClass() + " ] - " + "pruning vocabular");

		CommunicationContext communicationContext = (CommunicationContext) context;
		properties = (Properties) communicationContext.get("properties");
		database = (Database) communicationContext.get("database");

		float upperBoundPercent = Float.parseFloat(properties.getProperty("Prune_upperBound"));
		float lowerBoundPercent = Float.parseFloat(properties.getProperty("Prune_lowerBound"));
		prune.setLowerAndUpperBoundPercent(lowerBoundPercent, upperBoundPercent);

		prune.setLogger(logger);
		String inFilePath = properties.getProperty("InCSVFile");
		prune.setInFilePath(inFilePath);

		try {
			prune.prune();
		} catch (IOException e) {
			logger.error("During prune a file stream problem occured.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentTermTopicCreate");
		afterDependencies.add("InFilePreparation");
	}
}
