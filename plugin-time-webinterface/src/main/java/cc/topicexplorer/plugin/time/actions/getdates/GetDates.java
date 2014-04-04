package cc.topicexplorer.plugin.time.actions.getdates;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.json.JSONArray;
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
		getDatesMap.select.add("WORD_COUNT");
		getDatesMap.select.add("UNIX_TIMESTAMP(date_add(date_add(date_add(concat(substring(WEEK,1,4),'-01-01'), interval (8 - dayofweek(concat(substring(WEEK,1,4),'-01-01'))) % 7 DAY), interval substring(WEEK, 5) - 1 week), interval 1 day)) AS WEEK");
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
		JSONArray dates = new JSONArray();
		JSONArray tupel;
		ResultSet dateQueryRS = database.executeQuery(getDatesMap.getSQLString());
		
		int topicId = -1; 
		while (dateQueryRS.next()) {
			if (topicId != dateQueryRS.getInt("TOPIC_ID")) {
				if (dates.size() > 0) {
					date = new JSONObject();
					date.put("TIME$WORDS_PER_WEEK", dates);
					all.put(topicId, date);
					dates = new JSONArray();
				} 
				topicId = dateQueryRS.getInt("TOPIC_ID");
			} 
			tupel = new JSONArray();
			tupel.add(dateQueryRS.getLong("WEEK") * 1000L);
			tupel.add(dateQueryRS.getInt("WORD_COUNT"));
			dates.add(tupel);
		}
		this.outWriter.print(all.toString());
	}
}
