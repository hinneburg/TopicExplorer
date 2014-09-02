package cc.topicexplorer.plugin.pos.preprocessing.tables;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

import cc.topicexplorer.commands.TableCreateCommand;


public class PosCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(PosCreate.class);

	
	@Override
	public void createTable() {
		try {
			database.executeUpdateQuery("ALTER IGNORE TABLE `" + this.tableName
					+ "` ADD COLUMN POS$CONTINUATION` INT(11);");

			database.executeUpdateQuery("ALTER IGNORE TABLE `" + this.tableName
					+ "` ADD COLUMN POS$CONTINUATION_POS` varchar(255) COLLATE utf8_bin;");

			database.executeUpdateQuery("ALTER IGNORE TABLE `" + this.tableName
					+ "` ADD COLUMN POS$POS` varchar(255) COLLATE utf8_bin NOT NULL;");
			/*
			database.executeUpdateQuery(" ALTER TABLE `" + this.tableName + "` ( "
					+ "`DOCUMENT_ID` INT(11) NOT NULL, " 
					+ "`POSITION_OF_TOKEN_IN_DOCUMENT` INT(11) NOT NULL, "
					+ "`TOKEN` varchar(255) COLLATE utf8_bin NOT NULL, " 
					+ "`TOPIC_ID` int(11) NOT NULL, "
					!!+ "`POS_CONTINUATION` varchar(255) COLLATE utf8_bin NOT NULL, " 
					!!+ "`CONTINUATION_POS` varchar(255) COLLATE utf8_bin NOT NULL, " 
					+ ") ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_bin ;");*/
		} catch (SQLException e) {
			logger.error("Column could not be added to table " + this.tableName);
			throw new RuntimeException(e);
		}
	}

	private void dropColumns() {
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName 
					+ " DROP COLUMN POS$CONTINUATION"
					+ " DROP COLUMN POS$CONTINUATION_POS"
					+ " DROP COLUMN POS$POS;");
		} catch (SQLException e) {
			if (e.getErrorCode() != 1091) { // MySQL Error code for 'Can't DROP
				// ..; check that column/key exists
				logger.error("TopicMetaData.dropColumns: Cannot drop column.");
				throw new RuntimeException(e);
			} else {
				logger.info("dropColumns: ignored SQL-Exception with error code 1091.");
			}
		}
	}
	@Override
	public void dropTable() {
		this.dropColumns();
	}

	@Override
	public void setTableName() {
		this.tableName = "DOCUMENT_TERM_TOPIC";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Collections.emptySet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentTermTopicCreate");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Collections.emptySet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Collections.emptySet();
	}

}
