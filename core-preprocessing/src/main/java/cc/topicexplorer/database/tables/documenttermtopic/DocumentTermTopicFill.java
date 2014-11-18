package cc.topicexplorer.database.tables.documenttermtopic;

/** MIT-JOOQ-START 
 import static jooq.generated.Tables.DOCUMENT_TERM_TOPIC;
 MIT-JOOQ-ENDE */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;
import cc.topicexplorer.tools.TokenTopicAssociator;

import com.csvreader.CsvReader;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public class DocumentTermTopicFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(DocumentTermTopicFill.class);
	private static CsvReader inCsv;

	@Override
	public void fillTable() {
		if(!properties.get("newTopics").toString().equalsIgnoreCase("true")) {
			logger.info("Skip: Take the previous DOCUMENT_TERM_TOPIC table");
		} else {
			String inFilePath = TokenTopicAssociator.TOKEN_TOPIC_ASSIGNMENT_SQL_FILE;
			/**
			 * MIT-JOOQ-START logger.info("load data into table " + DOCUMENT_TERM_TOPIC.getName());
			 * 
			 * try { inCsv = new CsvReader(new FileInputStream( properties.getProperty("InCSVFile")), ';',
			 * Charset.forName("UTF-8")); try { if (inCsv.readHeaders()) { String[] headerEntries = inCsv.getHeaders();
			 * String header = ""; for (int j = 0; j < headerEntries.length; j++) { header += headerEntries[j] + ","; }
			 * header += "TOPIC_ID"; database.executeUpdateQuery("LOAD DATA LOCAL INFILE '" + inFilePath +
			 * "' IGNORE INTO TABLE " + DOCUMENT_TERM_TOPIC.getName() + " CHARACTER SET utf8 (" + header + " );");
			 * 
			 * database.executeUpdateQuery("ALTER TABLE " + DOCUMENT_TERM_TOPIC.getName() + " ADD KEY IDX0 (`" +
			 * DOCUMENT_TERM_TOPIC.DOCUMENT_ID.getName() + "`,`" + DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + "`,`" +
			 * DOCUMENT_TERM_TOPIC.POSITION_OF_TOKEN_IN_DOCUMENT .getName() + "`), " + " ADD KEY IDX1 (`" +
			 * DOCUMENT_TERM_TOPIC.DOCUMENT_ID.getName() + "`), " + " ADD KEY IDX2 (`" + DOCUMENT_TERM_TOPIC.TERM.getName()
			 * + "`), " + " ADD KEY IDX3 (`" + DOCUMENT_TERM_TOPIC.DOCUMENT_ID.getName() + "`,`" +
			 * DOCUMENT_TERM_TOPIC.TERM.getName() + "`,`" + DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + "`), " +
			 * " ADD KEY IDX4 (`" + DOCUMENT_TERM_TOPIC.DOCUMENT_ID.getName() + "`,`" +
			 * DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + "`), " + " ADD KEY IDX5 (`" + DOCUMENT_TERM_TOPIC.TERM.getName() +
			 * "`,`" + DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + "`) ");
			 * 
			 * logger.info("load data into table " + this.tableName + " done."); } else {
			 * logger.fatal("CSV-Header not read"); System.exit(1); } } catch (IOException e) {
			 * logger.fatal("CSV-Header not read"); e.printStackTrace(); System.exit(2); } } catch (FileNotFoundException e)
			 * { logger.fatal( "Input CSV-File couldn't be read - maybe the path is incorrect"); e.printStackTrace();
			 * System.exit(3); } MIT-JOOQ-ENDE
			 */
			/** OHNE_JOOQ-START */
			logger.info("load data into table " + "DOCUMENT_TERM_TOPIC");
	
			inCsv = tryToInstantiateCsvReader();
	
			try {
				Preconditions.checkState(inCsv.readHeaders(), "CSV-Header not read");
				String[] headerEntries = inCsv.getHeaders();
				String header = "";
				for (String headerEntrie : headerEntries) {
					header += headerEntrie + ",";
				}
				header += "TOPIC_ID";
				database.executeUpdateQuery("LOAD DATA LOCAL INFILE '" + inFilePath + "' IGNORE INTO TABLE "
						+ "DOCUMENT_TERM_TOPIC" + " CHARACTER SET utf8 (" + header + " );");
	
				database.executeUpdateQuery("ALTER TABLE " + "DOCUMENT_TERM_TOPIC" + " ADD KEY IDX0 (`" + "DOCUMENT_ID"
						+ "`,`" + "TOPIC_ID" + "`,`" + "POSITION_OF_TOKEN_IN_DOCUMENT" + "`), " + " ADD KEY IDX1 (`"
						+ "DOCUMENT_ID" + "`), " + " ADD KEY IDX2 (`" + "TERM" + "`), " + " ADD KEY IDX3 (`"
						+ "DOCUMENT_ID" + "`,`" + "TERM" + "`,`" + "TOPIC_ID" + "`), " + " ADD KEY IDX4 (`" + "DOCUMENT_ID"
						+ "`,`" + "TOPIC_ID" + "`), " + " ADD KEY IDX5 (`" + "TERM" + "`,`" + "TOPIC_ID" + "`) ");
	
				logger.info("load data into table " + this.tableName + " done.");
			} catch (IOException ioEx) {
				logger.error("CSV data could not be read.");
				throw new RuntimeException(ioEx);
			} catch (SQLException sqlEx) {
				logger.error("Table " + this.tableName + " could not be filled properly.");
				throw new RuntimeException(sqlEx);
			}
			/** OHNE_JOOQ-ENDE */
		}
	}

	private CsvReader tryToInstantiateCsvReader() {
		try {
			return new CsvReader(new FileInputStream(properties.getProperty("InCSVFile")), ';',
					Charset.forName("UTF-8"));
		} catch (FileNotFoundException fnfEx) {
			logger.error("Input CSV-File couldn't be read - maybe the path is incorrect");
			throw new RuntimeException(fnfEx);
		}
	}

	@Override
	public void setTableName() {
		/**
		 * MIT-JOOQ-START tableName = DOCUMENT_TERM_TOPIC.getName(); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		tableName = "DOCUMENT_TERM_TOPIC";
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentTermTopicCreate", "TokenTopicAssociator");
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
