package cc.topicexplorer.database.tables.document;

import java.sql.SQLException;

import cc.topicexplorer.commands.TableCreateCommand;

public class DocumentCreate extends TableCreateCommand {

	@Override
	public void createTable() {
		try {
			database.executeUpdateQuery("create table `" + this.tableName + "` ("
					+ "DOCUMENT_ID INTEGER(11) NOT NULL PRIMARY KEY, " + "NUMBER_OF_TOKENS INTEGER(11) NOT NULL) "
					+ "ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		tableName = "DOCUMENT";
	}
}