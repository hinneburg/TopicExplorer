package cc.topicexplorer.plugin.duplicates.preprocessing.tables.duplicates;

import java.sql.SQLException;

import cc.topicexplorer.commands.TableCreateCommand;

public class DuplicatesCreate extends TableCreateCommand {

	@Override
	public void createTable() {
//		@formatter:off
		String query = " CREATE TABLE `" + this.tableName + "` (" +
				"  `MD5SUM` varchar(255) COLLATE utf8_bin NOT NULL," +
				"  `DOCUMENT_ID` varchar(255) COLLATE utf8_bin NOT NULL," +
				"  `START_POSITION` int(11) NOT NULL," +
				"  `END_POSITION` int(11) NOT NULL," +
				"  `GROUP_ID` varchar(32) COLLATE utf8_bin NOT NULL," +
				"  `ID` int(11) NOT NULL AUTO_INCREMENT," +
				"  PRIMARY KEY (`ID`)," +
				"  KEY `HELPER_IDX` (`DOCUMENT_ID`,`START_POSITION`)," +
				"  KEY `GROUP_IDX` (`GROUP_ID`)" +
				") ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin ;";
//		@formatter:on
		try {
			this.database.executeUpdateQuery(query);
		} catch (SQLException sqlEx) {
			logger.error("Table " + this.tableName + "could not be created. The problematic query is:\n" + query);
			throw new RuntimeException(sqlEx);
		}
	}

	@Override
	public void setTableName() {
		this.tableName = "DUPLICATES$DUPLICATES";
	}
}