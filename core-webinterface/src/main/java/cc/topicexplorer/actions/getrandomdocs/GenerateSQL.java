package cc.topicexplorer.actions.getrandomdocs;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

public class GenerateSQL extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;
		SelectMap preQueryMap, innerQueryMap, mainQueryMap;
		preQueryMap = (SelectMap) communicationContext.get("PRE_QUERY");
		innerQueryMap = (SelectMap) communicationContext.get("INNER_QUERY");
		mainQueryMap = (SelectMap) communicationContext.get("MAIN_QUERY");
		
		int random;

		try {
			ResultSet preQueryRS = database.executeQuery(preQueryMap.getSQLString());
			if(preQueryRS.next()) {
				random = Math.round((float) Math.random() * (preQueryRS.getInt("COUNT") - innerQueryMap.limit));
		        innerQueryMap.offset = random;
		        mainQueryMap.from.add("(" + innerQueryMap.getSQLString() + ") x");
		        System.out.println(mainQueryMap.getSQLString());
		        try {
		        	ResultSet mainQueryRS = database.executeQuery(mainQueryMap.getSQLString());
		        	while(mainQueryRS.next()) {
		        		// mache eier!!
		        	}
		        	
		        } catch (SQLException e) {
		        	logger.fatal("Error in Query: " + mainQueryMap.getSQLString());
					e.printStackTrace();
		        }
			}
		} catch (SQLException e) {
			logger.fatal("Error in Query: " + preQueryMap.getSQLString());
			e.printStackTrace();
		}	
	}

}
