package cc.topicexplorer.actions.showdoc;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class GenerateSQL extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;

		Properties properties = (Properties) communicationContext
				.get("properties");

		PrintWriter servletWriter = (PrintWriter) communicationContext
				.get("SERVLET_WRITER");

		SelectMap documentMap = (SelectMap) communicationContext
				.get("DOCUMENT_QUERY");

		SelectMap topicMap = (SelectMap) communicationContext
				.get("TOPIC_QUERY");

		ArrayList<String> docColumnList = documentMap.getCleanColumnNames();
		ArrayList<String> topicColumnList = topicMap.getCleanColumnNames();
		ArrayList<Integer> termList = new ArrayList<Integer>();

		JSONArray topTopic = new JSONArray();
		JSONArray reverseTopTopic = new JSONArray();
		JSONArray topTerms = new JSONArray();

		JSONObject doc = new JSONObject();
		JSONObject docs = new JSONObject();
		JSONObject topic = new JSONObject();
		JSONObject topics = new JSONObject();
		JSONObject topTerm = new JSONObject();
		JSONObject term = new JSONObject();
		JSONObject terms = new JSONObject();
		JSONObject all = new JSONObject();
		ResultSet preQueryRS;

		long start = System.currentTimeMillis();
		try {
			ResultSet documentRS = database.executeQuery(documentMap
					.getSQLString());
			if (documentRS.next()) {
				for (int i = 0; i < docColumnList.size(); i++) {
						doc.put(docColumnList.get(i),
								documentRS.getString(docColumnList.get(i)));
				}
			}
			all.put("DOCUMENT", doc);
				Long time = System.currentTimeMillis() - start;
				logger.info(" DocQueryTime: " + time + " ms");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		servletWriter.println(all.toString());
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("ShowDocCoreCollect");
	}

}
