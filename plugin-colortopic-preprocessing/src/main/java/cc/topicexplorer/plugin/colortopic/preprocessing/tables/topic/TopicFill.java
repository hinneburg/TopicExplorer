package cc.topicexplorer.plugin.colortopic.preprocessing.tables.topic;

/** MIT-JOOQ-START 
 import static jooq.generated.Tables.TOPIC;
 MIT-JOOQ-ENDE */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;

import com.google.common.collect.Sets;

public class TopicFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(TopicFill.class);

	@Override
	public void fillTable() {
		try {
			this.prepareMetaDataAndFillTable();
		} catch (IOException ioEx) {
			logger.error("Problems occured while reading temp/topic_order.csv");
			throw new RuntimeException(ioEx);
		} catch (SQLException sqlEx) {
			logger.error("Table " + this.tableName + " could not be filled properly.");
			throw new RuntimeException(sqlEx);
		}
	}

	private void prepareMetaDataAndFillTable() throws IOException, SQLException {
		FileReader fr = new FileReader("temp/topic_order.csv");
		BufferedReader br = new BufferedReader(fr);

		String id;
		/**
		 * MIT-JOOQ-START Statement stmt = database.getCreateJooq().getConnection() .createStatement(); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		Statement stmt = this.database.getConnection().createStatement();
		/** OHNE_JOOQ-ENDE */
		for (int i = 0; i < Integer.parseInt(this.properties.getProperty("malletNumTopics")); i++) {

			String[] oldLine = br.readLine().split(",");

			id = Integer.toString(Integer.parseInt(oldLine[0]) - 1);

			String colour = oldLine[2].replaceAll("\"", "");

			if (colour.endsWith("FF") && colour.length() == 9) {
				colour = colour.substring(0, 7);
			}

			/**
			 * MIT-JOOQ-START stmt.addBatch(" UPDATE " + TOPIC.getName() + " set " + TOPIC.COLOR_TOPIC$COLOR.getName() +
			 * " = '" + colour + "' WHERE " + TOPIC.TOPIC_ID.getName() + " = " + id + "; "); MIT-JOOQ-ENDE
			 */
			/** OHNE_JOOQ-START */
//			@formatter:off
			stmt.addBatch(" UPDATE " + "TOPIC" + " set "
					+ "TOPIC.COLOR_TOPIC$COLOR" + " = '" + colour
					+ "' WHERE "
					+ "TOPIC.TOPIC_ID" + " = " + id + "; ");
//			@formatter:on
			/** OHNE_JOOQ-ENDE */
		}

		logger.info("Starting batch execution TopicMetaData update themen.");
		stmt.executeBatch();
		br.close();

	}

	@Override
	public void setTableName() {
		/**
		 * MIT-JOOQ-START this.tableName = TOPIC.getName(); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		this.tableName = "TOPIC";
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("TopicFill", "TopicMetaData", "ColorTopic_TopicCreate");
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
