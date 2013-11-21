package cc.topicexplorer.actions.autocomplete;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import cc.topicexplorer.database.Database;
import cc.topicexplorer.database.SelectMap;

public class Autocomplete {
	SelectMap autocompleteMap;
	Database database;
	PrintWriter outWriter;

	public Autocomplete(String searchWord, Database db, PrintWriter out) {
		autocompleteMap = new SelectMap();
		autocompleteMap.select.add("TERM.TERM_ID");
		autocompleteMap.select.add("TERM.TERM_NAME");
		autocompleteMap.select.add("TERM_TOPIC.TOPIC_ID");
		autocompleteMap.select.add("TERM_TOPIC.PR_TOPIC_GIVEN_TERM");
		autocompleteMap.from.add("TERM");
		autocompleteMap.from.add("TERM_TOPIC");
		autocompleteMap.where.add("TERM_TOPIC.TERM_ID = TERM.TERM_ID");
		autocompleteMap.where.add("TERM.TERM_NAME='" + searchWord + "'");

		setDatabase(db);
		setServletWriter(out);

	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	public void setServletWriter(PrintWriter servletWriter) {
		this.outWriter = servletWriter;
	}

	public void addAutocompleteColumn(String documentColumn, String documentColumnName) {
		autocompleteMap.select.add(documentColumn + " as " + documentColumnName);
	}

	public void executeQuery() {
		try {
			ResultSet autocompleteQueryRS = database.executeQuery(autocompleteMap.getSQLString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}