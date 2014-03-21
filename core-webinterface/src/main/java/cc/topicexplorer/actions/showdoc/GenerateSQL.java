package cc.topicexplorer.actions.showdoc;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.chain.Context;

import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class GenerateSQL extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;

		PrintWriter servletWriter = (PrintWriter) communicationContext.get("SERVLET_WRITER");
		SelectMap documentMap = (SelectMap) communicationContext.get("DOCUMENT_QUERY");
		SelectMap topicMap = (SelectMap) communicationContext.get("TOPIC_QUERY");

		ArrayList<String> docColumnList = documentMap.getCleanColumnNames();
		ArrayList<String> topicColumnList = topicMap.getCleanColumnNames();

		JSONObject doc = new JSONObject();
		JSONObject topic = new JSONObject();
		JSONArray topics = new JSONArray();
		JSONObject all = new JSONObject();

		long start = System.currentTimeMillis();
		try {
			ResultSet documentRS = database.executeQuery(documentMap.getSQLString());
			if (documentRS.next()) {
				for (int i = 0; i < docColumnList.size(); i++) {
					doc.put(docColumnList.get(i), documentRS.getString(docColumnList.get(i)));
				}
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
			doc.put("WORD_LIST", topics);
			all.put("DOCUMENT", doc);
			
		} catch (SQLException e) {
			logger.error("JSON Object could not be filled properly, due to database problems.");
			throw new RuntimeException(e);
		}
		servletWriter.println(all.toString());
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("ShowDocCoreCollect");
	}

}
