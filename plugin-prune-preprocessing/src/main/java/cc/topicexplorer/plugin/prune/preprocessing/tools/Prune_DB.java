package cc.topicexplorer.plugin.prune.preprocessing.tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.commandmanager.core.DependencyCommand;
import cc.topicexplorer.database.Database;

import com.csvreader.CsvReader;
import com.google.common.base.Preconditions;

/**
 * MIT-JOOQ-START import static jooq.generated.Tables.DOCUMENT_TERM_TOPIC;
 * MIT-JOOQ-ENDE
 */

public class Prune_DB extends DependencyCommand {
	private Properties properties;
	private static CsvReader inCsv;
	protected cc.topicexplorer.database.Database database;

	private void renameFile(String source, String destination) {
		File sourceFile = new File(source);
		File destinationFile = new File(destination);

		if (!sourceFile.renameTo(destinationFile)) {
			logger.error("File could not be renamed: " + source);
			throw new IllegalStateException();
		}
	}

	@Override
	public void specialExecute(Context context) {

		logger.info("[ " + getClass() + " ] - " + "pruning vocabular");

		CommunicationContext communicationContext = (CommunicationContext) context;
		properties = (Properties) communicationContext.get("properties");
		database = (Database) communicationContext.get("database");

		float upperBound, lowerBound;
		String inFilePath = properties.getProperty("InCSVFile");
		try {
			inCsv = new CsvReader(new FileInputStream(inFilePath), ';', Charset.forName("UTF-8"));

			Preconditions.checkState(inCsv.readHeaders(), "CSV-Header not read");
			String[] headerEntries = inCsv.getHeaders();
			String select = "TEMP4PRUNE." + headerEntries[0];
			String header = "\"" + headerEntries[0] + "\"";
			for (int j = 1; j < headerEntries.length; j++) {
				select += ",TEMP4PRUNE." + headerEntries[j];
				header += ";\"" + headerEntries[j] + "\"";
			}

			float upperBoundPercent = Float.parseFloat(properties.getProperty("Prune_upperBound"));
			float lowerBoundPercent = Float.parseFloat(properties.getProperty("Prune_lowerBound"));

			// are the bounds valid?
			if (upperBoundPercent < 0 || lowerBoundPercent < 0 || upperBoundPercent > 100 || lowerBoundPercent > 100
					|| upperBoundPercent < lowerBoundPercent) {
				logger.error("Stop: Invalid Pruning Bounds!");
				throw new IllegalArgumentException(String.format("upperBoundPercent: %f, lowerBoundPercent: %f",
						upperBoundPercent, lowerBoundPercent));
			}

			// copy from doctermtopic and delete topic_id
			/**
			 * MIT-JOOQ-START database.executeUpdateQuery(
			 * "CREATE TABLE IF NOT EXISTS TEMP4PRUNE LIKE " +
			 * DOCUMENT_TERM_TOPIC.getName()); MIT-JOOQ-ENDE
			 */

			/** OHNE_JOOQ-START */
			try {
				database.executeUpdateQuery("CREATE TABLE IF NOT EXISTS TEMP4PRUNE LIKE DOCUMENT_TERM_TOPIC");
			} catch (SQLException e1) {
				logger.error("Essential table TEMP4PRUNE could not be created.");
				throw new RuntimeException(e1);
			}
			/** OHNE_JOOQ-ENDE */

			try {
				database.executeUpdateQuery("ALTER TABLE TEMP4PRUNE DROP COLUMN TOPIC_ID");
			} catch (SQLException e2) {
				logger.error("Column TOPIC_ID could not be dropped in table TEMP4PRUNE, though it should be dropped.");
				throw new RuntimeException(e2);
			}

			try {
				database.executeUpdateQuery("LOAD DATA LOCAL INFILE '" + inFilePath + "' IGNORE INTO TABLE TEMP4PRUNE "
						+ "CHARACTER SET UTF8 FIELDS TERMINATED BY ';' ENCLOSED BY '\"' IGNORE 1 LINES (" + select
						+ ");");
			} catch (SQLException e3) {
				logger.error("Local data could not be loaded into table TEMP4PRUNE properly.");
				throw new RuntimeException(e3);
			}

			PrintWriter writer = null;
			String queryForDocCountRs = "SELECT COUNT(DISTINCT DOCUMENT_ID) FROM TEMP4PRUNE";
			try {
				ResultSet rsDocCount = database.executeQuery(queryForDocCountRs);
				int count = 0;
				if (rsDocCount.next()) {
					count = rsDocCount.getInt(1);
					upperBound = (float) (count / 100.0) * upperBoundPercent;
					lowerBound = (float) (count / 100.0) * lowerBoundPercent;
				} else {
					lowerBound = 0.0f;
					upperBound = Float.MAX_VALUE;
				}

				logger.info("Pruning: count: " + count + " lower: " + lowerBound + " upper: " + upperBound);
				writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(
						properties.getProperty("InCSVFile") + ".pruned.Lower." + lowerBound + ".Upper." + upperBound
								+ ".csv")), "UTF-8"));
			} catch (SQLException e4) {
				logger.error("Error in Query: " + queryForDocCountRs);
				throw new RuntimeException(e4);
			}

			try {
//				@formatter:off
				database.executeUpdateQuery("CREATE TABLE TEMP4PRUNE2 (UNIQUE (TERM)) "
						+ "ENGINE=MEMORY AS SELECT TERM FROM TEMP4PRUNE GROUP BY TERM "
						+ "HAVING COUNT(DISTINCT DOCUMENT_ID) < " + upperBound + " AND COUNT(DISTINCT DOCUMENT_ID) > "
						+ lowerBound);
//				@formatter:on
			} catch (SQLException e5) {
				logger.error("Essential table TEMP4PRUNE2 could not be created.");
				writer.close();
				throw new RuntimeException(e5);
			}

