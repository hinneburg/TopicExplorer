package cc.topicexplorer.plugin.prune.preprocessing.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class Prune_Ram_SortedCsv {

	float upperBoundPercent;
	float lowerBoundPercent;
	String inFilePath;
	CsvReader inCsv;
	String[] headerEntries;
	private Logger logger;

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	private void renameFile(String source, String destination) {
		File sourceFile = new File(source);
		File destinationFile = new File(destination);

		if (!sourceFile.renameTo(destinationFile)) {
			logger.error("[ " + getClass() + " ] - " + "Fehler beim Umbenennen der Datei: " + source);
			throw new IllegalStateException();
		}
	}

	void setLowerAndUpperBoundPercent(float lowerBoundPercent, float upperBoundPercent) {
		this.lowerBoundPercent = lowerBoundPercent;
		this.upperBoundPercent = upperBoundPercent;
		// are the bounds valid?
		if (upperBoundPercent < 0 || lowerBoundPercent < 0 || upperBoundPercent > 100 || lowerBoundPercent > 100
				|| upperBoundPercent < lowerBoundPercent) {
			logger.error("Stop: Invalid Pruning Bounds!");
			throw new IllegalArgumentException(String.format("upperBoundPercent: %f, lowerBoundPercent: %f",
					upperBoundPercent, lowerBoundPercent));
		}
	}

	void setInFilePath(String inFilePath) {
		this.inFilePath = inFilePath;
	}

	private void openInCsvReader() {
		try {
			inCsv = new CsvReader(new FileInputStream(inFilePath), ';', Charset.forName("UTF-8"));
		} catch (FileNotFoundException e) {
			logger.error("Input CSV-File couldn't be read - maybe the path is incorrect");
			throw new IllegalStateException();
		}
	}

	private void closeInCsvReader() {
		inCsv.close();
	}

	private void readInCsvHeader() {
		try {
			inCsv.readHeaders();
			headerEntries = inCsv.getHeaders();
			// for (int i = 0; i < h.length; i++) {
			// this.headerEntries.add(h[i]);
			// }
		} catch (IOException e) {
			logger.error("CSV-Header not read");
			throw new IllegalStateException();
		}

	}

	private boolean inCsvReadRecord() {
		Boolean result = false;
		try {
			result = inCsv.readRecord();
		} catch (IOException e) {
			logger.error("Csv record could not be read.");
			throw new IllegalStateException();
		}
		return result;
	}

	public void prune() throws IOException {

		openInCsvReader();
		readInCsvHeader();

		HashMap<String, Integer> vocabulary = new HashMap<String, Integer>();
		Integer numberOfDocuments = 0;

		String documentId = new String();
		HashSet<String> documentTerms = new HashSet<String>();

		while (inCsvReadRecord()) {
			String term = inCsv.get("TERM");
			if (!documentId.equals(inCsv.get("DOCUMENT_ID"))) {
				documentId = inCsv.get("DOCUMENT_ID");
				numberOfDocuments++;
				for (String t : documentTerms) {
					if (vocabulary.containsKey(t)) {
						Integer frequency = vocabulary.get(t);
						frequency++;
						vocabulary.put(t, frequency);
					} else {
						vocabulary.put(t, 1);
					}
				}
				documentTerms.clear();
				documentTerms.add(term);
			} else {
				documentTerms.add(term);
			}

		}

		float upperBound = numberOfDocuments * upperBoundPercent / (float) 100.0;
		float lowerBound = numberOfDocuments * lowerBoundPercent / (float) 100.0;

		HashSet<String> termsToKeep = new HashSet<String>();
		for (String term : vocabulary.keySet()) {
			if (vocabulary.get(term) > lowerBound && vocabulary.get(term) < upperBound) {
				termsToKeep.add(term);
			}

		}
		System.out.println("Number of Documents " + numberOfDocuments);

		closeInCsvReader();
		// Zweiter Durchlauf
		FileOutputStream outCsvStream = new FileOutputStream(inFilePath + ".pruned.Lower." + lowerBound + ".Upper."
				+ upperBound + ".csv");

		CsvWriter outCsv = new CsvWriter(outCsvStream, ';', Charset.forName("UTF-8"));
		outCsv.setForceQualifier(true);

		openInCsvReader();
		readInCsvHeader();

		outCsv.writeRecord(headerEntries);
		while (inCsv.readRecord()) {
			String term = inCsv.get("TERM");
			if (termsToKeep.contains(term)) {
				outCsv.writeRecord(inCsv.getValues());
			}
		}
		closeInCsvReader();
		outCsv.close();

		this.renameFile(inFilePath, inFilePath + ".org." + System.currentTimeMillis());

		this.renameFile(inFilePath + ".pruned.Lower." + lowerBound + ".Upper." + upperBound + ".csv", inFilePath);
	}
}
