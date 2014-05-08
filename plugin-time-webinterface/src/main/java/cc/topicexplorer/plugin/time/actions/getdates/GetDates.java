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
		getDatesMap.select.add("BEST_WORDS");
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
		JSONObject topicData = new JSONObject();
		JSONObject weekData, clipboard;
		ResultSet dateQueryRS = database.executeQuery(getDatesMap.getSQLString());
		
		int topicId = -1; 
		while (dateQueryRS.next()) {
			if (topicId != dateQueryRS.getInt("TOPIC_ID")) {
				if (topicData.size() > 0) {
					clipboard = new JSONObject();
					clipboard.put("TIME$WORDS_PER_WEEK", topicData);
					all.put(topicId, clipboard);
					topicData = new JSONObject();
				} 
				topicId = dateQueryRS.getInt("TOPIC_ID");
			} 
			
			weekData = new JSONObject();
			weekData.put("WORD_COUNT", dateQueryRS.getInt("WORD_COUNT"));
			weekData.put("LABEL", dateQueryRS.getString("BEST_WORDS"));
			topicData.put(dateQueryRS.getLong("WEEK") * 1000L, weekData);

		}
		clipboard = new JSONObject();
		clipboard.put("TIME$WORDS_PER_WEEK", topicData);
		all.put(topicId, clipboard);
		this.outWriter.print(all.toString());
	}
}
