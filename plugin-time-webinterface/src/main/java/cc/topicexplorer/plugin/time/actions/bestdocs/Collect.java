package cc.topicexplorer.plugin.time.actions.bestdocs;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import cc.commandmanager.core.Context;
import cc.topicexplorer.actions.bestdocs.BestDocumentsForGivenTopic;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		BestDocumentsForGivenTopic bestDocAction = context.get("BEST_DOC_ACTION", BestDocumentsForGivenTopic.class);

		bestDocAction.addDocumentColumn("DOCUMENT.TIME$TIME_STAMP", "TIME$TIME_STAMP", context.containsKey("term"));
		if(context.containsKey("week")) {
			int startTstamp = Integer.valueOf(context.getString("week"));
			int endTstamp = startTstamp + 604800;
	
			bestDocAction.addWhereClause("DOCUMENT.TIME$TIME_STAMP > " + startTstamp);
			bestDocAction.addWhereClause("DOCUMENT.TIME$TIME_STAMP < " + endTstamp);
		}
		if(context.containsKey("sorting")) {
			String sorting = context.getString("sorting");
			if (sorting.equals("TIME")) {
				ArrayList<String> orderBy = new ArrayList<String>();
				orderBy.add("DOCUMENT.TIME$TIME_STAMP");
				bestDocAction.setOrderBy(orderBy);
			}
		}
		
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
						bestDocAction.addWhereClause("DOCUMENT.TIME$TIME_STAMP > " + time / 1000);
					}
				}
				if(filter.has("lastDate")) {
					String lastDate = filter.getString("lastDate");
					if(!lastDate.isEmpty()) {
						Date date = dateFormat.parse(lastDate);
						long time = date.getTime();
						new Timestamp(time);
						bestDocAction.addWhereClause("DOCUMENT.TIME$TIME_STAMP < " + time / 1000);
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
		
		context.rebind("BEST_DOC_ACTION", bestDocAction);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("BestDocsCoreGenerateSQL");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("BestDocsCoreCreate");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}

}
