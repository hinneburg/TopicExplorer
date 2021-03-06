package wikiParser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import tools.WikiIDTitlePair;

public class MediawikiColorationParallelisation extends Thread {

	private final List<WikiIDTitlePair> list;
	private SupporterForBothTypes s;
	private final Database db;
	private final boolean bool_transaction;
	private Integer positionInArray;

	private final String databasePreprocessing;

	public MediawikiColorationParallelisation(List<WikiIDTitlePair> list, ThreadGroup tg, String threadName,
			Properties prop) {
		super(tg, null, threadName);

		try {
			this.s = new SupporterForBothTypes(prop, true); // target-database,
															// different from
															// other
															// db, otherwise the
															// corrected
															// articles
															// were overwritten
		} catch (SQLException e) {
			System.err.println("Database supporter could not be constructed.");
			e.printStackTrace();
		} catch (InstantiationException e) {
			System.err.println("Database supporter could not be constructed.");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println("Database supporter could not be constructed.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Database supporter could not be constructed.");
			e.printStackTrace();
		}
		this.db = s.getDatabase();
		this.list = list;

		this.databasePreprocessing = prop.getProperty("database.DB");
		this.bool_transaction = prop.getProperty("Wiki_transaction").equalsIgnoreCase("true");
	}

	// aus UI kopiert und erweitert mit TOPIC_LABEL, padding,
	// databasepreprocessing, SQL-Strings angepasst
	public String getTokenTopicAssignment(int documentID, String completeText) throws SQLException {
		ResultSet rsToken;

		int tempPosition = Integer.MAX_VALUE;
		String tempToken, tempColor, tempLabel;

		// get the start-positions und assigned topics for the token in a
		// document by a given document-ID
		rsToken = db.executeQuery(" SELECT WIKI$POSITION, " + "	TOKEN , " + " COLOR_TOPIC$COLOR , "
				+ " TEXT$TOPIC_LABEL" + " FROM " + databasePreprocessing + ".DOCUMENT_TERM_TOPIC , "
				+ databasePreprocessing + ".TOPIC "
				+ " WHERE TOPIC.TOPIC_ID = DOCUMENT_TERM_TOPIC.TOPIC_ID AND DOCUMENT_ID  = " + documentID
				+ " ORDER BY POSITION_OF_TOKEN_IN_DOCUMENT DESC");
		while (rsToken.next()) {

			if (rsToken.getInt("WIKI$POSITION") > 0) {

				if (tempPosition >= rsToken.getInt("DOCUMENT_TERM_TOPIC.WIKI$POSITION")
						+ rsToken.getString("DOCUMENT_TERM_TOPIC.TOKEN").length()) {
					// get the temporary pos, token and color

					tempPosition = rsToken.getInt("DOCUMENT_TERM_TOPIC.WIKI$POSITION"); // tempPosition
																						// =
					// rsToken.getInt("Seq");
					tempToken = rsToken.getString("DOCUMENT_TERM_TOPIC.TOKEN"); // tempToken
																				// =
					// rsToken.getString("Token_Original");
					tempColor = rsToken.getString("TOPIC.COLOR_TOPIC$COLOR"); // tempColor

					tempLabel = rsToken.getString("TOPIC.TEXT$TOPIC_LABEL");

					completeText = completeText.substring(0, tempPosition) + "<span style=\"background-color:"
							+ tempColor + "; padding:0 1.5pt 0 1.5pt;\" " + "title = \"" + tempLabel + "\" >"
							+ tempToken + "</span>"
							+ completeText.substring(tempPosition + tempToken.length(), completeText.length());
				}
			}
		}
		rsToken.close();

		rsToken = null;
		tempToken = null;
		tempColor = null;
		tempLabel = null;

		return completeText;
	}

	@Override
	public void run() {
		Integer id;
		String text;
		byte[] textAsByte;

		try {

			if (bool_transaction) {
				db.executeUpdateQuery("START TRANSACTION;");
			}

			PreparedStatement stmt = db.getConnection().prepareStatement(
					"UPDATE text set old_text = ? WHERE old_id = ?");

			for (Integer i = 0; i < list.size(); i++) {
				positionInArray = i;
				id = list.get(i).getOld_id();
				text = s.getWikiTextOnlyWithID(id);
				textAsByte = getTokenTopicAssignment(id, text).getBytes();

				stmt.setBytes(1, textAsByte);
				stmt.setInt(2, id);
				stmt.addBatch();

				text = null;
				textAsByte = null;
			}
			stmt.executeBatch();
			stmt.close();

			if (bool_transaction) {
				db.executeUpdateQuery("COMMIT;");
			}
			db.shutdownDB();
		} catch (SQLException e) {
			System.err.println("Error in run - jsonoutgettoken " + list.get(positionInArray).getOld_id());
		}
	}
}
