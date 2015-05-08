package cc.topicexplorer.plugin.wordtype.preprocessing.tables.documenttermtopic;

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
				database.executeUpdateQuery("ALTER IGNORE TABLE `" + this.tableName
						+ "` ADD COLUMN WORDTYPE$WORDTYPE varchar(100) NOT NULL;");
			} catch (SQLException e) {
				logger.error("Column WORDTYPE$WORDTYPE could not be added to table " + this.tableName);
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void dropTable() {
		if(!properties.get("newTopics").toString().equalsIgnoreCase("true")) {
			logger.info("[ " + getClass() + " ] - Skip: Take the previous DOCUMENT_TERM_TOPIC table");
		} else {
			try {
				this.database.executeUpdateQuery("ALTER TABLE " + this.tableName + " DROP COLUMN WORDTYPE$WORDTYPE");
			} catch (SQLException e) {
				if (e.getErrorCode() != 1091) { // MySQL Error code for: 'Can't //
												// DROP..; check that column/key //
												// exists
					logger.error("Document.dropColumns: Cannot drop column.");
					throw new RuntimeException(e);
				} else {
					logger.info("dropColumns: ignored SQL-Exception with error code 1091.");
				}
	
			}
		}
	}

	@Override
	public void setTableName() {
		tableName = "DOCUMENT_TERM_TOPIC";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("InFilePreparation");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentTermTopicCreate");
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