//			@formatter:off
			String queryForPrunedRs = "SELECT " + select + " FROM " + "TEMP4PRUNE,TEMP4PRUNE2 "
					+ "WHERE TEMP4PRUNE.TERM=TEMP4PRUNE2.TERM "
					+ "ORDER BY DOCUMENT_ID,POSITION_OF_TOKEN_IN_DOCUMENT";
//			@formatter:on
			try {
				ResultSet prunedRS = database.executeQuery(queryForPrunedRs);

				ResultSetMetaData md = prunedRS.getMetaData();

				writer.append(header).println();

				String line;
				while (prunedRS.next()) {
					line = "";
					for (int i = 1; i <= md.getColumnCount(); i++) {
						if (i > 1) {
							line += ";";
						}

						int type = md.getColumnType(i);
						if (type == Types.VARCHAR || type == Types.CHAR) {
							line += "\"" + prunedRS.getString(i) + "\"";
						} else {
							line += "\"" + prunedRS.getLong(i) + "\"";
						}
					}

					writer.append(line).println();
				}
			} catch (SQLException e6) {
				logger.error("Error in Query: " + queryForPrunedRs);
				throw new RuntimeException(e6);
			} finally {
				writer.close();
			}

			try {
				database.executeUpdateQuery("DROP TABLE TEMP4PRUNE;");
				database.executeUpdateQuery("DROP TABLE TEMP4PRUNE2;");
			} catch (SQLException e7) {
				logger.warn("Table TEMP4PRUNE or TEMP4PRUNE2 (latter only in memory) could not be dropped.", e7);
			}

			this.renameFile(properties.getProperty("InCSVFile"),
					properties.getProperty("InCSVFile") + ".org." + System.currentTimeMillis());

			this.renameFile(properties.getProperty("InCSVFile") + ".pruned.Lower." + lowerBound + ".Upper."
					+ upperBound + ".csv", properties.getProperty("InCSVFile"));
		} catch (FileNotFoundException e8) {
			logger.error("Required file could not be found.");
			throw new RuntimeException(e8);
		} catch (IOException e9) {
			logger.error("Handling CSV headers caused a file stream problem.");
			throw new RuntimeException(e9);
		}

		inCsv.close();
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentTermTopicCreate");
		afterDependencies.add("InFilePreparation");
	}
}