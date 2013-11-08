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

public class PruneAction_TwoPassMainMemoryVocabulary {

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
			logger.fatal("[ " + getClass() + " ] - " + "Fehler beim Umbenennen der Datei: " + source);
			System.exit(0);
		}
	}

	void setLowerAndUpperBoundPercent(float lowerBoundPercent, float upperBoundPercent) {
		this.lowerBoundPercent = lowerBoundPercent;
		this.upperBoundPercent = upperBoundPercent;
		// are the bounds valid?
		if (upperBoundPercent < 0 || lowerBoundPercent < 0 || upperBoundPercent > 100 || lowerBoundPercent > 100
				|| upperBoundPercent < lowerBoundPercent) {
			logger.fatal("Stop: Invalid Pruning Bounds!");
			System.exit(0);
		}
	}

	void setInFilePath(String inFilePath) {
		this.inFilePath = inFilePath;
	}

	private void openInCsvReader() {
		try {
			inCsv = new CsvReader(new FileInputStream(inFilePath), ';', Charset.forName("UTF-8"));
		} catch (FileNotFoundException e) {
			logger.fatal("Input CSV-File couldn't be read - maybe the path is incorrect");
			e.printStackTrace();
			System.exit(3);

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
			logger.fatal("CSV-Header not read");
			e.printStackTrace();
			System.exit(2);
		}

	}

	private boolean inCsvReadRecord() {
		Boolean result = false;
		try {
			result = inCsv.readRecord();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return result;
	}

	public void prune() throws IOException {

		openInCsvReader();
		readInCsvHeader();

		HashMap<String, HashSet<String>> vocabulary = new HashMap<String, HashSet<String>>();
		HashSet<String> documents = new HashSet<String>();
		Integer numberOfDocuments = 0;

		String documentId = new String();

		while (inCsvReadRecord()) {
			documentId = inCsv.get("DOCUMENT_ID");
			String term = inCsv.get("TERM");
			documents.add(documentId);

			if (vocabulary.containsKey(term)) {
				HashSet<String> docs = vocabulary.get(term);
				docs.add(documentId);
				vocabulary.put(term, docs);
			} else {
				HashSet<String> docs = new HashSet<String>();
				docs.add(documentId);
				vocabulary.put(term, docs);
			}

		}
		if (documents.size() > 0) {
			numberOfDocuments = documents.size();
		} else {
			// logger.fatal("Document List from InCSV is empty.");
			// TODO: In Testklasse Logger initialisieren!
			System.exit(1);
		}

		float upperBound = numberOfDocuments * upperBoundPercent / (float) 100.0;
		float lowerBound = numberOfDocuments * lowerBoundPercent / (float) 100.0;

		HashSet<String> termsToKeep = new HashSet<String>();
		for (String term : vocabulary.keySet()) {
			if (vocabulary.get(term).size() > lowerBound && vocabulary.get(term).size() < upperBound) {
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
