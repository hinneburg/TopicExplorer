package cc.topicexplorer.plugin.hierarchicaltopic.preprocessing.tables.termtopic;

/** MIT-JOOQ-START 
 import static jooq.generated.Tables.TOPIC;
 import static jooq.generated.Tables.TERM_TOPIC;
 MIT-JOOQ-ENDE */
import java.sql.ResultSet;
import java.sql.SQLException;

import cc.topicexplorer.commands.TableFillCommand;

/**
 * @author angefangen von Mattes weiterverarbeitet von Gert Kommaersetzung,
 *         Pfadangabe eingefügt, Tabellenname mit Jooq verknüpft
 * 
 */
public class TermTopicFill extends TableFillCommand {

	@Override
	public void fillTable() {
		try {
/** MIT-JOOQ-START 
		ResultSet rsTopics = this.database.executeQuery("SELECT * FROM " 
				+ TOPIC.getName() + " WHERE " 
				+ TOPIC.HIERARCHICAL_TOPIC$START.getName() + "<" 
				+ TOPIC.HIERARCHICAL_TOPIC$END.getName());
		MIT-JOOQ-ENDE */
			/** OHNE_JOOQ-START */
//			@formatter:off
			ResultSet rsTopics = this.database.executeQuery("SELECT * FROM " 
					+ "TOPIC" + " WHERE " 
					+ "TOPIC.HIERARCHICAL_TOPIC$START" + "<" 
					+ "TOPIC.HIERARCHICAL_TOPIC$END");
//			@formatter:on
			/** OHNE_JOOQ-ENDE */
			rsTopics.last();
			int rowCount = rsTopics.getRow();

			int topicIds[] = new int[rowCount];
			int numTokens[] = new int[rowCount];
			int clusterStart[] = new int[rowCount];
			int clusterEnd[] = new int[rowCount];

			int i = 0;
			rsTopics.beforeFirst();
			/**
			 * MIT-JOOQ-START while(rsTopics.next()) { topicIds[i] =
			 * rsTopics.getInt(TOPIC.TOPIC_ID.getName()); numTokens[i] =
			 * rsTopics.getInt(TOPIC.NUMBER_OF_TOKENS.getName());
			 * clusterStart[i] =
			 * rsTopics.getInt(TOPIC.HIERARCHICAL_TOPIC$START.getName());
			 * clusterEnd[i] =
			 * rsTopics.getInt(TOPIC.HIERARCHICAL_TOPIC$END.getName()); i++; }
			 * 
			 * for(i = 0; i < rowCount; i++) {
			 * database.executeUpdateQuery("INSERT INTO " + TERM_TOPIC.getName()
			 * + " (" + TERM_TOPIC.TOPIC_ID.getName() + "," +
			 * TERM_TOPIC.TERM_ID.getName() + "," +
			 * TERM_TOPIC.NUMBER_OF_TOKEN_TOPIC.getName() + "," +
			 * TERM_TOPIC.PR_TOPIC_GIVEN_TERM.getName() + "," +
			 * TERM_TOPIC.PR_TERM_GIVEN_TOPIC.getName() + ") SELECT " +
			 * topicIds[i] + "," + TERM_TOPIC.TERM_ID.getName() + "," + "SUM(" +
			 * TERM_TOPIC.NUMBER_OF_TOKEN_TOPIC.getName() + ")," + "SUM(" +
			 * TERM_TOPIC.PR_TOPIC_GIVEN_TERM.getName() + ")," + "SUM(" +
			 * TERM_TOPIC.NUMBER_OF_TOKEN_TOPIC.getName() + ") / T." +
			 * TOPIC.NUMBER_OF_TOKENS.getName() + " FROM " +
			 * TERM_TOPIC.getName() + "," + TOPIC.getName() + " TB," +
			 * TOPIC.getName() + " T WHERE " + TERM_TOPIC.getName() + "." +
			 * TERM_TOPIC.TOPIC_ID.getName() + "=TB." + TOPIC.TOPIC_ID.getName()
			 * + " AND " + "TB." + TOPIC.HIERARCHICAL_TOPIC$START.getName() +
			 * "=" + "TB." + TOPIC.HIERARCHICAL_TOPIC$END.getName() + " AND " +
			 * "TB." + TOPIC.HIERARCHICAL_TOPIC$START.getName() + ">=" +
			 * clusterStart[i] + " AND " + "TB." +
			 * TOPIC.HIERARCHICAL_TOPIC$END.getName() + "<=" + clusterEnd[i] +
			 * " AND " + "T." + TOPIC.TOPIC_ID.getName() + "=" + topicIds[i] +
			 * " GROUP BY " + TERM_TOPIC.TERM_ID.getName() + ", T." +
			 * TOPIC.NUMBER_OF_TOKENS.getName());
			 * 
			 * } MIT-JOOQ-ENDE
			 */
			/** OHNE_JOOQ-START */
			while (rsTopics.next()) {
				topicIds[i] = rsTopics.getInt("TOPIC.TOPIC_ID");
				numTokens[i] = rsTopics.getInt("TOPIC.NUMBER_OF_TOKENS");
				clusterStart[i] = rsTopics.getInt("TOPIC.HIERARCHICAL_TOPIC$START");
				clusterEnd[i] = rsTopics.getInt("TOPIC.HIERARCHICAL_TOPIC$END");
				i++;
			}

			for (i = 0; i < rowCount; i++) {
//				@formatter:off
				database.executeUpdateQuery("INSERT INTO " 
						+ "TERM_TOPIC" + " (" 
						+ "TERM_TOPIC.TOPIC_ID" + ","
						+ "TERM_TOPIC.TERM_ID" + ","
						+ "TERM_TOPIC.NUMBER_OF_TOKEN_TOPIC" + ","
						+ "TERM_TOPIC.PR_TOPIC_GIVEN_TERM" + "," 
						+ "TERM_TOPIC.PR_TERM_GIVEN_TOPIC" + ") SELECT "
						+ topicIds[i] + ","
						+ "TERM_TOPIC.TERM_ID" + ","
						+ "SUM(" + "TERM_TOPIC.NUMBER_OF_TOKEN_TOPIC" + ")," 
						+ "SUM(" + "TERM_TOPIC.PR_TOPIC_GIVEN_TERM" + ")," 
						+ "SUM(" + "TERM_TOPIC.NUMBER_OF_TOKEN_TOPIC" + ") / T." 
						+ "NUMBER_OF_TOKENS"
						+ " FROM "
						+ "TERM_TOPIC" + "," 
						+ "TOPIC" + " TB,"
						+ "TOPIC" + " T WHERE "
						+ "TERM_TOPIC.TOPIC_ID" + "=TB."
						+ "TOPIC_ID" + " AND "
						+ "TB." + "HIERARCHICAL_TOPIC$START" + "="
						+ "TB." + "HIERARCHICAL_TOPIC$END" + " AND "
						+ "TB." + "HIERARCHICAL_TOPIC$START" + ">="
						+ clusterStart[i] + " AND "
						+ "TB." + "HIERARCHICAL_TOPIC$END" + "<="
						+ clusterEnd[i] + " AND "
						+ "T." + "TOPIC_ID" + "="
						+ topicIds[i]
						+ " GROUP BY " + "TERM_TOPIC.TERM_ID"
						+ ", T." + "NUMBER_OF_TOKENS");
//				@formatter:on
			}
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be filled properly.");
			throw new RuntimeException(e);
		}
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public void setTableName() {
		/**
		 * MIT-JOOQ-START this.tableName = TERM_TOPIC.getName(); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		this.tableName = "TERM";
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("TermTopicFill");
		beforeDependencies.add("HierarchicalTopic_TopicFill");
	}
}
