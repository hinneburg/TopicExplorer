package cc.topicexplorer.database.tables.topic;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableCreateCommand;

public class TopicCreate extends TableCreateCommand {

	@Override
	public void createTable() {
		try {
			database.executeUpdateQuery(String.format(" CREATE TABLE `%s` (  `TOPIC_ID` int(11) NOT NULL,"
					+ "  `NUMBER_OF_TOKENS` int(11) NOT NULL"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ;", this.tableName));
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		tableName = "TOPIC";
	}
}