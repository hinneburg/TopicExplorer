package cc.topicexplorer.plugin.wiki.preprocessing.tables.categorytree;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.collect.Sets;

public class CategoryTreeCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(CategoryTreeCreate.class);

	@Override
	public void createTable() {
		// drop table
		try {
			this.database.executeUpdateQuery("drop table " + this.tableName + ";");
		} catch (SQLException e) {
			if (e.getErrorCode() != 1051) { // MySQL Error code for 'Can't
				// DROP
				// ..; check that table exists
				logger.error("wiki-plug-in." + this.tableName + ": Cannot drop table " + this.tableName);
				throw new RuntimeException(e);
			}
		}

		// create table
		try {
			database.executeUpdateQuery("create table `" + this.tableName + "` (" + "CAT$CHILD VARCHAR(255) NOT NULL, "
					+ "CAT$PARENT VARCHAR (255) not null ) " + "ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		tableName = "CATEGORYTREE";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet();
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
