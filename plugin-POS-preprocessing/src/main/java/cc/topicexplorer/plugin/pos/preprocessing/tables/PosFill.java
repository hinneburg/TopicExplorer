package cc.topicexplorer.plugin.pos.preprocessing.tables;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;
import cc.topicexplorer.plugin.pos.preprocessing.opennlp.OpenNlp;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public class PosFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(PosFill.class);
	//private static CsvReader inCsv;

	@Override
	public void fillTable() {
		OpenNlp openNlp = new OpenNlp();
		openNlp.setPath("/home/slayer/workspace/TopicExplorer/"
					+ "plugin-POS-preprocessing/src/main/resources/opennlpResources/");
		
		//String inFilePath = TokenTopicAssociator.TOKEN_TOPIC_ASSIGNMENT_SQL_FILE;
		//String data = "/home/slayer/workspace/TopicExplorer/"
					//+ "plugin-POS-preprocessing/src/main/resources/opennlpResources/tokens.csv";
		String inFilePath = "/home/slayer/workspace/TopicExplorer/"
					+ "plugin-POS-preprocessing/src/main/resources/opennlpResources/nlpOutput.csv";
		
		//todo: header from .csv
		String[] header = openNlp.getHeader();
		logger.info("load data into table " + "DOCUMENT_TERM_TOPIC");

		//inCsv = tryToInstantiateCsvReader();

		try {
			//Preconditions.checkState(inCsv.readHeaders(), "CSV-Header not read");
			//String[] headerEntries = inCsv.getHeaders();
			//String header = "";
			//for (String headerEntrie : headerEntries) {
				//header += headerEntrie + ",";
			
			//header += "TOPIC_ID";
			database.executeUpdateQuery("LOAD DATA LOCAL INFILE '" + inFilePath + "' IGNORE INTO TABLE "
					+ "DOCUMENT_TERM_TOPIC" + " CHARACTER SET utf8 (" + header + " );");

			database.executeUpdateQuery("ALTER TABLE " + "DOCUMENT_TERM_TOPIC" + " ADD KEY IDX0 (`" + "DOCUMENT_ID"
					+ "`,`" + "TOPIC_ID" + "`,`" + "POSITION_OF_TOKEN_IN_DOCUMENT" + "`), " + " ADD KEY IDX1 (`"
					+ "DOCUMENT_ID" + "`), " + " ADD KEY IDX2 (`" + "TERM" + "`), " + " ADD KEY IDX3 (`"
					+ "DOCUMENT_ID" + "`,`" + "TERM" + "`,`" + "TOPIC_ID" + "`), " + " ADD KEY IDX4 (`" + "DOCUMENT_ID"
					+ "`,`" + "TOPIC_ID" + "`), " + " ADD KEY IDX5 (`" + "TERM" + "`,`" + "TOPIC_ID" + "`) ");

			logger.info("load data into table " + this.tableName + " done.");
		} catch (SQLException sqlEx) {
			logger.error("Table " + this.tableName + " could not be filled properly.");
			throw new RuntimeException(sqlEx);
		}
		/** OHNE_JOOQ-ENDE */
	}
/*
	private CsvReader tryToInstantiateCsvReader() {
		try {
			return new CsvReader(new FileInputStream(properties.getProperty("InCSVFile")), ';',
					Charset.forName("UTF-8"));
		} catch (FileNotFoundException fnfEx) {
			logger.error("Input CSV-File couldn't be read - maybe the path is incorrect");
			throw new RuntimeException(fnfEx);
		}
	}
*/
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