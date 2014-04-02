package cc.topicexplorer.plugin.time.actions.getdates;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.json.JSONObject;
import cc.topicexplorer.database.Database;
import cc.topicexplorer.database.SelectMap;

public class GetDates {
	SelectMap getDatesMap;
	Database database;
	PrintWriter outWriter;
	
	public GetDates(Database db, PrintWriter out) {
		getDatesMap = new SelectMap();
		getDatesMap.select.add("TOPIC_ID");
		getDatesMap.select.add("WEEK");
		getDatesMap.select.add("WORD_COUNT");
		getDatesMap.from.add("TIME$WORDS_PER_TOPIC_PER_WEEK");
		getDatesMap.orderBy.add("TOPIC_ID");
		getDatesMap.orderBy.add("WEEK");
		setDatabase(db);
		setServletWriter(out);
	}
	
	public void setDatabase(Database database) {
		this.database = database;
	}

	public void setServletWriter(PrintWriter servletWriter) {
		this.outWriter = servletWriter;
	}
	
	public String getQueryForExecute() {
		return this.getDatesMap.getSQLString();
	}

	public void executeQuery() throws SQLException {
		JSONObject all = new JSONObject();
		JSONObject date = new JSONObject();
		JSONObject dates = new JSONObject();
		
		ResultSet dateQueryRS = database.executeQuery(getDatesMap.getSQLString());
		
		int topicId = -1; 
		while (dateQueryRS.next()) {
			if (topicId != dateQueryRS.getInt("TOPIC_ID")) {
				if (dates.size() > 0) {
					date = new JSONObject();
					date.put("TIME$WORDS_PER_WEEK", dates);
					all.put(topicId, date);
					dates = new JSONObject();
				} 
				topicId = dateQueryRS.getInt("TOPIC_ID");
			} 
			dates.put(dateQueryRS.getInt("WEEK"), dateQueryRS.getInt("WORD_COUNT"));
		}
		this.outWriter.print(all.toString());
	}
}
