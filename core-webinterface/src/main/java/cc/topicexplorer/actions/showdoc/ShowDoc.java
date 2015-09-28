package cc.topicexplorer.actions.showdoc;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import cc.topicexplorer.database.Database;
import cc.topicexplorer.database.SelectMap;

public class ShowDoc {
	private static Logger logger = Logger.getLogger(ShowDoc.class);

	private SelectMap documentMap, topicMap;
	private Database database;
	private PrintWriter outWriter;
	private Properties properties;
	
	public ShowDoc(int documentId, Database db, PrintWriter out, Properties prop) {
		documentMap = new SelectMap();
		documentMap.select.add("DOCUMENT_ID");
		documentMap.from.add("DOCUMENT");
		documentMap.where.add("DOCUMENT_ID=" + documentId);
		
		topicMap = new SelectMap();
		topicMap.select.add("TOPIC_ID");
		topicMap.select.add("POSITION_OF_TOKEN_IN_DOCUMENT");
		topicMap.select.add("TOKEN");
		topicMap.from.add("DOCUMENT_TERM_TOPIC");
		topicMap.where.add("DOCUMENT_ID=" + documentId);
		topicMap.orderBy.add("POSITION_OF_TOKEN_IN_DOCUMENT DESC");
		
		setDatabase(db);
		setServletWriter(out);
		setProperties(prop);
	}
	
	public void setProperties(Properties prop) {
		this.properties = prop;
	}
	
	public void setDatabase(Database database) {
		this.database = database;
	}

	public void setServletWriter(PrintWriter servletWriter) {
		this.outWriter = servletWriter;
	}
	
	public void addDocumentColumn(String documentColumn, String documentColumnName) {
		documentMap.select.add(documentColumn + " as " + documentColumnName);
	}
	
	public void addTopicColumn(String topicColumn, String topicColumnName) {
		topicMap.select.add(topicColumn + " as " + topicColumnName);
	}
	
	public void executeQuery() throws SQLException {
		ArrayList<String> docColumnList = documentMap.getCleanColumnNames();
		ArrayList<String> topicColumnList = topicMap.getCleanColumnNames();

		JSONObject doc = new JSONObject();
		JSONObject topic = new JSONObject();
		JSONArray topics = new JSONArray();
		JSONObject all = new JSONObject();
		
		JSONObject topicListOfParents = new JSONObject();

		int docId = 0;
		long start = System.currentTimeMillis();
		try {
			ResultSet documentRS = database.executeQuery(documentMap.getSQLString());
			if (documentRS.next()) {
				for (int i = 0; i < docColumnList.size(); i++) {
					doc.put(docColumnList.get(i), documentRS.getString(docColumnList.get(i)));
				}
				docId = documentRS.getInt("DOCUMENT_ID");
			}
			documentRS.close();

			Long time = System.currentTimeMillis() - start;
			logger.info(" DocQueryTime: " + time + " ms");
			
			if (Arrays.asList(properties.get("plugins").toString().split(",")).contains("hierarchicaltopic")) {
				ArrayList<Integer> parentList = new ArrayList<Integer>();
				int leafTopicId = -1;
				ResultSet hierarchicalTopicRS = database.executeQuery("SELECT t1.TOPIC_ID, t2.TOPIC_ID AS PARENT_ID FROM TOPIC t1, TOPIC t2 "
						+ "WHERE t1.HIERARCHICAL_TOPIC$START = t1.HIERARCHICAL_TOPIC$END AND "
						+ "t1.HIERARCHICAL_TOPIC$START >= t2.HIERARCHICAL_TOPIC$START AND "
						+ "t1.HIERARCHICAL_TOPIC$END <= t2.HIERARCHICAL_TOPIC$END AND "
						+ "t2.HIERARCHICAL_TOPIC$START < t2.HIERARCHICAL_TOPIC$END ORDER BY t1.TOPIC_ID, PARENT_ID");
				while(hierarchicalTopicRS.next()) {
					if(leafTopicId != hierarchicalTopicRS.getInt("TOPIC_ID")) {
						if(parentList != null) {
							topicListOfParents.put(leafTopicId, parentList);
							parentList.clear();
						}
						leafTopicId = hierarchicalTopicRS.getInt("TOPIC_ID");
					}
					parentList.add(hierarchicalTopicRS.getInt("PARENT_ID"));
				}
				hierarchicalTopicRS.close();
				topicListOfParents.put(leafTopicId, parentList);
			}
			

			ResultSet topicRS = database.executeQuery(topicMap.getSQLString());

			while (topicRS.next()) {
				for (int i = 0; i < topicColumnList.size(); i++) {
					topic.put(topicColumnList.get(i), topicRS.getString(topicColumnList.get(i)));
				}
				if (Arrays.asList(properties.get("plugins").toString().split(",")).contains("hierarchicaltopic")) {
					topic.put("HIERARCHICAL_TOPIC$PARENT_IDS", topicListOfParents.get(topicRS.getString("TOPIC_ID")));
				}
				topics.add(topic);
			}
			topicRS.close();

			// hack: the following should happen in frame class(es):
			if (Arrays.asList(properties.get("plugins").toString().split(",")).contains("frame")) {
				JSONObject frame = new JSONObject();
				JSONObject frameTypes = new JSONObject();
				JSONArray frames = new JSONArray();
				String actFrameType = "";
				
				ResultSet frameRS = database.executeQuery("SELECT * FROM FRAME$FRAMES WHERE DOCUMENT_ID=" + docId
						+ " ORDER BY FRAME_TYPE, START_POSITION DESC");
				ResultSetMetaData frameRSMD = frameRS.getMetaData();
				String[] frameColumnNames = new String[frameRSMD.getColumnCount()];
				
				for (int i = 0; i < frameRSMD.getColumnCount(); i++) {
					frameColumnNames[i] = frameRSMD.getColumnName(i + 1);
				}
				while (frameRS.next()) {
					if(!actFrameType.equals(frameRS.getString("FRAME_TYPE"))) {
						if(!frames.isEmpty()) {
							frameTypes.put(actFrameType, frames);
							frames.clear();
						}
						actFrameType = frameRS.getString("FRAME_TYPE");
					}
					for (String frameColumnName : frameColumnNames) {
						frame.put(frameColumnName, frameRS.getString(frameColumnName));
					}
					frames.add(frame);
				}
				frameRS.close();
				frameTypes.put(actFrameType, frames);
				doc.put("FRAME_LIST", frameTypes);
			}
			// hack end
			doc.put("WORD_LIST", topics);
			all.put("DOCUMENT", doc);

		} catch (SQLException e) {
			logger.error("JSON Object could not be filled properly, due to database problems.");
			throw new RuntimeException(e);
		}
		outWriter.println(all.toString());
	}
}
