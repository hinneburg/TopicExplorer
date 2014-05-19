package cc.topicexplorer.actions.init;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;

import cc.commandmanager.core.CommunicationContext;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class GenerateSQL extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;

		Properties properties = (Properties) communicationContext.get("properties");

		PrintWriter servletWriter = (PrintWriter) communicationContext.get("SERVLET_WRITER");

		SelectMap documentMap = (SelectMap) communicationContext.get("DOCUMENT_QUERY");

		SelectMap topicMap = (SelectMap) communicationContext.get("TOPIC_QUERY");

		ArrayList<String> docColumnList = documentMap.getCleanColumnNames();
		ArrayList<String> topicColumnList = topicMap.getCleanColumnNames();
		ArrayList<Integer> termList = new ArrayList<Integer>();

		JSONArray topTopic = new JSONArray();
		JSONArray reverseTopTopic = new JSONArray();
		JSONArray topTerms = new JSONArray();
		JSONArray documentSorting = new JSONArray();
		JSONArray topicSorting = new JSONArray();

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
		
		boolean firstTopic;
		try {
			preQueryRS = database.executeQuery("SELECT COUNT(*) AS COUNT FROM DOCUMENT");
			if (preQueryRS.next()) {
				// DOCUMENT
				int random = Math.round((float) Math.random() * (preQueryRS.getInt("COUNT") - documentMap.limit));
				documentMap.offset = random;
				ResultSet documentRS = database.executeQuery(documentMap.getSQLString());
				while (documentRS.next()) {
					for (int i = 0; i < docColumnList.size(); i++) {
						doc.put(docColumnList.get(i), documentRS.getString(docColumnList.get(i)));
					}
//					@formatter:off
					ResultSet docTopicRS = database
							.executeQuery("SELECT TOPIC_ID FROM DOCUMENT_TOPIC WHERE TOPIC_ID < "
									+ properties.getProperty("malletNumTopics") // ugly!
									+ " AND DOCUMENT_ID="
									+ documentRS.getInt("DOCUMENT_ID")
									+ " ORDER BY PR_TOPIC_GIVEN_DOCUMENT DESC LIMIT 4");
//					@formatter:on
					while (docTopicRS.next()) {
						topTopic.add(docTopicRS.getInt("TOPIC_ID"));
					}
					doc.put("TOP_TOPIC", topTopic);
					topTopic.clear();
//					@formatter:off
					ResultSet reverseDocTopicRS = database
							.executeQuery("SELECT TOPIC_ID FROM DOCUMENT_TOPIC WHERE TOPIC_ID < "
									+ properties.getProperty("malletNumTopics") // not
																				// more
																				// nice
									+ " AND DOCUMENT_ID="
									+ documentRS.getInt("DOCUMENT_ID")
									+ " ORDER BY PR_DOCUMENT_GIVEN_TOPIC DESC LIMIT 4");
					firstTopic = true;
//					@formatter:on
					while (reverseDocTopicRS.next()) {
						if(firstTopic) {
							doc.put("TOPIC_ID", reverseDocTopicRS.getInt("TOPIC_ID"));
							firstTopic = false;
						}
						reverseTopTopic.add(reverseDocTopicRS.getInt("TOPIC_ID"));
					}
					doc.put("REVERSE_TOP_TOPIC", reverseTopTopic);
					reverseTopTopic.clear();

					docs.put(documentRS.getString("DOCUMENT_ID"), doc);
					documentSorting.add(documentRS.getString("DOCUMENT_ID"));

				}
				all.put("DOCUMENT", docs);
				all.put("DOCUMENT_SORTING", documentSorting);
				Long time = System.currentTimeMillis() - start;
				logger.info(" DocQueryTime: " + time + " ms");
				Long start2 = System.currentTimeMillis();
				// TOPIC
				/*
				 * int topicId = -1;
				 * 
				 * topicMap.select.add("TERM_TOPIC.TERM_ID");
				 * topicMap.select.add
				 * ("TERM_TOPIC.PR_TERM_GIVEN_TOPIC As relevance");
				 * topicMap.from.add("TERM_TOPIC");
				 * topicMap.where.add("TOPIC.TOPIC_ID=TERM_TOPIC.TOPIC_ID");
				 * topicMap.orderBy.add("relevance"); ResultSet topicRS =
				 * database.executeQuery(topicMap .getSQLString()); int j = 0;
				 * while(topicRS.next()) { if(topicId !=
				 * topicRS.getInt("TOPIC_ID")) { if(topic.size() > 0) {
				 * topic.put("Top_Terms", topTerms); topTerms.clear();
				 * topics.put(topicId, topic); } for(int i = 0; i <
				 * docColumnList.size(); i++ ) {
				 * topic.put(topicColumnList.get(i),
				 * topicRS.getString(topicColumnList.get(i))); } topicId =
				 * topicRS.getInt("TOPIC_ID"); j = 0; } if(j < 20) {
				 * topTerm.put("TermId", topicRS.getString("TERM_ID"));
				 * topTerm.put("relevance", topicRS.getString("relevance"));
				 * topTerms.add(topTerm); j++; } }
				 */
				ResultSet topicRS = database.executeQuery(topicMap.getSQLString());
				while (topicRS.next()) {
					for (int i = 0; i < topicColumnList.size(); i++) {
						topic.put(topicColumnList.get(i), topicRS.getString(topicColumnList.get(i)));
					}
//					@formatter:off
					ResultSet topicTermRS = database
							.executeQuery("SELECT TERM_ID, PR_TERM_GIVEN_TOPIC FROM TERM_TOPIC WHERE TOPIC_ID="
									+ topicRS.getString("TOPIC_ID")
									+ " ORDER BY PR_TERM_GIVEN_TOPIC DESC LIMIT 20");
//					@formatter:on
					while (topicTermRS.next()) {
						topTerm.put("TermId", topicTermRS.getString("TERM_ID"));
						topTerm.put("relevance", topicTermRS.getString("PR_TERM_GIVEN_TOPIC"));
						topTerms.add(topTerm);
						termList.add(topicTermRS.getInt("TERM_ID"));
					}
					topic.put("Top_Terms", topTerms);
					topTerms.clear();
					topics.put(topicRS.getInt("TOPIC.TOPIC_ID"), topic);
					topicSorting.add(topicRS.getInt("TOPIC.TOPIC_ID"));
				}
				all.put("Topic", topics);
				all.put("TOPIC_SORTING", topicSorting);
				time = System.currentTimeMillis() - start2;
				logger.info(" TopicQueryTime: " + time + " ms");
				start2 = System.currentTimeMillis();
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
				time = System.currentTimeMillis() - start2;
				logger.info(" TermQueryTime: " + time + " ms");

				time = System.currentTimeMillis() - start;
				logger.info(" CompleteTime: " + time + " ms");

			}
		} catch (SQLException e) {
			logger.error("JSON Object could not be filled properly, due to database problems.");
			throw new RuntimeException(e);
		}
		servletWriter.print(all.toString());
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("InitCoreCollect");
	}

}
