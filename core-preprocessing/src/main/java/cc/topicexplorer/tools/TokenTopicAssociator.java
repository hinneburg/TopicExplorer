package cc.topicexplorer.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import cc.commandmanager.core.CommunicationContext;
import cc.commandmanager.core.DependencyCommand;

public class TokenTopicAssociator extends DependencyCommand {
	private static Properties properties;
	private static Logger logger = Logger.getRootLogger();
	private static List<String> outList = new ArrayList<String>();
	public static String TOKENTOPICASSIGNMENTSQLFILE = "temp/tokenTopicAssignment.sql.csv";
	// Number of Elements readed till output
	private static Integer blockSize = 5000;

	private static boolean setTokenTopicAssignment() {
		BufferedWriter outListSQLWriter = null;

		try {
			outListSQLWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					TOKENTOPICASSIGNMENTSQLFILE, true), "UTF-8"));

			for (String outListElement : outList) {
				outListSQLWriter.write(outListElement + "\n");
			}

		} catch (UnsupportedEncodingException encEx) {
			logger.warn("Encoding problems occured", encEx);
		} catch (FileNotFoundException fnfEx) {
			logger.warn("A required file was not found.", fnfEx);
		} catch (IOException ioEx1) {
			logger.warn("Problems with a file stream occured.", ioEx1);
		} finally {
			if (outListSQLWriter != null) {
				try {
					outListSQLWriter.flush();
					outListSQLWriter.close();
				} catch (IOException ioEx2) {
					logger.warn("Problems occured while releasing a writer resource.", ioEx2);
				}
			}
		}

		outList.clear();
		return true;
	}

	private static void deleteOldTFile() {
		File f = new File(TOKENTOPICASSIGNMENTSQLFILE);

		try {
			if (f.exists()) {
				f.delete();
				f.createNewFile();
			}
		} catch (IOException e) {
			logger.warn("Temporary file could not be deledet.", e);
		}
	}

	private void readAndWriteBlockwise(String inFile, String stateFile) throws SQLException,
			UnsupportedEncodingException, FileNotFoundException, IOException {

		BufferedReader inListinBufferedReader = null;

		String inListcurrentLine;

		String stateCurrentLine;
		String[] stateSplittedCurrentLine;

		BufferedReader stateBufferedReader = null;

		stateBufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(
				stateFile)), "UTF-8"));

		// we have to skip the first 3 lines:
		stateCurrentLine = stateBufferedReader.readLine();
		stateCurrentLine = stateBufferedReader.readLine();
		stateCurrentLine = stateBufferedReader.readLine();

		inListinBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), "UTF-8"));
		inListcurrentLine = inListinBufferedReader.readLine();
		;
		// checking seperator and names insult a skip of the first column
		if (InFilePreparation.checkHeader(inFile)) {

			while ((inListcurrentLine = inListinBufferedReader.readLine()) != null) {
				// beide Dateien sind gleich lang,
				// also sollte es keine Probleme beim Einlesen geben
				stateCurrentLine = stateBufferedReader.readLine();

				stateSplittedCurrentLine = stateCurrentLine.split(" ");

				outList.add(inListcurrentLine.replaceAll("\"", "").replaceAll(";", "\t") + "\t"
						+ stateSplittedCurrentLine[5]);

				// irgendwann zusammenführen und listen leeren
				// blockweise, vielleicht später direkt an bufferedReader
				// übergeben
				if (outList.size() == blockSize) {
					setTokenTopicAssignment();
				}
			}
		}

		// restlichen Daten anfügen
		setTokenTopicAssignment();

		inListinBufferedReader.close();
		stateBufferedReader.close();

	}

	@Override
	public void specialExecute(Context context) {
		logger.info("Current Command : [ " + getClass() + " ] ");

		CommunicationContext communicationContext = (CommunicationContext) context;
		properties = (Properties) communicationContext.get("properties");

		String stateFile = "temp/out.topic-state.gz";
		String inFile = properties.getProperty("InCSVFile");

		deleteOldTFile();

		try {
			readAndWriteBlockwise(inFile, stateFile);
		} catch (UnsupportedEncodingException encEx) {
			logger.error("Charset encoding problems occured while trying to read and write blockwise");
			throw new RuntimeException(encEx);
		} catch (FileNotFoundException fnfEx) {
			logger.error("Required file could not be found for reading and writing blockwise");
			throw new RuntimeException(fnfEx);
		} catch (SQLException sqlEx) {
			logger.error("A database access error occured while trying to read and write blockwise");
			throw new RuntimeException(sqlEx);
		} catch (IOException ioEx) {
			logger.error("File stream problems occured while trying to read and write blockwise");
			throw new RuntimeException(ioEx);
		}

		logger.info("TokenTopicAssignment finshed!");
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("Mallet");
	}
}
