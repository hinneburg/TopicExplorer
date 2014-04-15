package cc.topicexplorer.actions.getterms;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import cc.topicexplorer.database.Database;
import cc.topicexplorer.database.SelectMap;

public final class AllTerms {
	private PrintWriter outWriter;
	private Database database;
	private final SelectMap databaseQuery;
	private static final Logger logger = Logger.getLogger(AllTerms.class);

	public AllTerms(Database db, PrintWriter out) {
		this.setDatabase(db);
		this.setServletWriter(out);
		this.databaseQuery = new SelectMap();
		List<String> columnNames_neededForCore = new ArrayList<String>(Arrays.asList("TERM_ID", "DOCUMENT_FREQUENCY",
				"CORPUS_FREQUENCY", "INVERSE_DOCUMENT_FREQUENCY", "CF_IDF"));
		for (String columnName : columnNames_neededForCore) {
			this.addTableColumn(columnName, columnName);
		}
		this.databaseQuery.from.add("TERM");
	}

	private void setServletWriter(PrintWriter out) {
		if (out == null) {
			logger.error("ServletWriter could not be set.");
			throw new IllegalArgumentException("PrintWriter argument must not be null.");
		}
		this.outWriter = out;
	}

	private void setDatabase(Database database) {
		if (database == null) {
			logger.error("Database could not be set.");
			throw new IllegalArgumentException("Database argument must not be null.");
		}
		this.database = database;
	}

	public void addTableColumn(String tableColumn, String tableColumnName) {
		this.databaseQuery.select.add(tableColumn + " as " + tableColumnName);
	}

	public void readAllTermsAndGenerateJson() {
		List<String> columnNamesWithoutId = new ArrayList<String>();
		JSONObject rowsWithIndex = new JSONObject();
		JSONObject row = new JSONObject();
		String term_id_columnName = "TERM_ID";
		int term_id;
		ResultSet resultSet;

		try {
			resultSet = database.executeQuery(this.databaseQuery.getSQLString());

			String columnNameTmp;
			for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
				columnNameTmp = resultSet.getMetaData().getColumnName(i + 1);
				if (!columnNameTmp.equals(term_id_columnName)) {
					columnNamesWithoutId.add(columnNameTmp);
				}
			}

			while (resultSet.next()) {
				term_id = (Integer) resultSet.getObject(term_id_columnName);
				row.put(term_id_columnName, term_id);
				for (String columnName : columnNamesWithoutId) {
					row.put(columnName, resultSet.getObject(columnName));
				}
				rowsWithIndex.put(term_id, row);
				row.clear();
			}

		} catch (SQLException e) {
			logger.error("Error in Query: " + databaseQuery.getSQLString());
			throw new RuntimeException(e);
		}

		this.outWriter.print(rowsWithIndex);
	}
}
