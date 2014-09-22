package cc.topicexplorer.actions.gettopics;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cc.topicexplorer.actions.bestdocs.BestDocumentsForGivenTopic;
import cc.topicexplorer.database.Database;
import cc.topicexplorer.database.SelectMap;

public class GetTopics {
	private final SelectMap topicMap, selectedTopicsMap;
	private PrintWriter outWriter;
	private Database database;
	
	private static final Logger logger = Logger.getLogger(BestDocumentsForGivenTopic.class);

	public GetTopics(Database db, PrintWriter out) {
		topicMap = new SelectMap();
		topicMap.select.add("TOPIC.TOPIC_ID");
		topicMap.from.add("TOPIC");

		selectedTopicsMap = new SelectMap();
		selectedTopicsMap.select.add("TOPIC.TOPIC_ID");
		selectedTopicsMap.from.add("TOPIC");
		
		setDatabase(db);
		setServletWriter(out);
	}
	
	public void setDatabase(Database database) {
		this.database = database;
	}
	
	public void setServletWriter(PrintWriter servletWriter) {
		this.outWriter = servletWriter;
	}

	public void addTopicColumn(String documentColumn, String documentColumnName) {
		topicMap.select.add(documentColumn + " as " + documentColumnName);
	}
	
	public void addWhereClause(String where) {
		topicMap.where.add(where);
	}
	
	public void addSelectedTopicWhereClause(String where) {
		selectedTopicsMap.where.add(where);
	}
	
	public void addOrderBy(String orderBy) {
		topicMap.orderBy.add(orderBy);
	}
	
	public void addSelectedTopicOrderBy(String orderBy) {
		selectedTopicsMap.orderBy.add(orderBy);
	}
	
	public void executeQueriesAndWriteOutTopics(int topicBestItemLimit, boolean hierarchicalTopicsEnabled) {
		ArrayList<String> topicColumnList = topicMap.getCleanColumnNames();
		ArrayList<Integer> termList = new ArrayList<Integer>();

		JSONArray topTerms = new JSONArray();
		JSONArray topicSorting = new JSONArray();

		JSONObject topic = new JSONObject();
		JSONObject topics = new JSONObject();
		JSONObject topTerm = new JSONObject();
		JSONObject term = new JSONObject();
		JSONObject terms = new JSONObject();
		JSONObject all = new JSONObject();


		try {
	
			ResultSet topicRS = database.executeQuery(topicMap.getSQLString());
			while (topicRS.next()) {
				for (int i = 0; i < topicColumnList.size(); i++) {
					topic.put(topicColumnList.get(i), topicRS.getString(topicColumnList.get(i)));
				}
	//			@formatter:off
				ResultSet topicTermRS = database
						.executeQuery("SELECT TERM_ID, NUMBER_OF_DOCUMENT_TOPIC FROM TERM_TOPIC WHERE TOPIC_ID="
								+ topicRS.getString("TOPIC_ID")
								+ " ORDER BY NUMBER_OF_DOCUMENT_TOPIC DESC LIMIT " 
								+ topicBestItemLimit);
	//			@formatter:on
				while (topicTermRS.next()) {
					topTerm.put("TermId", topicTermRS.getString("TERM_ID"));
					topTerm.put("relevance", topicTermRS.getString("NUMBER_OF_DOCUMENT_TOPIC"));
					topTerms.add(topTerm);
					termList.add(topicTermRS.getInt("TERM_ID"));
				}
				topic.put("Top_Terms", topTerms);
				topTerms.clear();
				topics.put(topicRS.getInt("TOPIC.TOPIC_ID"), topic);
				
				
			}
			
			ResultSet selectedTopicRS = database.executeQuery(selectedTopicsMap.getSQLString());
			while (selectedTopicRS.next()) {
				topicSorting.add(selectedTopicRS.getInt("TOPIC.TOPIC_ID"));
			}
			all.put("Topic", topics);
			all.put("TOPIC_SORTING", topicSorting);


			// TERM
			logger.info(termList.size());
			ResultSet termRS = database.executeQuery("SELECT TERM_ID, TERM_NAME FROM TERM where TERM_ID IN ("
					+ StringUtils.join(termList.toArray(new Integer[termList.size()]), ",") + ")");
			while (termRS.next()) {
				term.put("TERM_ID", termRS.getString("TERM_ID"));
				term.put("TERM_NAME", termRS.getString("TERM_NAME"));
				terms.put(termRS.getString("TERM_ID"), term);
			}
			all.put("Term", terms);

		} catch (SQLException e) {
			logger.error("JSON Object could not be filled properly, due to database problems.");
			throw new RuntimeException(e);
		}
		outWriter.print(all.toString());
	}

}
