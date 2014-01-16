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

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.DependencyCommand;
import cc.topicexplorer.database.Database;

public class Prune_pb extends DependencyCommand {
	private Properties properties;
	private float lowerBound;
	private float upperBound;
	protected cc.topicexplorer.database.Database database;

	private void processPrune() {
		// TODO Auto-generated method stub
		ProcessBuilder p = new ProcessBuilder("bash", "-c", "scripts/prune.sh " + properties.getProperty("InCSVFile")
				+ " " + this.lowerBound + " " + this.upperBound);

		Process process = null;

		try {
			process = p.start();
		} catch (IOException e) {
			e.printStackTrace();
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
			e.printStackTrace();
			logger.fatal("Pruning execution failed");
			System.exit(0);
		}
	}

	private void renameFile(String source, String destination) {
		File sourceFile = new File(source);
		File destinationFile = new File(destination);

		if (!sourceFile.renameTo(destinationFile)) {
			logger.fatal("[ " + getClass() + " ] - " + "Fehler beim Umbenennen der Datei: " + source);
			System.exit(0);
		}
	}

	@Override
	public void specialExecute(Context context) throws NumberFormatException, SQLException {
		logger.info("[ " + getClass() + " ] - " + "pruning vocabular");

		CommunicationContext communicationContext = (CommunicationContext) context;
		properties = (Properties) communicationContext.get("properties");
		database = (Database) communicationContext.get("database");

		float upperBoundPercent = Float.parseFloat(properties.getProperty("prune_upperBound"));
		float lowerBoundPercent = Float.parseFloat(properties.getProperty("prune_lowerBound"));

		// are the bounds valid?
		if (upperBoundPercent < 0 || lowerBoundPercent < 0 || upperBoundPercent > 100 || lowerBoundPercent > 100
				|| upperBoundPercent < lowerBoundPercent) {
			logger.fatal("Stop Puning: Invalid Pruning Bounds!");
			System.exit(0);
		}

		ResultSet rsDocCount = database.executeQuery("SELECT COUNT(*) FROM " + properties.getProperty("OrgTableName"));
		if (rsDocCount.next()) {
			int count = rsDocCount.getInt(1);
			this.upperBound = (float) (count / 100.0) * upperBoundPercent;
			this.lowerBound = (float) (count / 100.0) * lowerBoundPercent;
		} else {
			lowerBound = 0.0f;
			upperBound = Float.MAX_VALUE;
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
