package cc.topicexplorer.actions.gettopics;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;
import cc.topicexplorer.database.SelectMap;

import com.google.common.collect.Sets;

public class GenerateSQL extends TableSelectCommand {

	private static final Logger logger = Logger.getLogger(GenerateSQL.class);

	@Override
	public void tableExecute(Context context) {
		SelectMap preQueryMap, innerQueryMap, mainQueryMap, tempMainQueryMap, tempInnerQueryMap;
		preQueryMap = context.get("PRE_QUERY", SelectMap.class);
		innerQueryMap = context.get("INNER_QUERY", SelectMap.class);
		mainQueryMap = context.get("MAIN_QUERY", SelectMap.class);

		ArrayList<String> topicColumnList = mainQueryMap.getCleanColumnNames();
		topicColumnList.remove("TERM_NAME");
		topicColumnList.remove("relevanz");

		boolean firstRes;

		JSONObject topic, topicTerm, topicTermColl, all;
		JSONArray topicArray, topicTermArray;
		all = new JSONObject();
		topic = new JSONObject();
		topicTerm = new JSONObject();
		topicTermColl = new JSONObject();
		topicArray = new JSONArray();
		topicTermArray = new JSONArray();

		try {
			ResultSet preQueryRS = database.executeQuery(preQueryMap.getSQLString());
			while (preQueryRS.next()) {
				tempInnerQueryMap = innerQueryMap.clone();
				tempMainQueryMap = mainQueryMap.clone();
				tempInnerQueryMap.where.add("TOPIC_ID=" + preQueryRS.getString(1));
				tempMainQueryMap.from.add("(" + tempInnerQueryMap.getSQLString() + ") CountRelevanz");

				try {
					firstRes = true;
					ResultSet mainQueryRS = database.executeQuery(tempMainQueryMap.getSQLString());
					while (mainQueryRS.next()) {
						if (firstRes) {
							firstRes = false;
							topic.put("TOPIC_ID", preQueryRS.getString(1));
							for (int i = 0; i < topicColumnList.size(); i++) {
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
					logger.error("Error in Query: " + tempMainQueryMap.getSQLString());
					throw new RuntimeException(e);
				}

			}
			all.put("TOPIC", topicArray);
			all.put("TERM_TOPIC", topicTermColl);

			PrintWriter servletWriter = context.get("SERVLET_WRITER", PrintWriter.class);
			servletWriter.println(all.toString());
		} catch (SQLException e) {
			logger.error("Error in Query: " + mainQueryMap.getSQLString());
			throw new RuntimeException(e);
		}
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("GetTopicsCoreCollect");
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
