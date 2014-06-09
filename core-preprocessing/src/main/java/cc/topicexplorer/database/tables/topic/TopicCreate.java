package cc.topicexplorer.database.tables.topic;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.collect.Sets;

public class TopicCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(TopicCreate.class);

	@Override
	public void createTable() {
		try {
			database.executeUpdateQuery(String.format(" CREATE TABLE `%s` (  `TOPIC_ID` int(11) NOT NULL,"
					+ "  `NUMBER_OF_TOKENS` int(11) NOT NULL"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ;", this.tableName));
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		tableName = "TOPIC";
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
