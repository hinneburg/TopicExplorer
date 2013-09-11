package cc.topicexplorer.database.tables.termtopic;

import java.sql.SQLException;
import java.util.ArrayList;

import cc.topicexplorer.chain.commands.TableCreateCommand;

public class TermTopicCreate extends TableCreateCommand {

	@Override
	public void createTable() throws SQLException {
		database
				.executeUpdateQuery("create table `"
						+ this.tableName
						+ "` ("
						+ "TERM_ID INTEGER(11) NOT NULL, TOPIC_ID INTEGER(11) NOT NULL, "
						+ "NUMBER_OF_TOKEN_TOPIC int(11) NOT NULL, "
						+ "PR_TOPIC_GIVEN_TERM DOUBLE NOT NULL, PR_TERM_GIVEN_TOPIC DOUBLE) "
						+ "ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin; ");
	}

	@Override
	public void setTableName() {
		tableName = "TERM_TOPIC";
	}
}
