package cc.topicexplorer.database.tables.documenttermtopic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.topicexplorer.database.Database;

public class PreCheck implements Command {
	private static final Logger logger = Logger.getLogger(DocumentTermTopicCreate.class);

	@Override
	public void execute(Context context) {
		Properties properties = context.get("properties", Properties.class);
		Database database = context.get("database", Database.class);
		
		if(!properties.get("newTopics").toString().equalsIgnoreCase("true")) {
			try {
				ResultSet topicCountRS = database.executeQuery("SELECT COUNT(DISTINCT TOPIC_ID) AS TOPIC_COUNT FROM DOCUMENT_TERM_TOPIC");
				if(topicCountRS.next()) {
					if(!properties.get("malletNumTopics").equals(topicCountRS.getString("TOPIC_COUNT"))) {
						logger.warn("DOCUMENT_TERM_TOPIC tables topic count ("
								+ topicCountRS.getString("TOPIC_COUNT") + ") doesn't match properties topic count ("
								+ properties.get("malletNumTopics") + ")... fall back to creating a new  DOCUMENT_TERM_TOPIC table.");
						properties.setProperty("newTopics", "true");
						context.rebind("properties", properties);
					}
				}
			} catch(SQLException e)  {
				logger.warn("DOCUMENT_TERM_TOPIC table doesn't exist for reusing it... fall back to creating a new one.");
				properties.setProperty("newTopics", "true");
				context.rebind("properties", properties);
			}
			System.out.println(properties.get("newTopics"));
		}
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet();
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
