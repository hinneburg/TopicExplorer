package cc.topicexplorer.database.tables.termtopic;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.collect.Sets;

public class TermTopicCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(TermTopicCreate.class);

	@Override
	public void createTable() {
		try {
			database.executeUpdateQuery("create table `" + this.tableName + "` ("
					+ "TERM_ID INTEGER(11) NOT NULL, TOPIC_ID INTEGER(11) NOT NULL, "
					+ "NUMBER_OF_TOKEN_TOPIC int(11) NOT NULL, "
					+ "PR_TOPIC_GIVEN_TERM DOUBLE NOT NULL, PR_TERM_GIVEN_TOPIC DOUBLE) "
					+ "ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin; ");
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		tableName = "TERM_TOPIC";
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
