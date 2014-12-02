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
		searchMap.select.add("SUBSTR(DOCUMENT.TEXT, 150) AS KEYWORD_SNIPPET");
		searchMap.select.add("DOCUMENT.TITLE AS KEYWORD_TITLE");
		searchMap.select.add("CONCAT('[',DOCUMENT.BEST_TOPICS,']') AS TOP_TOPIC");
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
	
	public ArrayList<String> getOrderBy() {
		return searchMap.orderBy;
	}

	public void executeQuery() throws SQLException {
		JSONArray docSorting = new JSONArray();
		JSONObject doc = new JSONObject();
		JSONObject docs = new JSONObject();
		JSONObject all = new JSONObject();

		ArrayList<String> docColumnList = searchMap.getCleanColumnNames();
		String docId;
		
		logger.info("QUERY will be executed: " + searchMap.getSQLString());
		ResultSet mainQueryRS = database.executeQuery(searchMap.getSQLString());

		while (mainQueryRS.next()) {
			docId = mainQueryRS.getString("DOCUMENT_ID");
			docSorting.add(docId);

			for (int i = 0; i < docColumnList.size(); i++) {
				doc.put(docColumnList.get(i), mainQueryRS.getString(docColumnList.get(i)));
			}
			docs.put(docId, doc);
		}

		all.put("DOCUMENT", docs);
		all.put("DOCUMENT_SORTING", docSorting);

		outWriter.print(all.toString());
	}
}
