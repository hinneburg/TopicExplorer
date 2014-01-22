package cc.topicexplorer.actions.getrandomdocs;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
		SelectMap preQueryMap, innerQueryMap, mainQueryMap;
		PrintWriter servletWriter = (PrintWriter) communicationContext.get("SERVLET_WRITER");

		preQueryMap = (SelectMap) communicationContext.get("PRE_QUERY");
		innerQueryMap = (SelectMap) communicationContext.get("INNER_QUERY");
		mainQueryMap = (SelectMap) communicationContext.get("MAIN_QUERY");

		ArrayList<String> docColumnList = innerQueryMap.getCleanColumnNames();

		JSONObject doc, docTopic, docTopicColl, all;
		JSONArray docArray, docTopicArray, docTopicCollArray;

		all = new JSONObject();
		doc = new JSONObject();
		docTopic = new JSONObject();
		docTopicColl = new JSONObject();
		docArray = new JSONArray();
		docTopicArray = new JSONArray();
		docTopicCollArray = new JSONArray();

		int random, docId = -1;

		try {
			ResultSet preQueryRS = database.executeQuery(preQueryMap.getSQLString());
			if (preQueryRS.next()) {
				random = Math.round((float) Math.random() * (preQueryRS.getInt("COUNT") - innerQueryMap.limit));
				innerQueryMap.offset = random;
				mainQueryMap.from.add("(" + innerQueryMap.getSQLString() + ") x");

				try {
					ResultSet mainQueryRS = database.executeQuery(mainQueryMap.getSQLString());
					while (mainQueryRS.next()) {
						if (docId != mainQueryRS.getInt("DOCUMENT_ID")) {
							if (docTopicArray.size() > 0) {
								docTopicColl.put("DOCUMENT_ID", docId);
								docTopicColl.put("TOPIC", docTopicArray);
								docTopicCollArray.add(docTopicColl);
								docTopicColl.clear();
								docTopicArray.clear();
							}
							docId = mainQueryRS.getInt("DOCUMENT_ID");
							for (int i = 0; i < docColumnList.size(); i++) {
								doc.put(docColumnList.get(i), mainQueryRS.getString(docColumnList.get(i)));
							}
							docArray.add(doc);
						}
						docTopic.clear();
						docTopic.put("TOPIC_ID", mainQueryRS.getString("TOPIC_ID"));
						docTopic.put("PR_TOPIC_GIVEN_DOCUMENT", mainQueryRS.getString("PR_TOPIC_GIVEN_DOCUMENT"));
						docTopic.put("PR_DOCUMENT_GIVEN_TOPIC", mainQueryRS.getString("PR_DOCUMENT_GIVEN_TOPIC"));
						docTopicArray.add(docTopic);
					}
					all.put("DOCUMENT", docArray);
					all.put("DOCUMENT_TOPIC", docTopicCollArray);
					servletWriter.println(all.toString());
				} catch (SQLException e) {
					logger.error("Error in Query: " + mainQueryMap.getSQLString());
					throw new RuntimeException(e);
				}
			}
		} catch (SQLException e) {
			logger.error("Error in Query: " + preQueryMap.getSQLString());
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("GetRandomDocsCoreCollect");
	}
}
