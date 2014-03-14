package cc.topicexplorer.plugin.hierarchicaltopic.preprocessing.tables.topic;

/** MIT-JOOQ-START 
 import static jooq.generated.Tables.TOPIC;
 MIT-JOOQ-ENDE */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import cc.topicexplorer.commands.TableFillCommand;

/**
 * @author angefangen von Mattes weiterverarbeitet von Gert Kommaersetzung,
 *         Pfadangabe eingefügt, Tabellenname mit Jooq verknüpft
 * 
 */
public class TopicFill extends TableFillCommand {

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

		String[] newLine = { "", "", "" };

		String cluster[] = new String[Integer.parseInt(this.properties.getProperty("malletNumTopics"))];
		int numTokens[] = new int[Integer.parseInt(this.properties.getProperty("malletNumTopics"))];
		ResultSet rsNumTokens;
		/**
		 * MIT-JOOQ-START for (int i = 0; i < Integer.parseInt(this.properties
		 * .getProperty("malletNumTopics")); i++) {
		 * 
		 * String[] oldLine = br.readLine().split(",");
		 * 
		 * newLine[0] = Integer.toString(Integer.parseInt(oldLine[0]) - 1);
		 * newLine[1] = Integer.toString(Integer.parseInt(oldLine[1]) - 1);
		 * 
		 * cluster[Integer.parseInt(newLine[1])] = oldLine[3].replaceAll("\"",
		 * "");
		 * 
		 * database.executeUpdateQuery(" UPDATE " + TOPIC.getName() + " set " +
		 * TOPIC.HIERARCHICAL_TOPIC$START.getName() + " = " + newLine[1] + " , "
		 * + TOPIC.HIERARCHICAL_TOPIC$END.getName() + " = " + newLine[1] + " , "
		 * + TOPIC.HIERARCHICAL_TOPIC$CLUSTER_MEMBERSHIP.getName() + " = '" +
		 * oldLine[3].replaceAll("\"", "") + "' WHERE " +
		 * TOPIC.TOPIC_ID.getName() + " = " + newLine[0] + "; ");
		 * 
		 * }
		 * 
		 * rsNumTokens = database.executeQuery("SELECT * FROM " +
		 * TOPIC.getName()); while (rsNumTokens.next()) {
		 * numTokens[rsNumTokens.getInt(TOPIC.HIERARCHICAL_TOPIC$START
		 * .getName())] = rsNumTokens.getInt(TOPIC.NUMBER_OF_TOKENS .getName());
		 * } MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		for (int i = 0; i < Integer.parseInt(this.properties.getProperty("malletNumTopics")); i++) {

			String[] oldLine = br.readLine().split(",");

			newLine[0] = Integer.toString(Integer.parseInt(oldLine[0]) - 1);
			newLine[1] = Integer.toString(Integer.parseInt(oldLine[1]) - 1);

			cluster[Integer.parseInt(newLine[1])] = oldLine[3].replaceAll("\"", "");

//			@formatter:off
			database.executeUpdateQuery(" UPDATE " + "TOPIC" + " set "
						+ "TOPIC.HIERARCHICAL_TOPIC$START" + " = "
						+ newLine[1] + " , "
						+ "TOPIC.HIERARCHICAL_TOPIC$END" + " = "
						+ newLine[1] + " , "
						+ "TOPIC.HIERARCHICAL_TOPIC$CLUSTER_MEMBERSHIP"
						+ " = '" + oldLine[3].replaceAll("\"", "") + "' WHERE "
						+ "TOPIC.TOPIC_ID" + " = " + newLine[0] + "; ");
//			@formatter:on
		}

		rsNumTokens = database.executeQuery("SELECT * FROM " + "TOPIC");
		while (rsNumTokens.next()) {
			numTokens[rsNumTokens.getInt("TOPIC.HIERARCHICAL_TOPIC$START")] = rsNumTokens
					.getInt("TOPIC.NUMBER_OF_TOKENS");
		}
		/** OHNE_JOOQ-ENDE */
		addHierarchicalTopic(0, cluster, new ArrayList<Integer>(), numTokens);
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

