package wikiParser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import tools.WikiIDTitlePair;

public class MediawikiColorationParallelisation extends Thread {

	private List<WikiIDTitlePair> list;
	private SupporterForBothTypes s;
	private Database db;

	private final String databasePreprocessing;

	// public JsonoutgetToken(List <WikiIDTitlePair> list )
	// {
	// this(list, null, "Thread-" + System.currentTimeMillis());
	// }

	public MediawikiColorationParallelisation(List<WikiIDTitlePair> list, ThreadGroup tg, String threadName,
			Properties prop) {
		super(tg, null, threadName);

		this.s = new SupporterForBothTypes(prop, true); // target-database,
														// different from other
														// db, otherwise the
														// corrected articles
														// were overwritten
		this.db = s.getDatabase();
		this.list = list;

		this.databasePreprocessing = prop.getProperty("database.DB");
	}

	// aus UI kopiert und erweitert mit TOPIC_LABEL, padding,
	// databasepreprocessing, SQL-Strings angepasst
	public String getTokenTopicAssignment(int documentID, String completeText) throws Exception {
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

				completeText = completeText.substring(0, tempPosition) + "<span style=\"background-color:" + tempColor
						+ "; padding:0 1.5pt 0 1.5pt;\" " + "title = \"" + tempLabel + "\" >" + tempToken + "</span>"
						+ completeText.substring(tempPosition + tempToken.length(), completeText.length());

				// // mit titel ohne padding
				// completeText = completeText.substring(0, tempPosition)
				// + "<span style=\"background-color:"
				// + tempColor
				// + ";\" "
				// + "title = \"" + tempLabel + "\" >"
				// + tempToken
				// + "</span>"
				// + completeText.substring(tempPosition
				// + tempToken.length(), completeText.length());
				//

				// // ohne titel
				// completeText = completeText.substring(0, tempPosition)
				// + "<span style=\"background-color:"
				// + tempColor
				// + ";\">"
				// + tempToken
				// + "</span>"
				// + completeText.substring(tempPosition
				// + tempToken.length(), completeText.length());
			}
		}
		rsToken.close();

		rsToken = null;
		tempToken = null;
		tempColor = null;
		tempLabel = null;

		return completeText;
	}

	// private void saveIntoCSVFile(CSVWriter writer, Integer old_id, String
	// text) throws IOException
	// {
	//
	// String [] entries = new String[2];
	// entries[0] = old_id.toString();
	// entries[1] = text;
	//
	// writer.writeNext(entries);
	// // bw.write("\""+ entries[0]+"\";\""+entries[1]+"\"|s|q|l|e|n|d|i|n|g|\n"
	// );
	// entries = null;
	// }

	@Override
	public void run() {

		// Integer id ;
		// String text;
		// byte []textAsByte ;
		//
		// try
		// {
		//
		// String fileName = this.getName()+"_einfaerbung.csv";
		//
		// CSVWriter writer = new CSVWriter(new FileWriter(fileName), ';');
		// // BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new
		// FileOutputStream(fileName), "UTF-8"));
		//
		//
		//
		// for (Integer i = 0 ; i< list.size(); i++)
		// {
		// id = list.get(i).getOld_id();
		// text = s.getWikiTextOnlyWithID(id);
		// // textAsByte = getTokenTopicAssignment(id,text).getBytes();
		//
		// System.out.println(id + "\t" + this.getName() + "\t"+
		// System.currentTimeMillis());
		//
		// saveIntoCSVFile(writer, id, getTokenTopicAssignment(id, text));
		//
		// }
		//
		//
		// writer.close();
		//
		// db.executeUpdateQuery("COMMIT;");
		// db.shutdownDB();
		// }
		// catch (Exception e)
		// {
		// e.printStackTrace();
		// System.err.println("Error in run - jsonoutgettoken ");
		// }
		//
		//
		//

		Integer id;
		String text;
		byte[] textAsByte;

		try {

			db.executeUpdateQuery("START TRANSACTION;");

			PreparedStatement stmt = db.getConnection().prepareStatement(
					"UPDATE text set old_text = ? WHERE old_id = ?");

			for (Integer i = 0; i < list.size(); i++) {
				id = list.get(i).getOld_id();
				text = s.getWikiTextOnlyWithID(id);
				textAsByte = getTokenTopicAssignment(id, text).getBytes();

				// System.out.println(id + "\t" + this.getName() + "\t"+
				// System.currentTimeMillis());

				stmt.setBytes(1, textAsByte);
				stmt.setInt(2, id);

				stmt.addBatch();

				// --> is now a batch
				// stmt.executeUpdate();
				// stmt.clearParameters();

				text = null;
				textAsByte = null;
			}
			stmt.executeBatch();
			stmt.close();

			db.executeUpdateQuery("COMMIT;");
			db.shutdownDB();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error in run - jsonoutgettoken ");
		}

	}
}
