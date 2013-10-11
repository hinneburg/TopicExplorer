package cc.topicexplorer.chain.commands;

import java.util.ArrayList;

import org.apache.commons.chain.Context;

import cc.topicexplorer.database.SelectMap;

public abstract class TableSelectCommand extends TableCommand {
	
	@Override
	public abstract void tableExecute(Context context);

	@Override
	public void setTableName() {
		// TODO Auto-generated method stub
	}
	protected ArrayList<String> getCleanColumnNames(SelectMap map) {
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0; i < map.select.size(); i++  ) {
			if(map.select.get(i).toLowerCase().contains(" as ")) {
				list.add(map.select.get(i).substring(map.select.get(i).toLowerCase().indexOf(" as ") + 3).trim());
			} else if(map.select.get(i).contains(".")) {
				list.add(map.select.get(i).substring(map.select.get(i).indexOf(".") + 1));
			} else {
				list.add(map.select.get(i));
			}	
		}		
		return list;		
	}
}
