package cc.topicexplorer.database.tables.documenttermtopic;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import jline.ConsoleReader;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.topicexplorer.database.Database;

public class PreCheck implements Command {
	private static final Logger logger = Logger.getLogger(PreCheck.class);

	private Properties properties; 
	
	private boolean timeout = false;
	
	private static long timeoutMillis = 10000;

	TimerTask task = new TimerTask(){
		public void run(){
			if(timeout){
				logger.info("Timeout aborted");
				System.exit(0);
			}
		}
	};
	
	@Override
	public void execute(Context context) {
		properties = context.get("properties", Properties.class);
		Database database = context.get("database", Database.class);
		
		if(!properties.get("newTopics").toString().equalsIgnoreCase("true")) {
			try {
				ResultSet topicCountRS = database.executeQuery("SELECT COUNT(DISTINCT TOPIC_ID) AS TOPIC_COUNT FROM DOCUMENT_TERM_TOPIC");
				if(topicCountRS.next()) {
					logger.info(topicCountRS.getString("TOPIC_COUNT"));
					if(!properties.get("malletNumTopics").equals(topicCountRS.getString("TOPIC_COUNT"))) {
						this.userDecision("DOCUMENT_TERM_TOPIC tables topic count ("
								+ topicCountRS.getString("TOPIC_COUNT") + ") doesn't match properties topic count ("
								+ properties.get("malletNumTopics") + ")...", context);
						
					}
				}
			} catch(SQLException e)  {
				this.userDecision("DOCUMENT_TERM_TOPIC table doesn't exists for reusing it...", context);
				properties.setProperty("newTopics", "true");
				context.rebind("properties", properties);
			}
			System.out.println(properties.get("newTopics"));
		}
	}

	private void userDecision(String warnMessage, Context context) {
		timeout = true;
		logger.warn(warnMessage);
		System.out.print("Ignore newTopics=false and create new topics? (y/N): ");
		if(userInputIsYes()) {
			timeout = false;
			properties.setProperty("newTopics", "true");
			context.rebind("properties", properties);
			logger.info("User decided to create new topics");
		} else {
			logger.info("User aborted");
			System.exit(0);
		}	
	}
	
	public boolean userInputIsYes()  {
		Timer timer = new Timer();
		timer.schedule( task, timeoutMillis );
		
		ConsoleReader reader;
		try {
			reader = new ConsoleReader();
			int k = reader.readVirtualKey();
			if(k > -1) {
				timer.cancel();
	           	if(k == 121 || k == 89) return true; 
	        }
	        
		} catch (IOException e) {
			logger.error("Error reading console input");
			System.exit(0);
		}
		return false;
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