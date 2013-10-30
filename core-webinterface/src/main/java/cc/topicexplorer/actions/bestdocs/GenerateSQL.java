package cc.topicexplorer.actions.bestdocs;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class GenerateSQL extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;

		Properties properties = (Properties) communicationContext.get("properties");

		PrintWriter servletWriter = (PrintWriter) communicationContext.get("SERVLET_WRITER");

		SelectMap documentMap = (SelectMap) communicationContext.get("DOCUMENT_QUERY");

		ArrayList<String> docColumnList = documentMap.getCleanColumnNames();

		documentMap.select.add("DOCUMENT_TOPIC.PR_DOCUMENT_GIVEN_TOPIC");

		JSONArray topTopic = new JSONArray();

		JSONObject doc = new JSONObject();
		JSONObject docs = new JSONObject();
		JSONObject all = new JSONObject();

		String docId;

		try {
			ResultSet mainQueryRS = database.executeQuery(documentMap.getSQLString());
			while (mainQueryRS.next()) {
				docId = mainQueryRS.getString("DOCUMENT_ID");
				for (int i = 0; i < docColumnList.size(); i++) {
					doc.put(docColumnList.get(i), mainQueryRS.getString(docColumnList.get(i)));
				}
				ResultSet bestTopicsRS = database.executeQuery("SELECT TOPIC_ID FROM DOCUMENT_TOPIC WHERE TOPIC_ID < "
						+ properties.getProperty("malletNumTopics") + " AND DOCUMENT_ID= " + docId
						+ " ORDER BY PR_TOPIC_GIVEN_DOCUMENT DESC LIMIT 4");
				while (bestTopicsRS.next()) {
					topTopic.add(bestTopicsRS.getInt("TOPIC_ID"));
				}
				doc.put("TOP_TOPIC", topTopic);
				docs.put(docId, doc);
				topTopic.clear();
			}
			all.put("DOCUMENT", docs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		servletWriter.print(all.toString());
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("BestDocsCoreCollect");
	}

}