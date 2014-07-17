package cc.topicexplorer.plugin.wordtype.actions.getbestterms;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import cc.topicexplorer.database.Database;
import cc.topicexplorer.database.SelectMap;

public class BestTerms {
	private final SelectMap besTermsMap;
	private PrintWriter outWriter;
	private Database database;

	public BestTerms(Database database, PrintWriter pw, Logger logger) {
		besTermsMap = new SelectMap();
		besTermsMap.select.add("TERM_ID");
		besTermsMap.select.add("TERM_NAME");
		besTermsMap.select.add("NUMBER_OF_DOCUMENT_TOPIC");
		besTermsMap.select.add("TOPIC_ID");
		besTermsMap.select.add("WORDTYPE");
		besTermsMap.from.add("WORDTYPE$BEST_TERMS");
		besTermsMap.orderBy.add("TOPIC_ID");
		besTermsMap.orderBy.add("WORDTYPE");
		besTermsMap.orderBy.add("NUMBER_OF_DOCUMENT_TOPIC DESC");

		this.setDatabase(database);
		this.setServletWriter(pw);
	}

	public SelectMap getBestTermsMap() {
		return this.besTermsMap;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	public void setServletWriter(PrintWriter servletWriter) {
		this.outWriter = servletWriter;
	}

	public void addBestTermsColumn(String bestTermsColumn, String bestTermsColumnName) {
		besTermsMap.select.add(bestTermsColumn + " as " + bestTermsColumnName);
	}

	public void getBestTerms() throws SQLException {
		ArrayList<String> bestTermsColumnList = besTermsMap.getCleanColumnNames();
		
		bestTermsColumnList.remove("TOPIC_ID");
		bestTermsColumnList.remove("WORDTYPE");
		
		JSONObject topicData = new JSONObject();
		JSONObject bestTermsData = new JSONObject();
		JSONObject bestTerms = new JSONObject();
		JSONObject wordTypes = new JSONObject();
		JSONObject all = new JSONObject();
		JSONArray sorting = new JSONArray();

		ResultSet termQueryRS = database.executeQuery(besTermsMap.getSQLString());
		System.out.println(besTermsMap.getSQLString());
		int topicId = -1;
		String wordType = "";
		while (termQueryRS.next()) {
			if (!wordType.equals(termQueryRS.getString("WORDTYPE"))) {
				if (wordType.length() > 0) {
					topicData.put("SORTING", sorting);
					bestTerms.put(wordType, topicData);
					if(topicId != termQueryRS.getInt("TOPIC_ID")) {
						wordTypes.put("WORDTYPES", bestTerms);
						all.put(topicId, wordTypes);
						bestTerms = new JSONObject();
						wordTypes = new JSONObject();
					}
					
					topicData = new JSONObject();
					sorting = new JSONArray();
				}
				wordType = termQueryRS.getString("WORDTYPE");
				topicId = termQueryRS.getInt("TOPIC_ID");
			} 
			
			bestTermsData = new JSONObject();
			for (int i = 0; i < bestTermsColumnList.size(); i++) {
				bestTermsData.put(bestTermsColumnList.get(i), termQueryRS.getString(bestTermsColumnList.get(i)));
			}
			topicData.put(termQueryRS.getString("TERM_ID"), bestTermsData);
			sorting.add(termQueryRS.getString("TERM_ID"));
		}
		topicData.put("SORTING", sorting);
		bestTerms.put(wordType, topicData);
		wordTypes.put("WORDTYPES", bestTerms);
		all.put(topicId, wordTypes);

		outWriter.print(all.toString());
	}
}
