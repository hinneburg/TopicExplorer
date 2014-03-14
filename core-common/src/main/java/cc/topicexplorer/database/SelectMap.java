package cc.topicexplorer.database;

import java.util.ArrayList;

import org.jooq.tools.StringUtils;

public class SelectMap {
	public ArrayList<String> select = new ArrayList<String>();
	public ArrayList<String> from = new ArrayList<String>();
	public ArrayList<String> where = new ArrayList<String>();
	public ArrayList<String> groupBy = new ArrayList<String>();
	public ArrayList<String> orderBy = new ArrayList<String>();
	public int limit, offset;

	private String getSQLPart(ArrayList<String> part, String glue) {
		return StringUtils.join(part.toArray(new String[part.size()]), glue);
	}

	public String getSQLString() {
		String select, from, where, groupBy, orderBy, limit;

		select = "SELECT " + this.getSQLPart(this.select, ", ");
		from = " FROM " + this.getSQLPart(this.from, ", ");
		if (this.where.size() > 0) {
			where = " WHERE " + this.getSQLPart(this.where, " AND ");
		} else {
			where = "";
		}
		if (this.groupBy.size() > 0) {
			groupBy = " GROUP BY " + this.getSQLPart(this.groupBy, ", ");
		} else {
			groupBy = "";
		}
		if (this.orderBy.size() > 0) {
			orderBy = " ORDER BY " + this.getSQLPart(this.orderBy, ", ");
		} else {
			orderBy = "";
		}
		if (this.limit > 0) {
			limit = " LIMIT ";
			if (this.offset > 0) {
				limit += this.offset + ", ";
			}
			limit += this.limit;
		} else {
			limit = "";
		}

		return select + from + where + groupBy + orderBy + limit;
	}

	public ArrayList<String> getCleanColumnNames() {
		ArrayList<String> list = new ArrayList<String>();
		int cutIndex = 0;
		for (int i = 0; i < select.size(); i++) {
			cutIndex = 0;
			if (cutIndex < select.get(i).lastIndexOf(".")) {
				cutIndex = select.get(i).lastIndexOf(".") + 1;
			}
			if (cutIndex < select.get(i).toLowerCase().lastIndexOf(" as ")) {
				cutIndex = select.get(i).toLowerCase().lastIndexOf(" as ") + 4;
			}
			list.add(select.get(i).substring(cutIndex).trim());
		}
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public SelectMap clone() {
		SelectMap newMap = new SelectMap();
		newMap.select = (ArrayList<String>) this.select.clone();
		newMap.from = (ArrayList<String>) this.from.clone();
		newMap.where = (ArrayList<String>) this.where.clone();
		newMap.orderBy = (ArrayList<String>) this.orderBy.clone();
		newMap.limit = this.limit;
		newMap.offset = this.offset;
		return newMap;
	}
}
