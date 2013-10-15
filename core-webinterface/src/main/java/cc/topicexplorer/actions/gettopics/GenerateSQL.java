package cc.topicexplorer.actions.gettopics;


import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.chain.Context;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;


public class GenerateSQL extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		SelectMap preQueryMap, innerQueryMap, mainQueryMap, tempMainQueryMap, tempInnerQueryMap;
		PrintWriter servletWriter = (PrintWriter) communicationContext.get("SERVLET_WRITER");
		
		JSONObject topic, topicTerm, topicTermColl, all;
		JSONArray topicArray, topicTermArray;
		
		preQueryMap = (SelectMap) communicationContext.get("PRE_QUERY");
		innerQueryMap = (SelectMap) communicationContext.get("INNER_QUERY");
		mainQueryMap = (SelectMap) communicationContext.get("MAIN_QUERY");
		
		ArrayList<String> topicColumnList = mainQueryMap.getCleanColumnNames();
		
		topicColumnList.remove("TERM_NAME");
		topicColumnList.remove("relevanz");
		
		boolean firstRes;
		
		all = new JSONObject();
		topic = new JSONObject();
		topicTerm = new JSONObject();
		topicTermColl = new JSONObject();
		topicArray = new JSONArray();
		topicTermArray = new JSONArray();
		
		try {
			ResultSet preQueryRS = database.executeQuery(preQueryMap.getSQLString());
			while(preQueryRS.next()) {
				tempInnerQueryMap = innerQueryMap.clone();
				tempMainQueryMap = mainQueryMap.clone();
				tempInnerQueryMap.where.add("TOPIC_ID=" + preQueryRS.getString(1));
				tempMainQueryMap.from.add("(" + tempInnerQueryMap.getSQLString() + ") CountRelevanz");
				try {
					firstRes = true;
					ResultSet mainQueryRS = database.executeQuery(tempMainQueryMap.getSQLString());
					while(mainQueryRS.next()) {
						if(firstRes) {
							firstRes = false;
							topic.put("TOPIC_ID", preQueryRS.getString(1));
							for(int i = 0; i < topicColumnList.size(); i++ ) {
		        				topic.put(topicColumnList.get(i), mainQueryRS.getString(topicColumnList.get(i)));
		        			}
							topicArray.add(topic);
						}
						topicTerm.put("TERM_NAME", mainQueryRS.getString("TERM_NAME"));
						topicTerm.put("RELEVANCE", mainQueryRS.getString("relevanz"));
						topicTermArray.add(topicTerm);
						
					}
					topicTermColl.put("TOPIC_ID", preQueryRS.getString(1));
					topicTermColl.put("TERM", topicTermArray);
				} catch (SQLException e) {
					logger.fatal("Error in Query: " + mainQueryMap.getSQLString());
					e.printStackTrace();
				}		
			}
			all.put("TOPIC", topicArray);
			all.put("TERM_TOPIC", topicTermColl);
	
			servletWriter.println(all.toString());		
		} catch (SQLException e) {
			logger.fatal("Error in Query: " + preQueryMap.getSQLString());
			e.printStackTrace();
		}		
	}
	
	@Override
	public void addDependencies() {
		beforeDependencies.add("GetTopicsCoreCollect"); 
	}	
}
