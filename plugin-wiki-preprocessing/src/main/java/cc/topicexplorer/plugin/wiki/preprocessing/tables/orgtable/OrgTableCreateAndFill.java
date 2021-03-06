package cc.topicexplorer.plugin.wiki.preprocessing.tables.orgtable;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableCommand;

import com.google.common.collect.Sets;

public class OrgTableCreateAndFill extends TableCommand {

	private static final Logger logger = Logger.getLogger(OrgTableCreateAndFill.class);

	@Override
	public void tableExecute(Context context) {
		String tableName = this.properties.getProperty("OrgTableName");

		// drop table

		try {
			this.database.executeUpdateQuery("drop table " + tableName + ";");
		} catch (SQLException e) {
			if (e.getErrorCode() != 1051) { // MySQL Error code for 'Can't
				// DROP
				// ..; check that column/key exists
				logger.error("wiki-plug-in." + this.tableName + ": Cannot drop table " + this.tableName);
				throw new RuntimeException(e);

			}
		}

		String sql = "CREATE TABLE `" + tableName + "` (" + "`" + this.properties.getProperty("OrgTableId")
				+ "` bigint(11) NOT NULL, " + "`" + this.properties.getProperty("OrgTableTxt")
				+ "` text NOT NULL, " + "`" + this.properties.getProperty("Text_OrgTableTitle")
				+ "` text NOT NULL , " + " UNIQUE KEY `id` (`id`) "
				+ " ) ENGINE=InnoDB; ";

		// create table

		logger.info("wiki-plug-in." + tableName + " :  filling ");
		try {
			this.database.executeUpdateQuery(sql);
		} catch (SQLException e) {
			logger.error("wiki-plug-in." + tableName + ": cannot create table \n. Program will exit.\n");
			throw new RuntimeException(e);
		}

		// fill table with values from the output of the wiki plug-in
		sql = " LOAD DATA LOCAL INFILE " + "'" + properties.getProperty("Wiki_outputFolder")
				+ System.getProperty("file.separator") + "inputsql.csv" + "' " + "INTO TABLE " + tableName + " "
				+ "CHARACTER set utf8 FIELDS TERMINATED BY ';' ENCLOSED BY '\"'   "
				+ "LINES TERMINATED BY '|s|q|l|e|n|d|i|n|g|\n' " + " (" + this.properties.getProperty("OrgTableId")
				+ ", " + this.properties.getProperty("Text_OrgTableTitle") + ","
				+ this.properties.getProperty("OrgTableTxt") + " ) ; ";

		try {
			this.database.executeUpdateQuery(sql);
		} catch (SQLException e) {
			logger.error("wiki-plug-in." + tableName + ": failure in filling the table.\n programm will exit.");
			throw new RuntimeException(e);
		}

	}

	@Override
	public void setTableName() {
		tableName = this.properties.getProperty("OrgTableName");
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("DocumentFill");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("Wiki_PreMallet");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}

}
