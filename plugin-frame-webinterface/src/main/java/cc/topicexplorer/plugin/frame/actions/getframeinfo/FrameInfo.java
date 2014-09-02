package cc.topicexplorer.plugin.frame.actions.getframeinfo;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import cc.topicexplorer.database.Database;
import cc.topicexplorer.database.SelectMap;

public class FrameInfo {
	private final SelectMap frameMap;
	private PrintWriter outWriter;
	private Database database;

	public FrameInfo(Database database, PrintWriter pw, Logger logger) {
		frameMap = new SelectMap();
		frameMap.select.add("FRAME_COUNT");
		frameMap.select.add("UNIQUE_FRAME_COUNT");
		frameMap.select.add("TOPIC_ID");
		frameMap.select.add("FRAME_TYPE");
		frameMap.from.add("FRAME$TOPIC_FRAME");
		frameMap.orderBy.add("TOPIC_ID");
		frameMap.orderBy.add("FRAME_TYPE");
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

	public void getFrameInfo() throws SQLException {
ArrayList<String> frameColumnList = frameMap.getCleanColumnNames();
		
		frameColumnList.remove("TOPIC_ID");
		
		JSONObject frameData = new JSONObject();
		JSONObject frames = new JSONObject();
		JSONObject all = new JSONObject();
		JSONObject frameTypes = new JSONObject();
	
		ResultSet frameQueryRS = database.executeQuery(frameMap.getSQLString());
		int topicId = -1;
		String frameType = "";
		while (frameQueryRS.next()) {
			if(!frameType.equals(frameQueryRS.getString("FRAME_TYPE"))) {
				if(!frameType.isEmpty()) {
					frameTypes.put(frameType, frameData);
					if (topicId != frameQueryRS.getInt("TOPIC_ID")) {
						if(topicId > -1) {
							frames.put("FRAMES", frameTypes);
							all.put(topicId, frames);
							frames = new JSONObject();
							frameTypes = new JSONObject();
						}
						topicId = frameQueryRS.getInt("TOPIC_ID");
					}			
				}
				frameType = frameQueryRS.getString("FRAME_TYPE");
			}
			
			frameData = new JSONObject();
			for (int i = 0; i < frameColumnList.size(); i++) {
				frameData.put(frameColumnList.get(i), frameQueryRS.getString(frameColumnList.get(i)));
			}
		
		}
		frames = new JSONObject();
		frameTypes.put(frameType, frameData);
		frames.put("FRAMES", frameTypes);
		all.put(topicId, frames);

		outWriter.print(all.toString());
	}
}
