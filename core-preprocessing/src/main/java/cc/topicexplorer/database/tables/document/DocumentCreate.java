package cc.topicexplorer.database.tables.document;

import java.sql.SQLException;
import cc.topicexplorer.chain.commands.TableCreateCommand;


/**
 * @author user
 * 
 */
public class DocumentCreate extends TableCreateCommand {

	@Override
	public void createTable() throws SQLException {
		database.executeUpdateQuery("create table `" + this.tableName + "` ("
				+ "DOCUMENT_ID INTEGER(11) NOT NULL PRIMARY KEY, "
				+ "NUMBER_OF_TOKENS INTEGER(11) NOT NULL) "
				+ "ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");

	}

	@Override
	public void setTableName() {
		tableName = "DOCUMENT";
	}
}