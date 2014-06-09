package cc.topicexplorer.database.tables.document;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.collect.Sets;

public class DocumentCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(DocumentCreate.class);

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
