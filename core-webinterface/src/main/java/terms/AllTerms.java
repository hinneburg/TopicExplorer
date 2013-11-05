package terms;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import cc.topicexplorer.database.Database;
import cc.topicexplorer.database.SelectMap;

import com.google.common.util.concurrent.UncheckedExecutionException;

public final class AllTerms {

	private PrintStream printStream;
	private Database database;
	private SelectMap databaseQuery;

	public AllTerms() {
		this.databaseQuery = new SelectMap();
	}

	public void setOutputStream(PrintStream printStream) {
		this.printStream = printStream;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	public void executeQueriesAndWriteAllTerms() throws SQLException {
		if (this.database == null) {
			throw new UncheckedExecutionException(new IllegalStateException("Database has not been set, yet."));
		}
		if (this.printStream == null) {
			throw new UncheckedExecutionException(new IllegalStateException("PrintStream has not been set, yet."));
		}

		List<String> columnNames = new ArrayList<String>();
		JSONObject rowsWithIndex = new JSONObject();
		JSONObject row = new JSONObject();

		this.databaseQuery.select.add("*");
		this.databaseQuery.from.add("TERM");
		ResultSet resultSet = database.executeQuery(this.databaseQuery.getSQLString());

		for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
			columnNames.add(resultSet.getMetaData().getColumnName(i + 1));
		}
		while (resultSet.next()) {
			for (String columnName : columnNames) {
				row.put(columnName, resultSet.getObject(columnName));
			}
			rowsWithIndex.put(resultSet.getObject("TERM_ID"), row);
			row.clear();
		}
		this.printStream.print(rowsWithIndex);
	}
}
