package cc.topicexplorer.tools;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.commandmanager.core.ResultState;
import cc.topicexplorer.database.Database;

import com.csvreader.CsvReader;
import com.google.common.collect.Sets;

/** MIT-JOOQ-START
 import static jooq.generated.Tables.DOCUMENT_TERM_TOPIC;
 MIT-JOOQ-ENDE */

/**
 * <h1>This class represents the first step in the TopicExplorer-workflow</h1>
 *
 * <p>
 * The class owns functions to proof the given in-file and prepare the
 * Mallet-in-file
 * </p>
 *
 * @author Matthias Pfuhl
 *
 */
public class InFilePreparation implements Command {

	private static final Logger logger = Logger.getLogger(InFilePreparation.class);
	private static Database database;
	private static CsvReader inCsv;

	private Properties properties;

	@Override
	public ResultState execute(Context context) {
		properties = (Properties) context.get("properties");

		if (!properties.get("newTopics").toString().equalsIgnoreCase("true")) {
			logger.info("Skip: Take the previous DOCUMENT_TERM_TOPIC table");
		} else {
			logger.info("preparing the in-file for mallet");

			database = (Database) context.get("database");
			String inFile = properties.getProperty("InCSVFile");

			if (checkHeader(inFile)) {
				writeMalletInFile("temp/malletinput.txt");
				logger.info("[ " + getClass() + " ] - " + "the in-file for mallet successfully prepared");
				inCsv.close();
			} else {
				return ResultState.failure("Missing CSV header in table DOCUMENT_TERM_TOPIC");
			}
		}
		return ResultState.success();
	}

	/**
	 * The function checks if the used separator and the static defined names
	 * are correct
	 *
	 * @return true if all is correct, else false (with a little information)
	 *
	 * @throws SQLException
	 *             if a database access error occurs
	 * @throws IllegalStateException
	 *             if the CSV headers could not be read successfully
	 * @throws IOException
	 *             if reading the CSV file causes a problem
	 */
	public static boolean checkHeader(String inFile) {

		inCsv = tryToConstructCsvReader(inFile);

		try {
			if (inCsv.readHeaders()) {
				String[] headerEntries = inCsv.getHeaders();
				List<String> tableColumnList = new ArrayList<String>();

				/**
				 * MIT-JOOQ-START ResultSet rs =
				 * database.executeQuery("SELECT * FROM " +
				 * DOCUMENT_TERM_TOPIC.getName()); MIT-JOOQ-ENDE
				 */
				/** OHNE_JOOQ-START */

				String query = "SELECT * FROM DOCUMENT_TERM_TOPIC";
				try {
					ResultSet rs = database.executeQuery(query);
					/** OHNE_JOOQ-ENDE */
					ResultSetMetaData md = rs.getMetaData();
					for (int i = 1; i <= md.getColumnCount(); i++) {
						tableColumnList.add(md.getColumnName(i));
					}
				} catch (SQLException e) {
					logger.error("Error in Query: " + query);
					throw new RuntimeException(e);
				}

				for (int j = 0; j < headerEntries.length; j++) {
					if (!tableColumnList.contains(headerEntries[j])) {
						logger.warn("The CSV-Column " + headerEntries[j] + " is not in the DOCUMENT_TERM_TOPIC table");
						return false;
					}
				}

			} else {
				logger.error("CSV-Header not successfully read.");
				throw new IllegalStateException();
			}
		} catch (IOException e) {
			logger.error("CSV-File problems occured.");
			throw new RuntimeException(e);
		}

		return true;
	}

	private static CsvReader tryToConstructCsvReader(String inFile) {
		try {
			return new CsvReader(new FileInputStream(inFile), ';', Charset.forName("UTF-8"));
		} catch (FileNotFoundException e) {
			logger.error("Input CSV-File couldn't be read - maybe the path is incorrect");
			throw new RuntimeException(e);
		}
	}

	private static void writeMalletInFile(String malletPreparedFile) {

		try {
			BufferedWriter malletInFileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					malletPreparedFile), "UTF-8"));
			int currentDocID = -1;
			String documentString = "";

			while (inCsv.readRecord()) {
				if (currentDocID == -1) {

					// first Doc -> Set the currentDocID
					currentDocID = Integer.parseInt(inCsv.get("DOCUMENT_ID"));
					// start to set the documentString with ID and LANGUAGE
					documentString = currentDocID + "\tDE\t" + inCsv.get("TERM");

				} else if (currentDocID != Integer.parseInt(inCsv.get("DOCUMENT_ID"))) {

					// new document, write current down
					documentString += "\n";
					malletInFileWriter.write(documentString);
					// set new DocID
					currentDocID = Integer.parseInt(inCsv.get("DOCUMENT_ID"));
					// start to set the new documentString with ID and LANGUAGE
					documentString = currentDocID + "\tDE\t" + inCsv.get("TERM");

				} else {

					// same Document, but new Token -> append it
					documentString += " " + inCsv.get("TERM");
				}
			}
			// We have to set the last document
			malletInFileWriter.write(documentString);

			malletInFileWriter.close();
		} catch (IOException e) {
			logger.error("IO exception occured while writing mallet in file.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
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
