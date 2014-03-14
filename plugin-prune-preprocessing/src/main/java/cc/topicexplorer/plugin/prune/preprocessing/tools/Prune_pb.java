package cc.topicexplorer.plugin.prune.preprocessing.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.commandmanager.core.DependencyCommand;
import cc.topicexplorer.database.Database;

public class Prune_pb extends DependencyCommand {
	private Properties properties;
	private float lowerBound;
	private float upperBound;
	protected cc.topicexplorer.database.Database database;

	private void processPrune() {
		ProcessBuilder p = new ProcessBuilder("bash", "-c", "scripts/prune.sh " + properties.getProperty("InCSVFile")
				+ " " + this.lowerBound + " " + this.upperBound);

		Process process = null;

		try {
			process = p.start();
		} catch (IOException e) {
			logger.warn("The shell process caused a file stream problem.", e);
		}

		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;

		try {
			while ((line = br.readLine()) != null) {
				logger.info("processing prune " + line);
			}
			br.close();
			process.destroy();
			logger.info("Pruning successfully executed");
		} catch (IOException e) {
			logger.error("Pruning execution failed");
			throw new RuntimeException(e);
		}
	}

	private void renameFile(String source, String destination) {
		File sourceFile = new File(source);
		File destinationFile = new File(destination);

		if (!sourceFile.renameTo(destinationFile)) {
			logger.error("File could not be renamed: " + source);
			throw new IllegalStateException();
		}
	}

	@Override
	public void specialExecute(Context context) {
		logger.info("[ " + getClass() + " ] - " + "pruning vocabular");

		CommunicationContext communicationContext = (CommunicationContext) context;
		properties = (Properties) communicationContext.get("properties");
		database = (Database) communicationContext.get("database");

		float upperBoundPercent = Float.parseFloat(properties.getProperty("prune_upperBound"));
		float lowerBoundPercent = Float.parseFloat(properties.getProperty("prune_lowerBound"));

		// are the bounds valid?
		if (upperBoundPercent < 0 || lowerBoundPercent < 0 || upperBoundPercent > 100 || lowerBoundPercent > 100
				|| upperBoundPercent < lowerBoundPercent) {
			logger.error("Stop: Invalid Pruning Bounds!");
			throw new IllegalArgumentException(String.format("upperBoundPercent: %f, lowerBoundPercent: %f",
					upperBoundPercent, lowerBoundPercent));
		}

		String query = "SELECT COUNT(*) FROM " + properties.getProperty("OrgTableName");
		try {
			ResultSet rsDocCount = database.executeQuery(query);
			if (rsDocCount.next()) {
				int count = rsDocCount.getInt(1);
				this.upperBound = (float) (count / 100.0) * upperBoundPercent;
				this.lowerBound = (float) (count / 100.0) * lowerBoundPercent;
			} else {
				lowerBound = 0.0f;
				upperBound = Float.MAX_VALUE;
			}
		} catch (SQLException e) {
			logger.error("Error in Query: " + query);
			throw new RuntimeException(e);
		}

		this.processPrune();

		this.renameFile(properties.getProperty("InCSVFile"),
				properties.getProperty("InCSVFile") + ".org." + System.currentTimeMillis());

		this.renameFile(properties.getProperty("InCSVFile") + ".pruned.Lower." + this.lowerBound + ".Upper."
				+ this.upperBound + ".csv", properties.getProperty("InCSVFile"));
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentTermTopicCreate");
		afterDependencies.add("InFilePreparation");
	}

}