	private void addHierarchicalTopic(int depth, String[] clusterInfos, ArrayList<Integer> oldBorders, int[] numTokens)
			throws SQLException {
		int init = Integer.parseInt(clusterInfos[0].split(";")[depth]);
		ResultSet rsBorders, rsID;
		int borderStart = 0, borderEnd = 0, oldDepth = 0, oldID = 0, numTokensFirst, numTokensSecond;
		for (int i = 1; i < Integer.parseInt(this.properties.getProperty("malletNumTopics")); i++) {
			if (init != Integer.parseInt(clusterInfos[i].split(";")[depth])) {
				if (oldBorders.contains(i)) {
					init = Integer.parseInt(clusterInfos[i].split(";")[depth]);
				} else {
					oldBorders.add(i);

					if (depth == 0) {
						borderStart = 0;
						borderEnd = Integer.parseInt(this.properties.getProperty("malletNumTopics")) - 1;
						oldDepth = -1;
					} else {
//						@formatter:off
					/** MIT-JOOQ-START 
						rsBorders = database.executeQuery("SELECT * FROM "
								+ TOPIC.getName() + " WHERE "
								+ TOPIC.HIERARCHICAL_TOPIC$START.getName()
								+ "<" + i + " AND "
								+ TOPIC.HIERARCHICAL_TOPIC$END.getName() + ">="
								+ i + " ORDER BY "
								+ TOPIC.HIERARCHICAL_TOPIC$DEPTH.getName()
								+ " DESC");
						if (rsBorders.next()) {
							borderStart = rsBorders
									.getInt(TOPIC.HIERARCHICAL_TOPIC$START
											.getName());
							borderEnd = rsBorders
									.getInt(TOPIC.HIERARCHICAL_TOPIC$END
											.getName());
							oldDepth = rsBorders
									.getInt(TOPIC.HIERARCHICAL_TOPIC$DEPTH
											.getName());
						}
					MIT-JOOQ-ENDE */
//						@formatter:on
						/** OHNE_JOOQ-START */
//						@formatter:off
						rsBorders = database.executeQuery("SELECT * FROM "
								+ "TOPIC" + " WHERE "
								+ "TOPIC.HIERARCHICAL_TOPIC$START"
								+ "<" + i + " AND "
								+ "TOPIC.HIERARCHICAL_TOPIC$END" + ">="
								+ i + " ORDER BY "
								+ "TOPIC.HIERARCHICAL_TOPIC$DEPTH"
								+ " DESC");
//						@formatter:on
						if (rsBorders.next()) {
							borderStart = rsBorders.getInt("TOPIC.HIERARCHICAL_TOPIC$START");
							borderEnd = rsBorders.getInt("TOPIC.HIERARCHICAL_TOPIC$END");
							oldDepth = rsBorders.getInt("TOPIC.HIERARCHICAL_TOPIC$DEPTH");
						}
						/** OHNE_JOOQ-ENDE */
					}
					/**
					 * MIT-JOOQ-START rsID = database.executeQuery("SELECT MAX("
					 * + TOPIC.TOPIC_ID.getName() + ") AS id FROM " +
					 * TOPIC.getName()); MIT-JOOQ-ENDE
					 */
					/** OHNE_JOOQ-START */
					rsID = database.executeQuery("SELECT MAX(" + "TOPIC.TOPIC_ID" + ") AS id FROM " + "TOPIC");
					/** OHNE_JOOQ-ENDE */
					if (rsID.next()) {
						oldID = rsID.getInt("id");
					}

					numTokensFirst = 0;
					for (int j = borderStart; j < i; j++) {
						numTokensFirst += numTokens[j];
					}

					numTokensSecond = 0;
					for (int k = i; k <= borderEnd; k++) {
						numTokensSecond += numTokens[k];
					}

					// inserts
					newTopic(oldID + 1, borderStart, i - 1, oldDepth + 1, numTokensFirst);
					newTopic(oldID + 2, i, borderEnd, oldDepth + 1, numTokensSecond);

					if (depth + 1 < clusterInfos[0].split(";").length) {
						addHierarchicalTopic(depth + 1, clusterInfos, oldBorders, numTokens);
					}
				}
			}
		}

	}

	private void newTopic(int id, int start, int end, int depth, int numToken) throws SQLException {
		/**
		 * MIT-JOOQ-START if (start < end) {
		 * database.executeUpdateQuery("INSERT INTO " + TOPIC.getName() + "(" +
		 * TOPIC.TOPIC_ID.getName() + "," + TOPIC.NUMBER_OF_TOKENS.getName() +
		 * "," + TOPIC.HIERARCHICAL_TOPIC$START.getName() + "," +
		 * TOPIC.HIERARCHICAL_TOPIC$END.getName() + "," +
		 * TOPIC.HIERARCHICAL_TOPIC$DEPTH.getName() + ") VALUES (" + id + "," +
		 * numToken + "," + start + "," + end + "," + depth + ")"); } else if
		 * (start == end) { database.executeUpdateQuery("UPDATE " +
		 * TOPIC.getName() + " SET " + TOPIC.HIERARCHICAL_TOPIC$DEPTH.getName()
		 * + "=" + depth + " WHERE " + TOPIC.HIERARCHICAL_TOPIC$START.getName()
		 * + "=" + start + " AND " + TOPIC.HIERARCHICAL_TOPIC$END.getName() +
		 * "=" + start); } MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		if (start < end) {
//			@formatter:off
			database.executeUpdateQuery("INSERT INTO " + "TOPIC" + "("
					+ "TOPIC.TOPIC_ID" + ","
					+ "TOPIC.NUMBER_OF_TOKENS" + ","
					+ "TOPIC.HIERARCHICAL_TOPIC$START" + ","
					+ "TOPIC.HIERARCHICAL_TOPIC$END" + ","
					+ "TOPIC.HIERARCHICAL_TOPIC$DEPTH" + ") VALUES ("
					+ id + "," + numToken + "," + start + ","
					+ end + "," + depth + ")");
//			@formatter:on
		} else if (start == end) {
//			@formatter:off
			database.executeUpdateQuery("UPDATE " + "TOPIC" + " SET "
					+ "TOPIC.HIERARCHICAL_TOPIC$DEPTH" + "=" + depth 
					+ " WHERE " + "TOPIC.HIERARCHICAL_TOPIC$START" + "=" + start
					+ " AND " + "TOPIC.HIERARCHICAL_TOPIC$END" + "=" + start);
//			@formatter:on
		}
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("TopicFill");
		beforeDependencies.add("TopicMetaData");
		beforeDependencies.add("HierarchicalTopic_TopicCreate");
	}
}