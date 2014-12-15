package cc.topicexplorer.plugin.frame.actions.getframes;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import cc.topicexplorer.database.Database;
import cc.topicexplorer.database.SelectMap;

public class Frames {
	private final SelectMap frameMap;
	private PrintWriter outWriter;
	private Database database;

	public Frames(Database database, PrintWriter pw, Logger logger, int topicId, String frameType, int offset, int limit) {
		frameMap = new SelectMap();
		frameMap.select.add("FRAME AS ITEM_NAME");
		frameMap.select.add("COUNT( DISTINCT DOCUMENT_ID ) AS ITEM_COUNT");
		frameMap.select.add("FRAME$FRAMES.TOPIC_ID");
		frameMap.select.add("FRAME$FRAMES.ACTIVE");
		frameMap.from.add("FRAME$FRAMES");
		frameMap.where.add("TOPIC_ID=" + topicId);
		frameMap.where.add("FRAME_TYPE like \"" + frameType + "\"");
		frameMap.where.add("ACTIVE=1");
		frameMap.groupBy.add("ITEM_NAME");
		frameMap.orderBy.add("ITEM_COUNT DESC");
		frameMap.limit = limit;
		frameMap.offset = offset;

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
		
		JSONObject topicData = new JSONObject();
		JSONObject frameData = new JSONObject();
		JSONObject frames = new JSONObject();
		JSONObject all = new JSONObject();
		JSONArray sorting = new JSONArray();

		ResultSet frameQueryRS = database.executeQuery(frameMap.getSQLString());
		System.out.println(frameMap.getSQLString());
		int topicId = -1;
		int counter = frameMap.offset;
		while (frameQueryRS.next()) {
			if (topicId != frameQueryRS.getInt("TOPIC_ID")) {
				if (topicData.size() > 0) {
					frames.put("FRAMES", topicData);
					frames.put("SORTING", sorting);
					all.put(topicId, frames);
					topicData = new JSONObject();
					sorting = new JSONArray();
					counter = frameMap.offset;
				} 
				topicId = frameQueryRS.getInt("TOPIC_ID");
			} 
			
			frameData = new JSONObject();
			for (int i = 0; i < frameColumnList.size(); i++) {
				frameData.put(frameColumnList.get(i), frameQueryRS.getString(frameColumnList.get(i)));
			}
			topicData.put(counter, frameData);
			sorting.add(counter);
			counter++;
		}
		frames = new JSONObject();
		frames.put("FRAMES", topicData);
		frames.put("SORTING", sorting);
		all.put(topicId, frames);

		outWriter.print(all.toString());
	}
}
