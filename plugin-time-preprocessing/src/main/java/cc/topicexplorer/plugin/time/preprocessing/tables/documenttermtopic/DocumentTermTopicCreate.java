package cc.topicexplorer.plugin.time.preprocessing.tables.documenttermtopic;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.collect.Sets;

public class DocumentTermTopicCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(DocumentTermTopicCreate.class);

	/**
	 * @throws SQLException
	 *             if a database access error occurs or the given SQL statement produces a ResultSet object
	 */
	@Override
	public void createTable() {
		if(!properties.get("newTopics").toString().equalsIgnoreCase("true")) {
			logger.info("Skip: Take the previous DOCUMENT_TERM_TOPIC table");
		} else {
			try {
				database.executeUpdateQuery("ALTER IGNORE TABLE `" + this.tableName
						+ "` ADD COLUMN TIME$WEEK INT(11) NOT NULL DEFAULT 1");
			} catch (SQLException e) {
				logger.error(String
						.format("Table %s could not be altered. Column TIME$WEEK was not added.", this.tableName));
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void dropTable() {
		if(!properties.get("newTopics").toString().equalsIgnoreCase("true")) {
			logger.info("Skip: Take the previous DOCUMENT_TERM_TOPIC table");
		} else {
			try {
				this.database.executeUpdateQuery("ALTER TABLE " + this.tableName + " DROP COLUMN TIME$WEEK");
			} catch (SQLException e) {
				if (e.getErrorCode() != 1091) { // MySQL Error code for 'Can't DROP
												// ..; check that column/key exists
					logger.error("DocumentTermTopic.dropColumns: Cannot drop column.");
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
		return Sets.newHashSet();
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
