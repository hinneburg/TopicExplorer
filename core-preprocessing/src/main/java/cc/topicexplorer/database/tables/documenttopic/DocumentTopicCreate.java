package cc.topicexplorer.database.tables.documenttopic;

import java.sql.SQLException;

import cc.topicexplorer.commands.TableCreateCommand;

public class DocumentTopicCreate extends TableCreateCommand {
	@Override
	public void createTable() {
		try {
//			@formatter:off
			database.executeUpdateQuery("CREATE TABLE `"
					+ this.tableName
					+ "` ( "
					+ " `DOCUMENT_ID` INTEGER(11) NOT NULL, "
					+ " `TOPIC_ID` int(11) NOT NULL, "
					+ " `NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT` bigint(21) NOT NULL DEFAULT '0', "
					+ "	 PR_TOPIC_GIVEN_DOCUMENT DOUBLE,"
					+ "	 PR_DOCUMENT_GIVEN_TOPIC DOUBLE"
					+ "	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");
//			@formatter:on
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		tableName = "DOCUMENT_TOPIC";
	}
}
