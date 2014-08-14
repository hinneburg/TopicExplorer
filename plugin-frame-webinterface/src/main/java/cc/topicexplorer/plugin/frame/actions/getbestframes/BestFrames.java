package cc.topicexplorer.plugin.frame.actions.getbestframes;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import cc.topicexplorer.database.Database;
import cc.topicexplorer.database.SelectMap;

public class BestFrames {
	private final SelectMap frameMap;
	private PrintWriter outWriter;
	private Database database;

	public BestFrames(Database database, PrintWriter pw, Logger logger) {
		frameMap = new SelectMap();
		frameMap.select.add("FRAME AS ITEM_NAME");
		frameMap.select.add("FRAME_COUNT AS ITEM_COUNT");
		frameMap.select.add("TOPIC_ID");
		frameMap.select.add("FRAME_TYPE");
		frameMap.from.add("FRAME$BEST_FRAMES");
		frameMap.orderBy.add("TOPIC_ID");
		frameMap.orderBy.add("FRAME_TYPE");
		frameMap.orderBy.add("ITEM_COUNT DESC");

		this.setDatabase(database);
		this.setServletWriter(pw);
	}

	public SelectMap getFrameMap() {
		return this.frameMap;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	public void setServletWriter(PrintWriter servletWriter) {
		this.outWriter = servletWriter;
	}

	public void addFrameColumn(String frameColumn, String frameColumnName) {
		frameMap.select.add(frameColumn + " as " + frameColumnName);
	}

	public void getFrames() throws SQLException {
		ArrayList<String> frameColumnList = frameMap.getCleanColumnNames();
		
		frameColumnList.remove("TOPIC_ID");
		frameColumnList.remove("FRAME_TYPE");
		
		JSONObject topicData = new JSONObject();
		JSONObject frameData = new JSONObject();
		JSONObject frames = new JSONObject();
		JSONObject frameTypes = new JSONObject();
		JSONObject all = new JSONObject();
		JSONArray sorting = new JSONArray();

		ResultSet frameQueryRS = database.executeQuery(frameMap.getSQLString());
		int topicId = -1;
		String frameType = "";
		int counter = 0;
		while (frameQueryRS.next()) {
			if (!frameType.equals(frameQueryRS.getString("FRAME_TYPE"))) {
				if (frameType.length() > 0) {
					topicData.put("SORTING", sorting);
					frames.put(frameType, topicData);
					if(topicId != frameQueryRS.getInt("TOPIC_ID")) {
						frameTypes.put("ITEMS", frames);
						all.put(topicId, frameTypes);
						frames = new JSONObject();
						frameTypes = new JSONObject();
					}
					
					topicData = new JSONObject();
					sorting = new JSONArray();
					counter = 0;
				}
				frameType = frameQueryRS.getString("FRAME_TYPE");
				topicId = frameQueryRS.getInt("TOPIC_ID");
			} 
			
			frameData = new JSONObject();
			for (int i = 0; i < frameColumnList.size(); i++) {
				frameData.put(frameColumnList.get(i), frameQueryRS.getString(frameColumnList.get(i)));
			}
			topicData.put(counter, frameData);
			sorting.add(counter);
			counter ++;
		}
		topicData.put("SORTING", sorting);
		frames.put(frameType, topicData);
		frameTypes.put("ITEMS", frames);
		all.put(topicId, frameTypes);

		outWriter.print(all.toString());
	}
}
