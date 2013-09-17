package cc.topicexplorer.plugin.prune.preprocessing.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.Properties;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.DatabaseContext;

public class Prune_pb implements Command {
	private String root;
	private Properties properties;
	private float lowerBound;
	private float upperBound;
	protected cc.topicexplorer.database.Database database;

	private void processPrune() {
		// TODO Auto-generated method stub
		ProcessBuilder p = new ProcessBuilder("bash", "-c", root
				+ "skripts/prune.sh " + root
				+ properties.getProperty("InCSVFile") + " " + this.lowerBound
				+ " " + this.upperBound);

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
				System.out.println("processing prune " + line);
			}
			br.close();
			process.destroy();
			System.out.println("Pruning successfully executed");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Pruning execution failed");
		}
	}

	private void renameFile(String source, String destination) {
		File sourceFile = new File(source);
		File destinationFile = new File(destination);

		if (!sourceFile.renameTo(destinationFile)) {
			System.out.println("[ " + getClass() + " ] - "
					+ "Fehler beim Umbenennen der Datei: " + source);
		}
	}

	@Override
	public boolean execute(Context context) throws Exception {
		System.out.println("[ " + getClass() + " ] - " + "pruning vocabular");
		if (DatabaseContext.class.isInstance(context)) {
			DatabaseContext databaseContext = (DatabaseContext) context;
	
			properties = databaseContext.getProperties();
			database = databaseContext.getDatabase();
		
			root = properties.getProperty("projectRoot");
	
			float upperBoundPercent = Float.parseFloat(properties
					.getProperty("prune_upperBound"));
			float lowerBoundPercent = Float.parseFloat(properties
					.getProperty("prune_lowerBound"));
	
			// are the bounds valid?
			if (upperBoundPercent < 0 || lowerBoundPercent < 0
					|| upperBoundPercent > 100 || lowerBoundPercent > 100
					|| upperBoundPercent < lowerBoundPercent) {
				System.out.println("Stop: Invalid Pruning Bounds!");
				System.exit(0);
			}
	
	
			ResultSet rsDocCount = database.executeQuery("SELECT COUNT(*) FROM "
					+ properties.getProperty("OrgTableName"));
			if (rsDocCount.next()) {
				int count = rsDocCount.getInt(1);
				this.upperBound = (float) (count / 100.0) * upperBoundPercent;
				this.lowerBound = (float) (count / 100.0) * lowerBoundPercent;
			}else {
				lowerBound = 0.0f;
				upperBound = Float.MAX_VALUE;
			}
	
			
			this.processPrune();
	
			this.renameFile(this.root + properties.getProperty("InCSVFile"),
					this.root + properties.getProperty("InCSVFile") + ".org."
							+ System.currentTimeMillis());
			
			this.renameFile(this.root + properties.getProperty("InCSVFile")
					+ ".pruned.Lower." + this.lowerBound + ".Upper."
					+ this.upperBound + ".csv", this.root
					+ properties.getProperty("InCSVFile"));
		} else {

		}
		return false;
	}

}
