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
	private final SelectMap bestTermsMap;
	private PrintWriter outWriter;
	private Database database;
	
	public BestTerms(Database database, PrintWriter pw, Logger logger, String wordtypes[]) {
		String wordtypeInString = "WORDTYPE IN ('" + wordtypes[0] + "'";
		for(int i = 1; i < wordtypes.length; i++) {
			wordtypeInString += ",'" + wordtypes[i] + "'"; 
		}
		wordtypeInString += ")";
		bestTermsMap = new SelectMap();
		bestTermsMap.select.add("TERM_ID AS ITEM_ID");
		bestTermsMap.select.add("TERM_NAME AS ITEM_NAME");
		bestTermsMap.select.add("NUMBER_OF_DOCUMENT_TOPIC AS ITEM_COUNT");
		bestTermsMap.select.add("TOPIC_ID");
		bestTermsMap.select.add("WORDTYPE");
		bestTermsMap.from.add("WORDTYPE$BEST_TERMS");
		bestTermsMap.where.add(wordtypeInString);
		bestTermsMap.orderBy.add("TOPIC_ID");
		bestTermsMap.orderBy.add("WORDTYPE");
		bestTermsMap.orderBy.add("ITEM_COUNT DESC");

		this.setDatabase(database);
		this.setServletWriter(pw);
	}

	public SelectMap getBestTermsMap() {
		return this.bestTermsMap;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	public void setServletWriter(PrintWriter servletWriter) {
		this.outWriter = servletWriter;
	}

	public void addBestTermsColumn(String bestTermsColumn, String bestTermsColumnName) {
		bestTermsMap.select.add(bestTermsColumn + " as " + bestTermsColumnName);
	}

	public void getBestTerms(String enabledWordTypes[]) throws SQLException {
		ArrayList<String> bestTermsColumnList = bestTermsMap.getCleanColumnNames();
		
		bestTermsColumnList.remove("TOPIC_ID");
		bestTermsColumnList.remove("WORDTYPE");
		
		JSONObject topicData = new JSONObject();
		JSONObject bestTermsData = new JSONObject();
		JSONObject bestTerms = new JSONObject();
		JSONObject wordTypes = new JSONObject();
		JSONObject all = new JSONObject();
		JSONArray sorting = new JSONArray();

		ResultSet termQueryRS = database.executeQuery(bestTermsMap.getSQLString());
		Integer topicId = -1;
		String wordType = "";
		while (termQueryRS.next()) {
			if (!wordType.equals(termQueryRS.getString("WORDTYPE"))) {
				if (wordType.length() > 0) {
					topicData.put("SORTING", sorting);
					bestTerms.put(wordType, topicData);
					
					
					topicData = new JSONObject();
					sorting = new JSONArray();
				}
				wordType = termQueryRS.getString("WORDTYPE");	
			}
			
			if(topicId != termQueryRS.getInt("TOPIC_ID")) {
				if(topicId > -1) {
					for(String wordtype: enabledWordTypes) {
						if(!bestTerms.containsKey(wordtype)) {
							topicData = new JSONObject();
							topicData.put("SORTING", new JSONArray());
							bestTerms.put(wordtype, topicData);
						}
					}
					wordTypes.put("ITEMS", bestTerms);
					all.put(topicId, wordTypes);
					bestTerms = new JSONObject();
					wordTypes = new JSONObject();
				}
				topicId = termQueryRS.getInt("TOPIC_ID");
			}
			
			bestTermsData = new JSONObject();
			for (int i = 0; i < bestTermsColumnList.size(); i++) {
				bestTermsData.put(bestTermsColumnList.get(i), termQueryRS.getString(bestTermsColumnList.get(i)));
			}
			topicData.put(termQueryRS.getString("ITEM_ID"), bestTermsData);
			sorting.add(termQueryRS.getString("ITEM_ID"));
		}
		topicData.put("SORTING", sorting);
		bestTerms.put(wordType, topicData);
		for(String wordtype: enabledWordTypes) {
			if(!bestTerms.containsKey(wordtype)) {
				topicData = new JSONObject();
				topicData.put("SORTING", new JSONArray());
				bestTerms.put(wordtype, topicData);
			}
		}
		wordTypes.put("ITEMS", bestTerms);
		all.put(topicId, wordTypes);
		
		// fill missing topicIds
		ResultSet topicIdsRS = database.executeQuery("SELECT DISTINCT TOPIC_ID FROM TOPIC");
		while(topicIdsRS.next()) {
			topicId = topicIdsRS.getInt("TOPIC_ID");
			if(!all.containsKey(topicId.toString())) {
				for(String wordtype: enabledWordTypes) {
					topicData = new JSONObject();
					topicData.put("SORTING", new JSONArray());
					bestTerms.put(wordtype, topicData);
				}
				wordTypes = new JSONObject();
				wordTypes.put("ITEMS", bestTerms);
				all.put(topicId, wordTypes);
				
			}
		}

		outWriter.print(all.toString());
	}
}
