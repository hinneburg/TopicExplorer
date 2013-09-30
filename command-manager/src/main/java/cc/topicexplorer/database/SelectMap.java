package cc.topicexplorer.database;

import java.util.ArrayList;

import org.jooq.tools.StringUtils;

public class SelectMap {
	public ArrayList<String> select =  new ArrayList<String>();
	public ArrayList<String> from =  new ArrayList<String>();
	public ArrayList<String> where =  new ArrayList<String>();
	public ArrayList<String> orderBy =  new ArrayList<String>();
	public int limit, offset;
	
	private String getSQLPart(ArrayList<String> part, String glue) {
		return StringUtils.join(part.toArray(new String[part.size()]), glue);
	}
	
	public String getSQLString() {
		String select, from, where, orderBy, limit;
		
		select = "SELECT " + this.getSQLPart(this.select, ", ");
		from = " FROM " + this.getSQLPart(this.from, ", ");
		if(this.where.size() > 0) {
			where = " WHERE " + this.getSQLPart(this.where, " AND ");
		} else {
			where = "";
		}
		if(this.orderBy.size() > 0) {
			orderBy = " ORDER BY " + this.getSQLPart(this.orderBy, ", ");
		} else {
			orderBy = "";
		}
		if(this.limit > 0) {
			limit = " LIMIT ";
			if(this.offset > 0) {
				limit += this.offset + ", ";
			} 
			limit += this.limit;
		} else {
			limit = "";
		}

		return select + from + where + orderBy + limit;
	}

}
