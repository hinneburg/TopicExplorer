package cc.topicexplorer.actions.bestdocs;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cc.topicexplorer.database.Database;
import cc.topicexplorer.database.SelectMap;

public class BestDocumentsForGivenTopic {

	private SelectMap documentMap;
	private String topicId;
	private int limit, numberOfTopics;
	private PrintWriter outWriter;
	private Database database;

	public BestDocumentsForGivenTopic(String topicId, Integer limit, Database db, PrintWriter out, int numberOfTopics) {
		documentMap = new SelectMap();
		documentMap.select.add("DOCUMENT.DOCUMENT_ID");
		documentMap.select.add("DOCUMENT_TOPIC.PR_DOCUMENT_GIVEN_TOPIC");
		documentMap.from.add("DOCUMENT");
		documentMap.from.add("DOCUMENT_TOPIC");
		documentMap.where.add("DOCUMENT.DOCUMENT_ID=DOCUMENT_TOPIC.DOCUMENT_ID");
		documentMap.where.add("DOCUMENT_TOPIC.TOPIC_ID=" + topicId);
		documentMap.orderBy.add("PR_DOCUMENT_GIVEN_TOPIC DESC");
		documentMap.limit = limit;

		setTopicId(topicId);
		setLimit(limit);
		setDatabase(db);
		setLimit(limit);
		setServletWriter(out);
		setNumberOfTopics(numberOfTopics);
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public void setNumberOfTopics(Integer numberOfTopics) {
		this.numberOfTopics = numberOfTopics;
	}

	private Integer getNumberOfTopics() {
		return this.numberOfTopics;
	}

	public void setServletWriter(PrintWriter servletWriter) {
		this.outWriter = servletWriter;
	}

	public void addDocumentColumn(String documentColumn, String documentColumnName) {
		documentMap.select.add(documentColumn + " as " + documentColumnName);
	}

	public void executeQueriesAndWriteOutBestDocumentsForGivenTopic() {
		JSONArray topTopic = new JSONArray();
		JSONArray docSorting = new JSONArray();
		JSONObject doc = new JSONObject();
		JSONObject docs = new JSONObject();
		JSONObject all = new JSONObject();
		

		ArrayList<String> docColumnList = documentMap.getCleanColumnNames();
		String docId;

		try {
			ResultSet mainQueryRS = database.executeQuery(documentMap.getSQLString());
			while (mainQueryRS.next()) {
				docId = mainQueryRS.getString("DOCUMENT_ID");
				for (int i = 0; i < docColumnList.size(); i++) {
					doc.put(docColumnList.get(i), mainQueryRS.getString(docColumnList.get(i)));
				}
				ResultSet bestTopicsRS = database.executeQuery("SELECT TOPIC_ID FROM DOCUMENT_TOPIC WHERE TOPIC_ID < "
						+ getNumberOfTopics().toString() + " AND DOCUMENT_ID= " + docId
						+ " ORDER BY PR_TOPIC_GIVEN_DOCUMENT DESC LIMIT 4");
				while (bestTopicsRS.next()) {
					topTopic.add(bestTopicsRS.getInt("TOPIC_ID"));
				}
				doc.put("TOP_TOPIC", topTopic);
				docs.put(docId, doc);
				docSorting.add(docId);
				topTopic.clear();
			}
			all.put("DOCUMENT", docs);
			all.put("DOCUMENT_SORTING", docSorting);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		outWriter.print(all.toString());

	}
}
