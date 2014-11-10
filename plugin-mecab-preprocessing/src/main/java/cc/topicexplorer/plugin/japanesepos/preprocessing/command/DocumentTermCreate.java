package cc.topicexplorer.plugin.japanesepos.preprocessing.command;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.collect.Sets;

public class DocumentTermCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(DocumentTermCreate.class);

	@Override
	public void createTable() {
		try {
			this.database.executeUpdateQuery(" CREATE TABLE `" + this.tableName + "` ("
					+ "`DOCUMENT_ID` INT(11) NOT NULL, "
					+ "`POSITION_OF_TOKEN_IN_DOCUMENT` INT(11) NOT NULL, "
					+ "`TERM` varchar(255) COLLATE utf8_bin NOT NULL, "
					+ "`TOKEN` varchar(255) COLLATE utf8_bin NOT NULL, "
					+ "`WORDTYPE_CLASS` INT(11) NOT NULL, "
					+ "`CONTINUATION` INT(11) NOT NULL "
					+ ") ENGINE=InnoDB CHARSET=utf8 COLLATE=utf8_bin ;");
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		this.tableName = "DOCUMENT_TERM";
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
