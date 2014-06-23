package cc.topicexplorer.plugin.pos.preprocessing.tables;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;


public class PosCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(PosCreate.class);

	@Override
	public void createTable() {
		try {
			database.executeUpdateQuery(" CREATE TABLE `" + this.tableName + "` ( "
					+ "`DOCUMENT_ID` INT(11) NOT NULL, " + "`POSITION_OF_TOKEN_IN_DOCUMENT` INT(11) NOT NULL, "
					+ "`TERM` varchar(255) COLLATE utf8_bin NOT NULL, "
					+ "`TOKEN` varchar(255) COLLATE utf8_bin NOT NULL, " + "`TOPIC_ID` int(11) NOT NULL, "
					+ "`PROBALITY_TOPIC_GIVEN_DOCUMENT_TERM` FLOAT, "
					+ "`DOCUMENT_TERM_TOPIC_ASSIGNMENT` INT(11) NOT NULL "
					+ ") ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_bin ;");
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created");
			throw new RuntimeException(e);
		}

	}
	
	/*
	@Override
	public void createTable() {
		try {
			database.executeUpdateQuery(" CREATE TABLE `" + this.tableName + "` ( "
					+ "`DOCUMENT_ID` INT(11) NOT NULL, " 
					+ "`POSITION_OF_TOKEN_IN_DOCUMENT` INT(11) NOT NULL, "
					+ "`TOKEN` varchar(255) COLLATE utf8_bin NOT NULL, " 
					+ "`TOPIC_ID` int(11) NOT NULL, "
					+ "`POS_CONTINUATION` varchar(255) COLLATE utf8_bin NOT NULL, " 
					+ "`CONTINUATION_POS` varchar(255) COLLATE utf8_bin NOT NULL, " 
					+ ") ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_bin ;");
		} catch (SQLException e) {
			logger.error("Column could not be added to table " + this.tableName);
			throw new RuntimeException(e);
		}
	}
*/
	private void dropColumns() {
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName 
					+ " DROP COLUMN DATA.DOCUMENT_ID"
					+ " DROP COLUMN DATA.TOKEN"
					+ " DROP COLUMN DATA.TOPIC_ID"
					+ " DROP COLUMN DATA.POS_CONTINUATION"
					+ " DROP COLUMN DATA.CONTINUATION_POS");
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
		this.tableName = "DATA";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Collections.emptySet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Collections.emptySet();
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
