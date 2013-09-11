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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.sql.SQLException;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import cc.topicexplorer.chain.DatabaseContext;
import cc.topicexplorer.chain.DependencyContext;
import cc.topicexplorer.chain.PropertyContext;
import cc.topicexplorer.chain.commands.DependencyCommand;

//import tools.tokenTopicAssignment.*;

public class TokenTopicAssociator extends DependencyCommand {

	private static Properties properties;
	private static Logger logger = Logger.getRootLogger();
	
	private static List<String> outList = new ArrayList<String>();

	public static String TOKENTOPICASSIGNMENTSQLFILE = "temp/tokenTopicAssignment.sql.csv";

	// Number of Elements readed till output
	private static Integer blockSize = 5000;

	private static boolean setTokenTopicAssignment() {

		int errorCount = 0;

		BufferedWriter outListSQLWriter = null;

		try {

			outListSQLWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(TOKENTOPICASSIGNMENTSQLFILE, true),
					"UTF-8"));

			for (String outListElement : outList) {
				outListSQLWriter.write(outListElement + "\n");
			}

		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			if (outListSQLWriter != null) {
				try {
					outListSQLWriter.flush();
					outListSQLWriter.close();
				} catch (IOException e) {
					logger.error(e);				}
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
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void readAndWriteBlockwise(String inFile, String stateFile)
			throws SQLException {

		BufferedReader inListinBufferedReader = null;

		String inListcurrentLine;
		String[] inlistsplittedCurrentLine;

		String stateCurrentLine;
		String[] stateSplittedCurrentLine;

		BufferedReader stateBufferedReader = null;

		try {
			stateBufferedReader = new BufferedReader(new InputStreamReader(
					new GZIPInputStream(new FileInputStream(stateFile)),
					"UTF-8"));

			// we have to skip the first 3 lines:
			stateCurrentLine = stateBufferedReader.readLine();
			stateCurrentLine = stateBufferedReader.readLine();
			stateCurrentLine = stateBufferedReader.readLine();

			inListinBufferedReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(inFile), "UTF-8"));
			inListcurrentLine = inListinBufferedReader.readLine();;
			// checking seperator and names insult a skip of the first column
			if (InFilePreperation.checkHeader(inFile)) {

				while ((inListcurrentLine = inListinBufferedReader.readLine()) != null) {
					// beide Dateien sind gleich lang,
					// also sollte es keine Probleme beim Einlesen geben
					stateCurrentLine = stateBufferedReader.readLine();

					stateSplittedCurrentLine = stateCurrentLine.split(" ");

					outList.add(inListcurrentLine.replaceAll("\"", "")
							.replaceAll(";", "\t")
							+ "\t" + stateSplittedCurrentLine[5]);

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

		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			try {
				inListinBufferedReader.close();
				stateBufferedReader.close();

			} catch (IOException e) {
				logger.error(e);
			}
		}

	}

	@Override
	public void specialExecute(Context context) throws Exception {

		logger.info("Current Command : [ " + getClass() + " ] ");

		PropertyContext propertyContext = (PropertyContext) context;

		properties = propertyContext.getProperties();

		String stateFile = properties.getProperty("projectRoot")
				+ "temp/out.topic-state.gz";
		String inFile = properties.getProperty("projectRoot")
				+ properties.getProperty("InCSVFile");
		TOKENTOPICASSIGNMENTSQLFILE = properties.getProperty("projectRoot")
				+ "temp/tokenTopicAssignment.sql.csv";

		deleteOldTFile();

		readAndWriteBlockwise(inFile, stateFile);

		logger.info("TokenTopicAssignment finshed!");
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("Mallet");
	}	
}
