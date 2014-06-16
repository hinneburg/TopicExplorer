package cc.topicexplorer.plugin.wiki.preprocessing.tables.categorytree;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;
import cc.topicexplorer.plugin.wiki.preprocessing.CategoryResolver;

import com.google.common.collect.Sets;

public class CategoryTreeFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(CategoryTreeFill.class);

	@Override
	public void fillTable() {
		String inFilePath = this.properties.getProperty("Wiki_outputFolder") + "/" + CategoryResolver.categoryFileName;
		String header = " CAT$CHILD , CAT$PARENT ";

		try {
			logger.info("wiki-plug-in." + tableName + " :  filling table");

			database.executeUpdateQuery("LOAD DATA LOCAL INFILE '" + inFilePath + "' IGNORE INTO TABLE " + tableName
					+ " CHARACTER SET utf8 FIELDS TERMINATED BY ';' ENCLOSED BY '\"'   " + "LINES TERMINATED BY '\n' ("
					+ header + " );");
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be filled properly.");
			throw new RuntimeException(e);
		}

		// TODO add index!

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
		return Sets.newHashSet("Wiki_CategoryResolver");
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
