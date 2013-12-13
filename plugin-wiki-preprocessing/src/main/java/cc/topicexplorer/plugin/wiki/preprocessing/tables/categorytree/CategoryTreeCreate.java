package cc.topicexplorer.plugin.wiki.preprocessing.tables.categorytree;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableCreateCommand;

public class CategoryTreeCreate extends TableCreateCommand {

	@Override
	public void createTable() throws SQLException {

		// try {
		// this.database.executeUpdateQuery("drop table " + tableName + ";");
		// } catch (SQLException e) {
		// logger.warn("wiki-plug-in." + tableName + " : Cannot drop table " +
		// tableName
		// + ", perhaps it doesn't exists. Doesn't matter ;)");
		// }

		database.executeUpdateQuery("create table `" + this.tableName + "` (" + "CAT$CHILD VARCHAR(255) NOT NULL, "
				+ "CAT$PARENT VARCHAR (255) not null ) " + "ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");

	}

	@Override
	public void setTableName() {
		tableName = "CATEGORYTREE";
	}

}
