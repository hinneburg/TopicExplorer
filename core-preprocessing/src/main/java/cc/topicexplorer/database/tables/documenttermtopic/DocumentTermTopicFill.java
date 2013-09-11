package cc.topicexplorer.database.tables.documenttermtopic;
/** MIT-JOOQ-START 
import static jooq.generated.Tables.DOCUMENT_TERM_TOPIC;
 MIT-JOOQ-ENDE */ 
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import cc.topicexplorer.tools.TokenTopicAssociator;

import cc.topicexplorer.chain.commands.TableFillCommand;

import java.nio.charset.Charset;
import com.csvreader.CsvReader;

public class DocumentTermTopicFill extends TableFillCommand {
	private static CsvReader inCsv;
	@Override
	public void fillTable() throws SQLException {

		// String inFilePath = properties.getProperty("projectRoot")
		// + TokenTopicAssociator.TOKENTOPICASSIGNMENTSQLFILE;
		String inFilePath = TokenTopicAssociator.TOKENTOPICASSIGNMENTSQLFILE;
/** MIT-JOOQ-START 
		System.out.println("load data into table "
				+ DOCUMENT_TERM_TOPIC.getName());

		try {
			inCsv = new CsvReader(new FileInputStream(
					properties.getProperty("projectRoot")
							+ properties.getProperty("InCSVFile")), ';',
					Charset.forName("UTF-8"));
			try {
				if (inCsv.readHeaders()) {
					String[] headerEntries = inCsv.getHeaders();
					String header = "";
					for (int j = 0; j < headerEntries.length; j++) {
						header += headerEntries[j] + ",";
					}
					header += "TOPIC_ID";
					database.executeUpdateQuery("LOAD DATA LOCAL INFILE '"
							+ inFilePath + "' IGNORE INTO TABLE "
							+ DOCUMENT_TERM_TOPIC.getName()
							+ " CHARACTER SET utf8 (" + header + " );");

					database.executeUpdateQuery("ALTER TABLE "
							+ DOCUMENT_TERM_TOPIC.getName()
							+ " ADD KEY IDX0 (`"
							+ DOCUMENT_TERM_TOPIC.DOCUMENT_ID.getName()
							+ "`,`"
							+ DOCUMENT_TERM_TOPIC.TOPIC_ID.getName()
							+ "`,`"
							+ DOCUMENT_TERM_TOPIC.POSITION_OF_TOKEN_IN_DOCUMENT
									.getName() + "`), " + " ADD KEY IDX1 (`"
							+ DOCUMENT_TERM_TOPIC.DOCUMENT_ID.getName()
							+ "`), " + " ADD KEY IDX2 (`"
							+ DOCUMENT_TERM_TOPIC.TERM.getName() + "`), "
							+ " ADD KEY IDX3 (`"
							+ DOCUMENT_TERM_TOPIC.DOCUMENT_ID.getName() + "`,`"
							+ DOCUMENT_TERM_TOPIC.TERM.getName() + "`,`"
							+ DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + "`), "
							+ " ADD KEY IDX4 (`"
							+ DOCUMENT_TERM_TOPIC.DOCUMENT_ID.getName() + "`,`"
							+ DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + "`), "
							+ " ADD KEY IDX5 (`"
							+ DOCUMENT_TERM_TOPIC.TERM.getName() + "`,`"
							+ DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + "`) ");

					System.out.println("load data into table " + this.tableName
							+ " done.");
				} else {
					System.err.println("CSV-Header not read");
					System.exit(1);
				}
			} catch (IOException e) {
				System.err.println("CSV-Header not read");
				e.printStackTrace();
				System.exit(2);
			}
		} catch (FileNotFoundException e) {
			System.err
					.println("Input CSV-File couldn't be read - maybe the path is incorrect");
			e.printStackTrace();
			System.exit(3);
		}
 MIT-JOOQ-ENDE */ 
/** OHNE_JOOQ-START */		
		System.out.println("load data into table "
				+ "DOCUMENT_TERM_TOPIC");

		try {
			inCsv = new CsvReader(new FileInputStream(
					properties.getProperty("projectRoot")
							+ properties.getProperty("InCSVFile")), ';',
					Charset.forName("UTF-8"));
			try {
				if (inCsv.readHeaders()) {
					String[] headerEntries = inCsv.getHeaders();
					String header = "";
					for (int j = 0; j < headerEntries.length; j++) {
						header += headerEntries[j] + ",";
					}
					header += "TOPIC_ID";
					database.executeUpdateQuery("LOAD DATA LOCAL INFILE '"
							+ inFilePath + "' IGNORE INTO TABLE "
							+ "DOCUMENT_TERM_TOPIC"
							+ " CHARACTER SET utf8 (" + header + " );");

					database.executeUpdateQuery("ALTER TABLE "
							+ "DOCUMENT_TERM_TOPIC"
							+ " ADD KEY IDX0 (`"
							+ "DOCUMENT_TERM_TOPIC.DOCUMENT_ID"
							+ "`,`"
							+ "DOCUMENT_TERM_TOPIC.TOPIC_ID"
							+ "`,`"
							+ "DOCUMENT_TERM_TOPIC.POSITION_OF_TOKEN_IN_DOCUMENT" 
							+ "`), " + " ADD KEY IDX1 (`"
							+ "DOCUMENT_TERM_TOPIC.DOCUMENT_ID"
							+ "`), " + " ADD KEY IDX2 (`"
							+ "DOCUMENT_TERM_TOPIC.TERM" + "`), "
							+ " ADD KEY IDX3 (`"
							+ "DOCUMENT_TERM_TOPIC.DOCUMENT_ID" + "`,`"
							+ "DOCUMENT_TERM_TOPIC.TERM" + "`,`"
							+ "DOCUMENT_TERM_TOPIC.TOPIC_ID" + "`), "
							+ " ADD KEY IDX4 (`"
							+ "DOCUMENT_TERM_TOPIC.DOCUMENT_ID" + "`,`"
							+ "DOCUMENT_TERM_TOPIC.TOPIC_ID" + "`), "
							+ " ADD KEY IDX5 (`"
							+ "DOCUMENT_TERM_TOPIC.TERM" + "`,`"
							+ "DOCUMENT_TERM_TOPIC.TOPIC_ID" + "`) ");

					System.out.println("load data into table " + this.tableName
							+ " done.");
				} else {
					System.err.println("CSV-Header not read");
					System.exit(1);
				}
			} catch (IOException e) {
				System.err.println("CSV-Header not read");
				e.printStackTrace();
				System.exit(2);
			}
		} catch (FileNotFoundException e) {
			System.err
					.println("Input CSV-File couldn't be read - maybe the path is incorrect");
			e.printStackTrace();
			System.exit(3);
		}
/** OHNE_JOOQ-ENDE */  
	}

	@Override
	public void setTableName() {
/** MIT-JOOQ-START 
		tableName = DOCUMENT_TERM_TOPIC.getName();
 MIT-JOOQ-ENDE */ 
/** OHNE_JOOQ-START */ 
		tableName = "DOCUMENT_TERM_TOPIC";
/** OHNE_JOOQ-ENDE */ 
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentTermTopicCreate");
		beforeDependencies.add("TokenTopicAssociator");
	}	
}
