package cc.topicexplorer.plugin.prune.preprocessing.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.topicexplorer.database.Database;

import com.google.common.collect.Sets;

public class Prune_pb implements Command {

	private static final Logger logger = Logger.getLogger(Prune_pb.class);

	private Properties properties;
	private float lowerBound;
	private float upperBound;
	private Database database;

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
	public void execute(Context context) {
		logger.info("[ " + getClass() + " ] - " + "pruning vocabular");

		properties = context.get("properties", Properties.class);
		database = context.get("database", Database.class);

		float upperBoundPercent = Float.parseFloat(properties.getProperty("prune_upperBound"));
		float lowerBoundPercent = Float.parseFloat(properties.getProperty("prune_lowerBound"));

		if (!hasValidBounds(upperBoundPercent, lowerBoundPercent)) {
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

	private boolean hasValidBounds(float upperBoundPercent, float lowerBoundPercent) {
		return !(upperBoundPercent < 0 || lowerBoundPercent < 0 || upperBoundPercent > 100 || lowerBoundPercent > 100 || upperBoundPercent < lowerBoundPercent);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("InFilePreparation");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentTermTopicCreate");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}

}
