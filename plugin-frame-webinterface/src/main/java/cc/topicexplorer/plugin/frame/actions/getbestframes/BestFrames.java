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
		frameMap.select.add("FRAMES.FRAME");
		frameMap.select.add("FRAME_COUNT");
		frameMap.select.add("FRAMES.FRAME_ID");
		frameMap.select.add("FRAMES.TOPIC_ID");
		frameMap.from.add("FRAMES");
		frameMap.from.add("BEST_FRAMES");
		frameMap.where.add("FRAMES.FRAME_ID = BEST_FRAMES.FRAME_ID");
		frameMap.orderBy.add("FRAMES.TOPIC_ID");
		frameMap.orderBy.add("FRAME_COUNT DESC");

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
		frameColumnList.remove("FRAME_ID");
		
		JSONObject topicData = new JSONObject();
		JSONObject frameData = new JSONObject();
		JSONObject frames = new JSONObject();
		JSONObject all = new JSONObject();
		JSONArray sorting = new JSONArray();

		ResultSet frameQueryRS = database.executeQuery(frameMap.getSQLString());
		int topicId = -1;
		while (frameQueryRS.next()) {
			if (topicId != frameQueryRS.getInt("TOPIC_ID")) {
				if (topicData.size() > 0) {
					frames.put("FRAMES", topicData);
					frames.put("SORTING", sorting);
					all.put(topicId, frames);
					topicData = new JSONObject();
					sorting = new JSONArray();
				} 
				topicId = frameQueryRS.getInt("TOPIC_ID");
			} 
			
			frameData = new JSONObject();
			for (int i = 0; i < frameColumnList.size(); i++) {
				frameData.put(frameColumnList.get(i), frameQueryRS.getString(frameColumnList.get(i)));
			}
			topicData.put(frameQueryRS.getString("FRAME_ID"), frameData);
			sorting.add(frameQueryRS.getString("FRAME_ID"));
		}
		frames = new JSONObject();
		frames.put("FRAMES", topicData);
		frames.put("SORTING", sorting);
		all.put(topicId, frames);

		outWriter.print(all.toString());
	}
}