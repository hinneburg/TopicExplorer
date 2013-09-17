package cc.topicexplorer.plugin.text.preprocessing.tables.topic;

/** MIT-JOOQ-START 
import static jooq.generated.Tables.TOPIC;
import static jooq.generated.Tables.TERM;
import static jooq.generated.Tables.TERM_TOPIC; 
 MIT-JOOQ-ENDE */

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cc.topicexplorer.chain.commands.TableFillCommand;

/**
 * angefangen von Mattes weiterverarbeitet von Gert Kommaersetzung, Pfadangabe
 * eingefügt, Tabellenname mit Jooq verknüpft
 * 
 */
public class TopicFill extends TableFillCommand {

	@Override
	public void fillTable() throws SQLException {
		try {
			this.prepareMetaDataAndFillTable();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	private void prepareMetaDataAndFillTable() throws IOException, SQLException {
		String name;

		/** MIT-JOOQ-START 
		Statement stmt = database.getCreateJooq().getConnection()
				.createStatement();

		
		ResultSet rsNames;
		for (int i = 0; i < Integer.parseInt(this.properties
				.getProperty("malletNumTopics")); i++) {

			String sql = "SELECT CONCAT((select k." + TERM.TERM_NAME.getName()
					+ " from " + TERM.getName() + " k, (SELECT "
					+ TERM_TOPIC.TOPIC_ID.getName() + ", "
					+ TERM_TOPIC.TERM_ID.getName() + " FROM `"
					+ TERM_TOPIC.getName() + "` WHERE "
					+ TERM_TOPIC.TOPIC_ID.getName() + "=" + i + " order by "
					+ TERM_TOPIC.PR_TERM_GIVEN_TOPIC.getName()
					+ " desc limit 0,1) x where k." + TERM.TERM_ID.getName()
					+ "=x." + TERM_TOPIC.TERM_ID.getName()
					+ "),', ',(select k." + TERM.TERM_NAME.getName() + " from "
					+ TERM.getName() + " k, (SELECT "
					+ TERM_TOPIC.TOPIC_ID.getName() + ", "
					+ TERM_TOPIC.TERM_ID.getName() + " FROM `"
					+ TERM_TOPIC.getName() + "` WHERE "
					+ TERM_TOPIC.TOPIC_ID.getName() + "=" + i + " order by "
					+ TERM_TOPIC.PR_TERM_GIVEN_TOPIC.getName()
					+ " desc limit 1,1) x where k." + TERM.TERM_ID.getName()
					+ "=x." + TERM_TOPIC.TERM_ID.getName()
					+ "),', ',(select k." + TERM.TERM_NAME.getName() + " from "
					+ TERM.getName() + " k, (SELECT "
					+ TERM_TOPIC.TOPIC_ID.getName() + ", "
					+ TERM_TOPIC.TERM_ID.getName() + " FROM `"
					+ TERM_TOPIC.getName() + "` WHERE "
					+ TERM_TOPIC.TOPIC_ID.getName() + "=" + i + " order by "
					+ TERM_TOPIC.PR_TERM_GIVEN_TOPIC.getName()
					+ " desc limit 2,1) x where k." + TERM.TERM_ID.getName()
					+ "=x." + TERM_TOPIC.TERM_ID.getName() + "))";
 
			rsNames = database.executeQuery(sql);

			if (rsNames.next()) {
				name = rsNames.getString(1);

				stmt.addBatch(" UPDATE " + TOPIC.getName() + " set "
						+ TOPIC.TEXT$TOPIC_LABEL.getName() + " = '"
						+ name + "' " + " WHERE "
						+ TOPIC.TOPIC_ID.getName() + " = " + i + "; ");
			} else {
				System.out.println("Error fetching name data");
				System.exit(0);
			}
		}

		System.out
				.println("Starting batch execution TopicMetaData update themen.");
		stmt.executeBatch();
MIT-JOOQ-ENDE */
		/** OHNE_JOOQ-START */ 
		Statement stmt = database.getConnection()
				.createStatement();
		ResultSet rsNames;
		for (int i = 0; i < Integer.parseInt(this.properties
				.getProperty("malletNumTopics")); i++) {

			String sql = "SELECT CONCAT((select k." + "TERM.TERM_NAME"
					+ " from " + "TERM" + " k, (SELECT "
					+ "TERM_TOPIC.TOPIC_ID" + ", "
					+ "TERM_TOPIC.TERM_ID" + " FROM `"
					+ "TERM_TOPIC" + "` WHERE "
					+ "TERM_TOPIC.TOPIC_ID" + "=" + i + " order by "
					+ "TERM_TOPIC.PR_TERM_GIVEN_TOPIC"
					+ " desc limit 0,1) x where k." + "TERM.TERM_ID"
					+ "=x." + "TERM_TOPIC.TERM_ID"
					+ "),', ',(select k." + "TERM.TERM_NAME" + " from "
					+ "TERM" + " k, (SELECT "
					+ "TERM_TOPIC.TOPIC_ID" + ", "
					+ "TERM_TOPIC.TERM_ID" + " FROM `"
					+ "TERM_TOPIC" + "` WHERE "
					+ "TERM_TOPIC.TOPIC_ID" + "=" + i + " order by "
					+ "TERM_TOPIC.PR_TERM_GIVEN_TOPIC"
					+ " desc limit 1,1) x where k." + "TERM.TERM_ID"
					+ "=x." + "TERM_TOPIC.TERM_ID"
					+ "),', ',(select k." + "TERM.TERM_NAME" + " from "
					+ "TERM" + " k, (SELECT "
					+ "TERM_TOPIC.TOPIC_ID" + ", "
					+ "TERM_TOPIC.TERM_ID" + " FROM `"
					+ "TERM_TOPIC" + "` WHERE "
					+ "TERM_TOPIC.TOPIC_ID" + "=" + i + " order by "
					+ "TERM_TOPIC.PR_TERM_GIVEN_TOPIC"
					+ " desc limit 2,1) x where k." + "TERM.TERM_ID"
					+ "=x." + "TERM_TOPIC.TERM_ID" + "))";
 
			rsNames = database.executeQuery(sql);

			if (rsNames.next()) {
				name = rsNames.getString(1);

				stmt.addBatch(" UPDATE " + "TOPIC" + " set "
						+ "TOPIC.TEXT$TOPIC_LABEL" + " = '"
						+ name + "' " + " WHERE "
						+ "TOPIC.TOPIC_ID" + " = " + i + "; ");
			} else {
				System.out.println("Error fetching name data");
				System.exit(0);
			}
		}

		System.out
				.println("Starting batch execution TopicMetaData update themen.");
		stmt.executeBatch();

		/** OHNE_JOOQ-ENDE */ 
	
	}

	@Override
	public void setTableName() {
/** MIT-JOOQ-START 
		this.tableName = TOPIC.getName();
MIT-JOOQ-ENDE */
	}
	
	@Override
	public void addDependencies() {
		beforeDependencies.add("Text_TopicCreate");
		beforeDependencies.add("TopicFill");
		beforeDependencies.add("TermTopicFill");
		beforeDependencies.add("TermFill");
	}	
}
