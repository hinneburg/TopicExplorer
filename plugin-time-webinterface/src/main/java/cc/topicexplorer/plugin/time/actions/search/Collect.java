package cc.topicexplorer.plugin.time.actions.search;

import java.util.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import cc.commandmanager.core.Context;
import cc.topicexplorer.actions.search.Search;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		Search searchAction = context.get("SEARCH_ACTION", Search.class);
		searchAction.addSearchColumn("DOCUMENT.TIME$TIME_STAMP", "TIME$TIME_STAMP");
		
		if(context.containsKey("filter")) {
			JSONObject filter;
			try {
				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
				filter = new JSONObject(context.getString("filter"));
				if(filter.has("firstDate")) {
					String firstDate = filter.getString("firstDate");
					if(!firstDate.isEmpty()) {
						Date date = dateFormat.parse(firstDate);
						long time = date.getTime();
						new Timestamp(time);
						searchAction.addWhereClause("DOCUMENT.TIME$TIME_STAMP > " + (time / 1000));
					}
				}
				if(filter.has("lastDate")) {
					String lastDate = filter.getString("lastDate");
					if(!lastDate.isEmpty()) {
						Date date = dateFormat.parse(lastDate);
						long time = date.getTime();
						new Timestamp(time);
						searchAction.addWhereClause("DOCUMENT.TIME$TIME_STAMP < " + time / 1000);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ArrayList<String> orderBy = searchAction.getOrderBy();
		orderBy.add("DOCUMENT.TIME$TIME_STAMP");
		searchAction.setOrderBy(orderBy);
		
		context.rebind("SEARCH_ACTION", searchAction);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("SearchCoreGenerateSQL");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("SearchCoreCreate");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet("SearchTextCollect");
	}

}
