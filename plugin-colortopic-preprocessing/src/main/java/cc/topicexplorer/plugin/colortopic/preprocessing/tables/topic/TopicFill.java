package cc.topicexplorer.plugin.colortopic.preprocessing.tables.topic;
/** MIT-JOOQ-START 
import static jooq.generated.Tables.TOPIC;
MIT-JOOQ-ENDE */ 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;


import cc.topicexplorer.chain.commands.TableFillCommand;

/**
 * angefangen von Mattes weiterverarbeitet von Gert Kommaersetzung, Pfadangabe
 * eingefügt, Tabellenname mit Jooq verknüpft
 * 
 */
public class TopicFill extends TableFillCommand {

	public void fillTable() throws SQLException {
		try {
			this.prepareMetaDataAndFillTable();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

	private void prepareMetaDataAndFillTable() throws IOException, SQLException {
		FileReader fr = new FileReader(properties.getProperty("projectRoot")
				+ "temp/topic_order.csv");
		BufferedReader br = new BufferedReader(fr);

		String id;
		/** MIT-JOOQ-START 
		Statement stmt = database.getCreateJooq().getConnection()
				.createStatement();
		MIT-JOOQ-ENDE */ 
		/** OHNE_JOOQ-START */ 
		Statement stmt = this.database.getConnection().createStatement();
		/** OHNE_JOOQ-ENDE */ 
		for (int i = 0; i < Integer.parseInt(this.properties
				.getProperty("malletNumTopics")); i++) {
		
			String[] oldLine = br.readLine().split(",");

			id = Integer.toString(Integer.parseInt(oldLine[0]) - 1);

			String colour = oldLine[2].replaceAll("\"", "");

			if (colour.endsWith("FF") && colour.length() == 9) {
				colour = colour.substring(0, 7);
			}
		
			/** MIT-JOOQ-START 
			stmt.addBatch(" UPDATE " + TOPIC.getName() + " set "
						+ TOPIC.COLOR_TOPIC$COLOR.getName() + " = '" + colour
						+ "' WHERE "
						+ TOPIC.TOPIC_ID.getName() + " = " + id + "; ");
			MIT-JOOQ-ENDE */ 	
			/** OHNE_JOOQ-START */ 
			stmt.addBatch(" UPDATE " + "TOPIC" + " set "
					+ "TOPIC.COLOR_TOPIC$COLOR" + " = '" + colour
					+ "' WHERE "
					+ "TOPIC.TOPIC_ID" + " = " + id + "; ");
			/** OHNE_JOOQ-ENDE */ 
		}

		System.out
				.println("Starting batch execution TopicMetaData update themen.");
		stmt.executeBatch();
		br.close();
		
	}

	@Override
	public void setTableName() {
	/** MIT-JOOQ-START 
		this.tableName = TOPIC.getName();
	MIT-JOOQ-ENDE */ 
	/** OHNE_JOOQ-START */ 	
		this.tableName = "TOPIC";
	/** OHNE_JOOQ-ENDE */ 
	}
	
	@Override
	public void addDependencies() {
		beforeDependencies.add("TopicFill");
		beforeDependencies.add("TopicMetaData");
		beforeDependencies.add("ColorTopic_TopicCreate");
	}	
}
