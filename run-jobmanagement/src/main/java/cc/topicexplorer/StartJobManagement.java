package cc.topicexplorer;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import cc.topicexplorer.utils.LoggerUtil;
import cc.topicexplorer.utils.PropertiesUtil;

public class StartJobManagement {

	private static final Logger logger = Logger.getLogger(StartJobManagement.class);

	public static void main(String[] args) throws Exception {
		LoggerUtil.initializeLogger();

		Properties dbProps = PropertiesUtil.updateOptionalProperties(new Properties(), "cmdb", "");

		Connection con = DriverManager.getConnection("jdbc:mysql://" + dbProps.getProperty("DbLocation")
				+ "?useUnicode=true&characterEncoding=UTF-8&useCursorFetch=true", dbProps.getProperty("DbUser"),
				dbProps.getProperty("DbPassword"));

		Statement getNextJobStmt = con.createStatement();

		ResultSet nextJobRS;

		// check if job is running
		nextJobRS = getNextJobStmt
				.executeQuery("SELECT COUNT(*) AS RUNNING_JOBS_COUNT FROM TOPIC_EXPLORER WHERE RUNNING IS NOT NULL AND FINISHED IS NULL");
		if (nextJobRS.next()) {
			if (nextJobRS.getInt("RUNNING_JOBS_COUNT") > 0) {
				logger.info("there is a running job atm.");
				System.exit(0);
			}
		}

		nextJobRS = getNextJobStmt
				.executeQuery("SELECT * FROM TOPIC_EXPLORER WHERE RUNNING IS NULL ORDER BY PENDING LIMIT 1");

		if (nextJobRS.next()) {
			int jobId = nextJobRS.getInt("TOPIC_EXPLORER_ID");
			logger.info("starting next job: " + jobId);
			Statement setNextJobStmt = con.createStatement();
			setNextJobStmt.executeUpdate("UPDATE TOPIC_EXPLORER SET RUNNING=NOW() WHERE TOPIC_EXPLORER_ID=" + jobId);
			setNextJobStmt.close();

			decompressConfigs(nextJobRS.getBytes("ZIPPED_CONFIGS"));

			generateCsv();

			cc.topicexplorer.Run.main(args);

			Statement setNextJobStmt2 = con.createStatement();
			setNextJobStmt2.executeUpdate("UPDATE TOPIC_EXPLORER SET FINISHED=NOW() WHERE TOPIC_EXPLORER_ID=" + jobId);
			setNextJobStmt2.close();

			System.exit(0);
		} else {
			logger.info("no jobs in queue");
			System.exit(0);
		}
	}

	private static void decompressConfigs(byte[] zippedConfigs) {
		logger.info("decompress property files");
		try {
			ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zippedConfigs));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				logger.info("Extracting: " + entry);
				int count;
				byte data[] = new byte[1024];
				// write the files to the disk
				FileOutputStream fos = new FileOutputStream("resources" + File.separator + entry.getName());
				BufferedOutputStream dest = new BufferedOutputStream(fos, 1024);
				while ((count = zis.read(data, 0, 1024)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
			zis.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
			System.exit(0);
		}
	}

	private static void generateCsv() {
		logger.info("generating csv");

		try {
			Properties props = PropertiesUtil.loadMandatoryProperties("config", "");
			Writer csvWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					props.getProperty("InCSVFile")), "utf-8"));

			Properties dbProps = PropertiesUtil.loadMandatoryProperties("database", "");

			Connection con = DriverManager.getConnection("jdbc:mysql://" + dbProps.getProperty("DbLocation") + "/"
					+ dbProps.getProperty("DB") + "?useUnicode=true&characterEncoding=UTF-8&useCursorFetch=true",
					dbProps.getProperty("DbUser"), dbProps.getProperty("DbPassword"));

			csvWriter
					.write("\"DOCUMENT_ID\";\"POSITION_OF_TOKEN_IN_DOCUMENT\";\"TERM\";\"TOKEN\";\"WORDTYPE$WORDTYPE\"\n");

			JSONParser jsonParser = new JSONParser();
			JSONObject wordtypeItem;
			JSONArray stopWords;
			JSONArray rootArray = (JSONArray) jsonParser.parse(new FileReader("resources" + File.separator
					+ "wordlist.json"));
			String getCsvSql = "SELECT DOCUMENT_TERM.DOCUMENT_ID, DOCUMENT_TERM.POSITION_OF_TOKEN_IN_DOCUMENT, DOCUMENT_TERM.TERM, "
					+ "DOCUMENT_TERM.TOKEN, ALL_TERMS.POS FROM DOCUMENT_TERM, ALL_TERMS WHERE ALL_TERMS.TERM=DOCUMENT_TERM.TERM "
					+ "AND ALL_TERMS.POS=DOCUMENT_TERM.WORDTYPE_CLASS ";
			if (rootArray.size() > 0) {
				wordtypeItem = (JSONObject) rootArray.get(0);
				getCsvSql += " AND ((ALL_TERMS.POS=" + wordtypeItem.get("id") + " AND ALL_TERMS.COUNT<"
						+ wordtypeItem.get("upperBorder") + " AND ALL_TERMS.COUNT>" + wordtypeItem.get("lowerBorder");
				stopWords = (JSONArray) wordtypeItem.get("stopWords");

				getCsvSql += " AND ALL_TERMS.TERM NOT IN ('*'";
				for (int j = 0; j < stopWords.size(); j++) {
					getCsvSql += ",'" + stopWords.get(0) + "'";
				}
				getCsvSql += "))";

				for (int i = 1; i < rootArray.size(); i++) {
					wordtypeItem = (JSONObject) rootArray.get(i);
					getCsvSql += " OR (ALL_TERMS.POS=" + wordtypeItem.get("id") + " AND ALL_TERMS.COUNT<"
							+ wordtypeItem.get("upperBorder") + " AND ALL_TERMS.COUNT>"
							+ wordtypeItem.get("lowerBorder");
					stopWords = (JSONArray) wordtypeItem.get("stopWords");

					getCsvSql += " AND ALL_TERMS.TERM NOT IN ('*'";
					for (int j = 0; j < stopWords.size(); j++) {
						getCsvSql += ",'" + stopWords.get(j) + "'";
					}
					getCsvSql += "))";
				}
				getCsvSql += ")";
			}
			getCsvSql += " ORDER BY DOCUMENT_ID, POSITION_OF_TOKEN_IN_DOCUMENT";

			logger.info("bla: " + getCsvSql);

			Statement genCsvStmt = con.createStatement();
			ResultSet genCsvRs = genCsvStmt.executeQuery(getCsvSql);
			while (genCsvRs.next()) {
				csvWriter.write("\"" + genCsvRs.getString(1) + "\"");
				for (int k = 2; k <= 5; k++) {
					csvWriter.write(";\"" + genCsvRs.getString(k) + "\"");
				}
				csvWriter.write("\n");
				csvWriter.flush();

			}

			csvWriter.close();

			// new File("resources" + File.separator +
			// "wordlist.json").delete();

		} catch (Exception e) {
			logger.error(e.getMessage());
			System.exit(0);
		}

	}
}
