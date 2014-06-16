package cc.topicexplorer.plugin.duplicates.preprocessing.tables.duplicates;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.collect.Sets;

public class DuplicatesCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(DuplicatesCreate.class);

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
