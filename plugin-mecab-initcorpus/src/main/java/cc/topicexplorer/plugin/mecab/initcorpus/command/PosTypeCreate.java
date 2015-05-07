package cc.topicexplorer.plugin.mecab.initcorpus.command;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

import cc.topicexplorer.commands.TableCreateCommand;

public class PosTypeCreate  extends TableCreateCommand{
	private static final Logger logger = Logger.getLogger(PosTypeCreate.class);

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

	@Override
	public void createTable() {
		try {
			this.database.executeUpdateQuery(" CREATE TABLE `" + this.tableName + "` ("
					+ "`POS` int(11) NOT NULL DEFAULT '0',"
					+ "`LOW` int(11) DEFAULT NULL,"
					+ "`HIGH` int(11) DEFAULT NULL, "
					+ "`DESCRIPTION` varchar(255), "
					+ "`PARENT_POS` int(11) DEFAULT NULL, "
					+ "TOKEN_COUNT int(11) DEFAULT NULL, "
					+ "DOCUMENT_COUNT int(11) DEFAULT NULL, "
					+ "TERM_COUNT int(11) DEFAULT NULL, "
					+ "MIN_TOKEN_LENGTH int(11) DEFAULT NULL, "
					+ "MAX_TOKEN_LENGTH int(11) DEFAULT NULL, "
					+ "AVG_TOKEN_LENGTH float(4,1) DEFAULT NULL, "
					+ "PRIMARY KEY (`POS`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ;");
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public void setTableName() {
		this.tableName = "POS_TYPE";
		
	}

}
