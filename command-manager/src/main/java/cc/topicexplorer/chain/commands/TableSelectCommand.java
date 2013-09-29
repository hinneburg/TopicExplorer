package cc.topicexplorer.chain.commands;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.chain.Context;
import org.jooq.tools.StringUtils;

public abstract class TableSelectCommand extends TableCommand {
	protected String getSQLPart(ArrayList<String> part, String glue) {
		return StringUtils.join(part.toArray(new String[part.size()]), glue);
	}
	
	protected String getSQLString(HashMap<String, ArrayList<String>> queryMap) {
		String select, from, where, orderBy, limit;
		
		select = "SELECT " + this.getSQLPart(queryMap.get("SELECT"), ", ");
		from = " FROM " + this.getSQLPart(queryMap.get("FROM"), ", ");
		if(queryMap.get("WHERE").size() > 0) {
			where = " WHERE " + this.getSQLPart(queryMap.get("WHERE"), " AND ");
		} else {
			where = "";
		}
		if(queryMap.get("ORDERBY").size() > 0) {
			orderBy = " ORDER BY " + this.getSQLPart(queryMap.get("ORDERBY"), ", ");
		} else {
			orderBy = "";
		}
		if(queryMap.get("LIMIT").size() > 0) {
			limit = " LIMIT ";
			if(queryMap.get("START").size() > 0) {
				limit += queryMap.get("START").get(0) + ", ";
			} 
			limit += queryMap.get("LIMIT").get(0);
		} else {
			limit = "";
		}

		return select + from + where + orderBy + limit;
	}
	
	protected HashMap<String, ArrayList<String>> newSelectMap() {
		HashMap<String, ArrayList<String>> selectMap = new HashMap<String, ArrayList<String>>();
		
		selectMap.put("SELECT", new ArrayList<String>());
		selectMap.put("FROM", new ArrayList<String>());
		selectMap.put("WHERE", new ArrayList<String>());
		selectMap.put("ORDERBY", new ArrayList<String>());
		selectMap.put("LIMIT", new ArrayList<String>()); // TODO: typ int???
		selectMap.put("START", new ArrayList<String>()); // TODO: typ int???
		
		return selectMap;
	}
	@Override
	public abstract void tableExecute(Context context);

	@Override
	public void setTableName() {
		// TODO Auto-generated method stub
	}

}
