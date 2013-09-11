package cc.topicexplorer.database.tables.topic;

import java.sql.SQLException;
import java.util.ArrayList;

import cc.topicexplorer.chain.commands.TableCreateCommand;

public class TopicCreate extends TableCreateCommand {

	@Override
	public void createTable() throws SQLException {
		database.executeUpdateQuery(" CREATE TABLE `" + this.tableName + "` ("
				+ "  `TOPIC_ID` int(11) NOT NULL,"
				+ "  `NUMBER_OF_TOKENS` int(11) NOT NULL"
				+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ;");

	}

	@Override
	public void setTableName() {
		tableName = "TOPIC";
	}
}