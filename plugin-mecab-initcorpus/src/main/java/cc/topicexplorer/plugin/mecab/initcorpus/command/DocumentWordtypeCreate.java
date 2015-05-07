package cc.topicexplorer.plugin.mecab.initcorpus.command;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.collect.Sets;

public class DocumentWordtypeCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(DocumentWordtypeCreate.class);

	@Override
	public void createTable() {
		try {
			this.database.executeUpdateQuery(" CREATE TABLE `" + this.tableName + "` ("
					+ "`DOCUMENT_ID` INT(11) NOT NULL, "
					+ "`WORDTYPE_CLASS` INT(11) NOT NULL, "
					+ "`TOKEN_COUNT` INT(11) NOT NULL, "
					+ "`TERM_COUNT` INT(11) NOT NULL, "
					+ "`MIN_TOKEN_LENGTH` INT(11) NOT NULL, "
					+ "`MAX_TOKEN_LENGTH` INT(11) NOT NULL, "
					+ "`SUM_TOKEN_LENGTH` INT(11) NOT NULL "
					+ ") ENGINE=InnoDB;");			
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		this.tableName = "DOCUMENT_WORDTYPE";
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
