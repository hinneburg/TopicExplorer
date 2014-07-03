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
	
	public ShowDoc(String documentId, Database db, PrintWriter out, Properties prop) {
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

			Long time = System.currentTimeMillis() - start;
			logger.info(" DocQueryTime: " + time + " ms");

			ResultSet topicRS = database.executeQuery(topicMap.getSQLString());

			while (topicRS.next()) {
				for (int i = 0; i < topicColumnList.size(); i++) {
					topic.put(topicColumnList.get(i), topicRS.getString(topicColumnList.get(i)));
				}
				topics.add(topic);
			}

			// hack: the following should happen in frame class(es):
			if (Arrays.asList(properties.get("plugins").toString().split(",")).contains("frame")) {
				JSONObject frame = new JSONObject();
				JSONArray frames = new JSONArray();
System.out.println("SELECT * FROM FRAMES WHERE DOCUMENT_ID=" + docId
		+ " ORDER BY START_POSITION DESC");
				ResultSet frameRS = database.executeQuery("SELECT * FROM FRAMES WHERE DOCUMENT_ID=" + docId
						+ " ORDER BY START_POSITION DESC");
				ResultSetMetaData frameRSMD = frameRS.getMetaData();
				String[] frameColumnNames = new String[frameRSMD.getColumnCount()];
				for (int i = 0; i < frameRSMD.getColumnCount(); i++) {
					frameColumnNames[i] = frameRSMD.getColumnName(i + 1);
				}
				while (frameRS.next()) {
					for (String frameColumnName : frameColumnNames) {
						frame.put(frameColumnName, frameRS.getString(frameColumnName));
					}
					frames.add(frame);
				}
				doc.put("FRAME_LIST", frames);
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
