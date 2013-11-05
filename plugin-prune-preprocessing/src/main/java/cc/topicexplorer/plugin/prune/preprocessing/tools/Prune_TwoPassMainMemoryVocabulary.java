package cc.topicexplorer.plugin.prune.preprocessing.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.chain.Context;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.DependencyCommand;
import cc.topicexplorer.database.Database;

public class Prune_TwoPassMainMemoryVocabulary extends DependencyCommand {
	private Properties properties;
	private static CsvReader inCsv;
	protected cc.topicexplorer.database.Database database;

	private void renameFile(String source, String destination) {
		File sourceFile = new File(source);
		File destinationFile = new File(destination);

		if (!sourceFile.renameTo(destinationFile)) {
			logger.fatal("[ " + getClass() + " ] - "
					+ "Fehler beim Umbenennen der Datei: " + source);
			System.exit(0);
		}
	}

	
	@Override
	public void specialExecute(Context context) throws Exception {
		// TODO Auto-generated method stub
		logger.info("[ " + getClass() + " ] - " + "pruning vocabular");

		CommunicationContext communicationContext = (CommunicationContext) context;
		properties = (Properties) communicationContext.get("properties");
		database = (Database) communicationContext.get("database");

		float upperBoundPercent = Float.parseFloat(properties
				.getProperty("Prune_upperBound"));
		float lowerBoundPercent = Float.parseFloat(properties
				.getProperty("Prune_lowerBound"));

		// are the bounds valid?
		if (upperBoundPercent < 0 || lowerBoundPercent < 0
				|| upperBoundPercent > 100 || lowerBoundPercent > 100
				|| upperBoundPercent < lowerBoundPercent) {
			logger.fatal("Stop: Invalid Pruning Bounds!");
			System.exit(0);
		}

		HashMap<String, Integer> vocabulary = new HashMap<String, Integer>();
		Integer numberOfDocuments = 0;
		String inFilePath = properties.getProperty("InCSVFile");
		try {
			inCsv = new CsvReader(new FileInputStream(inFilePath), ';',
					Charset.forName("UTF-8"));
			try {
				if (inCsv.readHeaders()) {
					String documentId = new String();

					while (inCsv.readRecord()) {
						String term = inCsv.get("TERM");
						if (vocabulary.containsKey(term)) {
							Integer frequency = vocabulary.get(term);
							vocabulary.put(term, frequency++);
						} else {
							vocabulary.put(term, 1);
						}
						if (!documentId.equals(inCsv.get("DOCUMENT_ID"))) {
							documentId = inCsv.get("DOCUMENT_ID");
							numberOfDocuments++;
						}
					}
				} else {
					logger.fatal("CSV-Header not read");
					System.exit(1);
				}
			} catch (IOException e) {
				logger.fatal("CSV-Header not read");
				e.printStackTrace();
				System.exit(2);
			}
			float upperBound = numberOfDocuments * upperBoundPercent;
			float lowerBound = numberOfDocuments * lowerBoundPercent;
            
			Set<String> terms = vocabulary.keySet();
			for (String term : terms) {
				if (vocabulary.get(term) < lowerBound
						|| vocabulary.get(term) > upperBound) {
					vocabulary.remove(term);
				}

			}

			inCsv.close();

			// Zweiter Durchlauf
			FileOutputStream outCsvStream = new FileOutputStream(
											properties
															.getProperty("InCSVFile")
													+ ".pruned.Lower."
													+ lowerBound
													+ ".Upper."
													+ upperBound
													+ ".csv");

			CsvWriter outCsv = new CsvWriter(outCsvStream, ';', Charset.forName("UTF-8"));
			
			
			inCsv = new CsvReader(new FileInputStream(inFilePath), ';',
					Charset.forName("UTF-8"));
			String[] headerEntries = inCsv.getHeaders();
			outCsv.writeRecord(headerEntries);
			while (inCsv.readRecord()) {
				String term = inCsv.get("TERM");
				if (vocabulary.containsKey(term)) {
					outCsv.writeRecord(inCsv.getValues());
				}
			}
			
			this.renameFile(
					properties.getProperty("InCSVFile"),
					properties.getProperty("InCSVFile")
							+ ".org." + System.currentTimeMillis());

			this.renameFile(
					properties.getProperty("InCSVFile")
							+ ".pruned.Lower." + lowerBound
							+ ".Upper." + upperBound + ".csv",
							properties.getProperty("InCSVFile"));

		} catch (FileNotFoundException e) {
			logger.fatal("Input CSV-File couldn't be read - maybe the path is incorrect");
			e.printStackTrace();
			System.exit(3);
		}
	}

}
