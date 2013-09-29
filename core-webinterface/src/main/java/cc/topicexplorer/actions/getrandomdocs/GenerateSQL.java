package cc.topicexplorer.actions.getrandomdocs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;

public class GenerateSQL extends TableSelectCommand {

	@SuppressWarnings("unchecked")
	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		HashMap<String, ArrayList<String>> preQueryMap, innerQueryMap, mainQueryMap;
		preQueryMap = (HashMap<String, ArrayList<String>>) communicationContext.get("PRE_QUERY");
		innerQueryMap = (HashMap<String, ArrayList<String>>) communicationContext.get("INNER_QUERY");
		mainQueryMap = (HashMap<String, ArrayList<String>>) communicationContext.get("MAIN_QUERY");
		
		int random;

		try {
			ResultSet preQueryRS = database.executeQuery(this.getSQLString(preQueryMap));
			if(preQueryRS.next()) {
				random = Math.round((float) Math.random() * (preQueryRS.getInt("COUNT") - Integer.parseInt(innerQueryMap.get("LIMIT").get(0))));
		        innerQueryMap.get("START").add(String.valueOf(random));
		        mainQueryMap.get("FROM").add("(" + this.getSQLString(innerQueryMap) + ") x");
		        System.out.println(this.getSQLString(mainQueryMap));
		        try {
		        	ResultSet mainQueryRS = database.executeQuery(this.getSQLString(mainQueryMap));
		        	while(mainQueryRS.next()) {
		        		// mache eier!!
		        	}
		        	
		        } catch (SQLException e) {
		        	logger.fatal("Error in Query: " + this.getSQLString(mainQueryMap));
					e.printStackTrace();
		        }
			}
		} catch (SQLException e) {
			logger.fatal("Error in Query: " + this.getSQLString(preQueryMap));
			e.printStackTrace();
		}	
	}

}
