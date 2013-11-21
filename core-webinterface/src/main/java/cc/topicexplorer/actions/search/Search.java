package cc.topicexplorer.actions.search;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import cc.topicexplorer.database.Database;
import cc.topicexplorer.database.SelectMap;

public class Search {
	SelectMap searchMap;
	Database database;
	PrintWriter outWriter;
	String searchWord;

	public Search(String searchWord, Database db, PrintWriter out) {
		searchMap = new SelectMap();
		searchMap.select.add("DOCUMENT.DOCUMENT_ID");
		searchMap.select.add("DOCUMENT_TOPIC.TOPIC_ID");
		searchMap.select.add("DOCUMENT_TOPIC.PR_DOCUMENT_GIVEN_TOPIC");
		searchMap.from.add("DOCUMENT");
		searchMap.from.add("DOCUMENT_TOPIC");
		searchMap.where.add("DOCUMENT.DOCUMENT_ID = DOCUMENT_TOPIC.DOCUMENT_ID");

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

	public void executeQuery() {
		try {
			ResultSet autocompleteQueryRS = database.executeQuery(searchMap.getSQLString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}