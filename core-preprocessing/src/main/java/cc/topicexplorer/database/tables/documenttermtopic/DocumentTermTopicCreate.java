package cc.topicexplorer.database.tables.documenttermtopic;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.collect.Sets;

public class DocumentTermTopicCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(DocumentTermTopicCreate.class);

	@Override
	public void createTable() {
		if(!properties.get("newTopics").toString().equalsIgnoreCase("true")) {
			logger.info("Skip: Take the previous DOCUMENT_TERM_TOPIC table");			
		} else {
			try {
				database.executeUpdateQuery(" CREATE TABLE `" + this.tableName + "` ( "
						+ "`DOCUMENT_ID` INT(11) NOT NULL, " + "`POSITION_OF_TOKEN_IN_DOCUMENT` INT(11) NOT NULL, "
						+ "`TERM` varchar(100) NOT NULL, "
						+ "`TOKEN` varchar(100) NOT NULL, " + "`TOPIC_ID` int(11) NOT NULL, "
						+ "`PROBALITY_TOPIC_GIVEN_DOCUMENT_TERM` FLOAT, "
						+ "`DOCUMENT_TERM_TOPIC_ASSIGNMENT` INT(11) NOT NULL "
						+ ") ENGINE=InnoDB;");
			} catch (SQLException e) {
				logger.error("Table " + this.tableName + " could not be created");
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void setTableName() {
		tableName = "DOCUMENT_TERM_TOPIC";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentTermTopicPreCheck");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}
	
	public void dropTable() {
		if(properties.get("newTopics").toString().equalsIgnoreCase("true")) {
			super.dropTable();
		} else {
			logger.info("Skip: Take the previous DOCUMENT_TERM_TOPIC table");
			
		}
	}

}
