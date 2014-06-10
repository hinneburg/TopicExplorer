package cc.topicexplorer.plugin.wiki.preprocessing.tables.documenttermtopic;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.collect.Sets;

public class DocumentTermTopicCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(DocumentTermTopicCreate.class);

	@Override
	public void createTable() {
		try {
			database.executeUpdateQuery("ALTER IGNORE TABLE `" + this.tableName
					+ "` ADD COLUMN WIKI$POSITION int(11) NOT NULL;");
		} catch (SQLException e) {
			logger.error("Column WIKI$POSITION could not be added to table " + this.tableName);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void dropTable() {
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName + " DROP COLUMN WIKI$POSITION");
		} catch (SQLException e) {
			if (e.getErrorCode() != 1091) { // MySQL Error code for: 'Can't
				//
				// DROP..; check that column/key //
				// exists
				logger.error("WIKI_DocumentTermTopicCreate.dropColumns: Cannot drop column.");
				throw new RuntimeException(e);
			} else {
				logger.info("dropColumns: ignored SQL-Exception with error code 1091.");
			}
		}
	}

	@Override
	public void setTableName() {
		tableName = "DOCUMENT_TERM_TOPIC";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("DocumentTermTopicFill", "InFilePreparation");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentTermTopicCreate");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet("Prune");
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}

}
