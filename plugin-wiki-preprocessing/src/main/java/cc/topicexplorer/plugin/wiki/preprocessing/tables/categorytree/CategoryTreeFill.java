package cc.topicexplorer.plugin.wiki.preprocessing.tables.categorytree;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableFillCommand;
import cc.topicexplorer.plugin.wiki.preprocessing.CategoryResolver;

public class CategoryTreeFill extends TableFillCommand {

	@Override
	public void fillTable() throws SQLException {

		String inFilePath = this.properties.getProperty("Wiki_outputFolder") + "/" + CategoryResolver.categoryFileName;

		String header = " CAT$CHILD , CAT$PARENT ";

		try {
			logger.info("wiki-plug-in." + tableName + " :  filling table");

			database.executeUpdateQuery("LOAD DATA LOCAL INFILE '" + inFilePath + "' IGNORE INTO TABLE " + tableName
					+ " CHARACTER SET utf8 FIELDS TERMINATED BY ';' ENCLOSED BY '\"'   " + "LINES TERMINATED BY '\n' ("
					+ header + " );");
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}

		// noch index hinzufügen!

	}

	@Override
	public void setTableName() {
		tableName = "CATEGORYTREE";

	}

	@Override
	public void addDependencies() {

		beforeDependencies.add("Wiki_CategoryResolver");

	}

}
