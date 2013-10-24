package cc.topicexplorer.plugin.wiki.preprocessing.tables.orgtable;

import java.sql.SQLException;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.commands.DependencyCommand;
import cc.topicexplorer.chain.commands.TableCommand;

public class OrgTableCreateAndFill extends TableCommand {


	@Override
	public void addDependencies() {
			
		beforeDependencies.add("Wiki_PreMallet");
		
		afterDependencies.add("DocumentFill");

	}

	@Override
	public void tableExecute(Context context) {
		String tableName = this.properties.getProperty("OrgTableName");

		// drop table

		try {
			this.database.executeUpdateQuery("drop table " + tableName + ";");
		} catch (SQLException e) {
			logger.warn("wiki-plug-in." + tableName + " : Cannot drop table "
					+ tableName
					+ ", perhaps it doesn't exists. Doesn't matter ;)");
		}

		String sql = "CREATE TABLE `" + tableName + "` (" + "`"
				+ this.properties.getProperty("OrgTableId")
				+ "` bigint(11) NOT NULL, " + "`"
				+ this.properties.getProperty("OrgTableTxt")
				+ "` text COLLATE utf8_bin NOT NULL, " + "`"
				+ this.properties.getProperty("Text_OrgTableTitle")
				+ "` text COLLATE utf8_bin NOT NULL , "
				+ " UNIQUE KEY `id` (`id`) "
				+ " ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin; ";

		// create table

		try {
			this.database.executeUpdateQuery(sql);
		} catch (SQLException e) {
			logger.fatal("wiki-plug-in." + tableName
					+ ": cannot create table \n. Program will exit.\n" ) ;
			e.printStackTrace();
			System.exit(0);
		}

		// fill table with values from the output of the wiki plug-in

		sql = " START TRANSACTION ; "
				+ " LOAD DATA LOCAL INFILE "
				+ "'temp/inputsql.csv' "
				+ "INTO TABLE "
				+ tableName
				+ " "
				+ "CHARACTER set utf8 FIELDS TERMINATED BY ';' ENCLOSED BY '\"'   "
				+ "LINES TERMINATED BY '|s|q|l|e|n|d|i|n|g|\n' " + " ("
				+ this.properties.getProperty("OrgTableId") + ", "
				+ this.properties.getProperty("Text_OrgTableTitle") + ","
				+ this.properties.getProperty("OrgTableTxt") + " ) ; "
				+ "COMMIT;";

		System.out.println(sql);
		
		try {
			this.database.executeUpdateQuery(sql);
		} catch (SQLException e) {
			logger.fatal("wiki-plug-in."+tableName+": failure in filling the table.\n programm will exit.");
			e.printStackTrace();
			System.exit(1);
		}
		
	}

	@Override
	public void setTableName() {
		tableName = this.properties.getProperty("OrgTableName");		
	}

}
