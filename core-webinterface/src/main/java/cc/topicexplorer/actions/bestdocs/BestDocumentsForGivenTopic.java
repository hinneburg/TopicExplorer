package cc.topicexplorer.actions.bestdocs;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import cc.topicexplorer.database.Database;
import cc.topicexplorer.database.SelectMap;

public class BestDocumentsForGivenTopic {

	private static final Logger logger = Logger.getLogger(BestDocumentsForGivenTopic.class);

	private final SelectMap documentMap;
	private PrintWriter outWriter;
	private Database database;

	public BestDocumentsForGivenTopic(String topicId, Integer limit, Integer offset, Database db, PrintWriter out) {
		documentMap = new SelectMap();
		documentMap.select.add("DOCUMENT.DOCUMENT_ID");
		documentMap.select.add("DOCUMENT_TOPIC.PR_DOCUMENT_GIVEN_TOPIC");
		documentMap.select.add("DOCUMENT_TOPIC.TOPIC_ID");
		documentMap.from.add("DOCUMENT");
		documentMap.from.add("DOCUMENT_TOPIC");
		documentMap.where.add("DOCUMENT.DOCUMENT_ID=DOCUMENT_TOPIC.DOCUMENT_ID");
		documentMap.where.add("DOCUMENT_TOPIC.TOPIC_ID = " + topicId);
		documentMap.orderBy.add("PR_DOCUMENT_GIVEN_TOPIC DESC");
		documentMap.limit = limit;
		documentMap.offset = offset;

		setDatabase(db);
		setServletWriter(out);
		
	}
	
	public BestDocumentsForGivenTopic(String topicId, Integer limit, Integer offset, Database db, PrintWriter out, String term, boolean hierarchicalTopicEnabled) {		
		documentMap = new SelectMap();
		documentMap.select.add("DOCUMENT.DOCUMENT_ID");
		documentMap.select.add("DOCUMENT_TERM_TOPIC.TOPIC_ID");
		documentMap.select.add("COUNT(*) AS DOCUMENT_COUNT");
		documentMap.from.add("DOCUMENT");
		documentMap.from.add("DOCUMENT_TERM_TOPIC");
		documentMap.from.add("DOCUMENT_TOPIC");
		documentMap.where.add("DOCUMENT.DOCUMENT_ID=DOCUMENT_TOPIC.DOCUMENT_ID");
		documentMap.where.add("DOCUMENT.DOCUMENT_ID=DOCUMENT_TERM_TOPIC.DOCUMENT_ID");
		if(hierarchicalTopicEnabled) {

				String where = "DOCUMENT_TERM_TOPIC.TOPIC_ID IN ("
					+ "SELECT t1.TOPIC_ID FROM TOPIC t1, TOPIC t2 "
					+ "WHERE t1.HIERARCHICAL_TOPIC$START=t1.HIERARCHICAL_TOPIC$END AND "
					+ "t1.HIERARCHICAL_TOPIC$START>=t2.HIERARCHICAL_TOPIC$START AND "
					+ "t1.HIERARCHICAL_TOPIC$END<=t2.HIERARCHICAL_TOPIC$END AND "
					+ "t2.TOPIC_ID=" + topicId;
				
					where += ")";
				
				documentMap.where.add(where);
			
		} else {
			documentMap.where.add("DOCUMENT_TERM_TOPIC.TOPIC_ID IN (" + topicId + ")");
		}
		documentMap.where.add("DOCUMENT_TERM_TOPIC.TERM IN ('" + term + "')");
		documentMap.groupBy.add("DOCUMENT_ID");
		documentMap.groupBy.add("DOCUMENT_TERM_TOPIC.TOPIC_ID");
		documentMap.orderBy.add("DOCUMENT_COUNT DESC");
		documentMap.orderBy.add("PR_DOCUMENT_GIVEN_TOPIC DESC");
		documentMap.limit = limit;
		documentMap.offset = offset;

		setDatabase(db);
		setServletWriter(out);
	}

	public void setDatabase(Database database) {
		this.database = database;
	}


	public void setServletWriter(PrintWriter servletWriter) {
		this.outWriter = servletWriter;
	}

	public void addDocumentColumn(String documentColumn, String documentColumnName, boolean addToGroupBy) {
		documentMap.select.add(documentColumn + " as " + documentColumnName);
		if(addToGroupBy) {
			documentMap.groupBy.add(documentColumnName);
		}
	}
	
	public void addWhereClause(String where) {
		documentMap.where.add(where);
	}
	
	public void addFrom(String from) {
		documentMap.from.add(from);
	}

	public void setOrderBy(ArrayList <String> orderBy) {
		documentMap.orderBy = orderBy;
	}
	
	public void executeQueriesAndWriteOutBestDocumentsForGivenTopic() {
		JSONArray topTopic = new JSONArray();
		JSONArray docSorting = new JSONArray();
		JSONObject doc = new JSONObject();
		JSONObject docs = new JSONObject();
		JSONObject all = new JSONObject();

		ArrayList<String> docColumnList = documentMap.getCleanColumnNames();
		String docId;
		String keywordTitle, keywordText;

		try {
			ResultSet mainQueryRS = database.executeQuery(documentMap.getSQLString());
			while (mainQueryRS.next()) {
				docId = mainQueryRS.getString("DOCUMENT_ID");
				for (int i = 0; i < docColumnList.size(); i++) {
					doc.put(docColumnList.get(i), mainQueryRS.getString(docColumnList.get(i)));
				}
				ResultSet bestTopicsRS = database.executeQuery("SELECT TOPIC_ID FROM DOCUMENT_TOPIC WHERE DOCUMENT_ID= " + docId
						+ " ORDER BY PR_TOPIC_GIVEN_DOCUMENT DESC");
				while (bestTopicsRS.next()) {
					topTopic.add(bestTopicsRS.getInt("TOPIC_ID"));
				}				
				doc.put("TOP_TOPIC", topTopic);
				
				keywordTitle = "";
				ResultSet keywordCountsRS = database.executeQuery("SELECT TERM, COUNT(TERM) FROM DOCUMENT_TERM_TOPIC WHERE DOCUMENT_ID="
						+ docId + " GROUP BY TERM ORDER BY COUNT(TERM) DESC LIMIT 3");
				while(keywordCountsRS.next()) {
					keywordTitle += keywordCountsRS.getString("TERM") + " ";
				}
				doc.put("KEYWORD_TITLE", keywordTitle);
				
				keywordText = "";
				ResultSet keywordPosRS = database.executeQuery("SELECT TERM FROM DOCUMENT_TERM_TOPIC WHERE DOCUMENT_ID="
						+ docId + " ORDER BY POSITION_OF_TOKEN_IN_DOCUMENT LIMIT 50");
				while(keywordPosRS.next()) {
					keywordText += keywordPosRS.getString("TERM") + " ";
				}
				doc.put("KEYWORD_SNIPPET", keywordText);
				
				docs.put(docId, doc);
				docSorting.add(docId);
				topTopic.clear();
				
				
				
			}
			all.put("DOCUMENT", docs);
			all.put("DOCUMENT_SORTING", docSorting);
		} catch (SQLException e) {
			logger.error("Error in Query: " + documentMap.getSQLString());
			throw new RuntimeException(e);
		}
		outWriter.print(all.toString());

	}
}
