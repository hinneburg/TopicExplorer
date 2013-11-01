/**
 * API for GetRandomDocuments action
 * 
 * The action picks documents randomly from the database and joins the related topics to them. 
 * 
 */
package cc.topicexplorer.actions.getrandomdocs;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.stringtemplate.v4.ST;

import cc.topicexplorer.database.Database;
import cc.topicexplorer.database.SelectMap;

import com.google.common.base.Joiner;

public class GetRandomDocuments {

	private SelectMap innerQuery;
	private ST mainQuery = new ST("SELECT " + "<DocumentAttributeList>," + "y.TOPIC_ID,"
			+ "y.PR_TOPIC_GIVEN_DOCUMENT," + "y.PR_DOCUMENT_GIVEN_TOPIC "
			+ "FROM (<InnerQuery>) x, DOCUMENT_TOPIC y " + "WHERE x.DOCUMENT_ID=y.DOCUMENT_ID "
			+ "ORDER BY x.DOCUMENT_ID, y.TOPIC_ID");

	private static String preQuery = "SELECT COUNT(*) AS COUNT FROM DOCUMENT";
	private PrintWriter out;
	private Database databaseConnection;
	private Logger logger;

	public GetRandomDocuments(Integer NumberOfRandomDocuments) {
		innerQuery = new SelectMap();

		innerQuery.select.add("DOCUMENT.DOCUMENT_ID");
		innerQuery.from.add("DOCUMENT");
		innerQuery.limit = NumberOfRandomDocuments;

	}

	public void addDocumentAttributeToSelectClause(String attribute) {
		innerQuery.select.add(attribute);
	}

	public PrintWriter getPrintWriter() {
		return this.out;
	}

	public void setPrintWriter(PrintWriter out) {
		this.out = out;
	}

	Database getDatbaseConnection() {
		return this.databaseConnection;
	}

	public void setDatabase(Database databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	private String generateDocumentAttributeListForMainQuery() {
		ArrayList<String> documentAttributeList = innerQuery.getCleanColumnNames();
		for (int i = 0; i < documentAttributeList.size(); i++) {
			documentAttributeList.set(i, "x." + documentAttributeList.get(i));
		}
		return Joiner.on(",").join(documentAttributeList);
	}

	public String getMainQuery() {
		mainQuery.add("InnerQuery", innerQuery.getSQLString());
		mainQuery.add("DocumentAttributeList", generateDocumentAttributeListForMainQuery());
		return mainQuery.render();
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void excuteQueriesAndWriteOutJsonWithRandomDocuments() {

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
			ResultSet preQueryRS = databaseConnection.executeQuery(preQuery);

			if (!preQueryRS.next()) {
				random = Math.round((float) Math.random() * (preQueryRS.getInt("COUNT") - innerQuery.limit));
				innerQuery.offset = random;

				try {
					ResultSet mainQueryRS = databaseConnection.executeQuery(getMainQuery());
					ArrayList<String> docColumnList = innerQuery.getCleanColumnNames();

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
					out.println(all.toString());
				} catch (SQLException e) {
					logger.fatal("Error in Query: " + getMainQuery());
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			logger.fatal("Error in Query: " + preQuery);
			e.printStackTrace();
		}

	}
}
