package cc.topicexplorer.actions.search;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import cc.topicexplorer.database.Database;
import cc.topicexplorer.database.SelectMap;

public class Search {

	private static Logger logger = Logger.getLogger(Search.class);

	private SelectMap searchMap;
	private Database database;
	private PrintWriter outWriter;
	private String searchWord;
	
	public Search(String searchWord, Database db, PrintWriter out, int limit, int offset) {
		searchMap = new SelectMap();
		searchMap.select.add("DOCUMENT.DOCUMENT_ID");
		searchMap.from.add("DOCUMENT");
		searchMap.limit = limit;
		searchMap.offset = offset;

		setDatabase(db);
		setServletWriter(out);
		setSearchWord(searchWord);
	}

	private void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	public String getSearchWord() {
		return this.searchWord;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	public void setServletWriter(PrintWriter servletWriter) {
		this.outWriter = servletWriter;
	}

	public void addSearchColumn(String documentColumn, String documentColumnName) {
		searchMap.select.add(documentColumn + " as " + documentColumnName);
	}

	public void addWhereClause(String whereClause) {
		searchMap.where.add(whereClause);
	}

	public void setOrderBy(ArrayList<String> orderBy) {
		searchMap.orderBy = orderBy;
	}

	public void executeQuery() throws SQLException {
		JSONArray topTopic = new JSONArray();
		JSONArray docSorting = new JSONArray();
		JSONObject doc = new JSONObject();
		JSONObject docs = new JSONObject();
		JSONObject all = new JSONObject();

		ArrayList<String> docColumnList = searchMap.getCleanColumnNames();
		String docId;
		
		String keywordTitle, keywordText;

		logger.info("QUERY will be executed: " + searchMap.getSQLString());
		ResultSet mainQueryRS = database.executeQuery(searchMap.getSQLString());

		while (mainQueryRS.next()) {
			docId = mainQueryRS.getString("DOCUMENT_ID");
			docSorting.add(docId);

			for (int i = 0; i < docColumnList.size(); i++) {
				doc.put(docColumnList.get(i), mainQueryRS.getString(docColumnList.get(i)));
			}

			String secondQuery = "SELECT TOPIC_ID FROM DOCUMENT_TOPIC WHERE DOCUMENT_ID= " + docId
					+ " ORDER BY PR_TOPIC_GIVEN_DOCUMENT DESC";
			logger.info("QUERY will be executed: " + secondQuery);
			ResultSet bestTopicsRS = database.executeQuery(secondQuery);

			while (bestTopicsRS.next()) {
				topTopic.add(bestTopicsRS.getInt("TOPIC_ID"));
			}

//			ResultSet reverseDocTopicRS = database.executeQuery("SELECT TOPIC_ID FROM DOCUMENT_TOPIC WHERE DOCUMENT_ID=" + mainQueryRS.getInt("DOCUMENT_ID")
//					+ " ORDER BY PR_DOCUMENT_GIVEN_TOPIC DESC LIMIT 1");
//			if (reverseDocTopicRS.next()) {
//				doc.put("TOPIC_ID", reverseDocTopicRS.getInt("TOPIC_ID"));
//			}

			doc.put("TOP_TOPIC", topTopic);
			
			keywordTitle = "";
			ResultSet keywordCountsRS = database.executeQuery("SELECT TERM, COUNT(TERM) FROM DOCUMENT_TERM_TOPIC WHERE DOCUMENT_ID="
					+ docId + " GROUP BY TERM ORDER BY COUNT(TERM) DESC LIMIT 3");
			while(keywordCountsRS.next()) {
				keywordTitle += keywordCountsRS.getString("TERM") + " ";
			}
			doc.put("KEYWORD_TITLE", keywordTitle);
			
			keywordText = "";
			ResultSet keywordPosRS = database.executeQuery("SELECT TERM FROM DOCUMENT_TERM_TOPIC WHERE DOCUMENT_ID="
					+ docId + " ORDER BY POSITION_OF_TOKEN_IN_DOCUMENT LIMIT 50");
			while(keywordPosRS.next()) {
				keywordText += keywordPosRS.getString("TERM") + " ";
			}
			doc.put("KEYWORD_SNIPPET", keywordText);
			
			docs.put(docId, doc);
			topTopic.clear();
		}

		all.put("DOCUMENT", docs);
		all.put("DOCUMENT_SORTING", docSorting);

		outWriter.print(all.toString());
	}
}
