package cc.topicexplorer.database.tables.term;


import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableCreateCommand;


public class TermCreate extends TableCreateCommand {

	@Override
	public void createTable() throws SQLException {

		database
				.executeUpdateQuery("CREATE TABLE `"
						+ this.tableName
						+ "` "
						+ "   ( TERM_ID INTEGER(11) NOT NULL KEY auto_increment,"
						+ "	TERM_NAME VARCHAR(255) NOT NULL unique , "
						+ " DOCUMENT_FREQUENCY bigint(21) NOT NULL DEFAULT '0', "
						+ " CORPUS_FREQUENCY bigint(21) NOT NULL DEFAULT '0', "
						+ " INVERSE_DOCUMENT_FREQUENCY double DEFAULT NULL, "
						+ " CF_IDF double DEFAULT NULL "
						+ "	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin; ");

	}

	@Override
	public void setTableName() {
		tableName = "TERM";
	}
}