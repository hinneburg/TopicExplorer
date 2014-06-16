package cc.topicexplorer.database.tables.documenttopic;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.collect.Sets;

public class DocumentTopicCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(DocumentTopicCreate.class);

	@Override
	public void createTable() {
		try {
//			@formatter:off
			database.executeUpdateQuery("CREATE TABLE `"
					+ this.tableName
					+ "` ( "
					+ " `DOCUMENT_ID` INTEGER(11) NOT NULL, "
					+ " `TOPIC_ID` int(11) NOT NULL, "
					+ " `NUMBER_OF_TOKEN_TOPIC_IN_DOCUMENT` bigint(21) NOT NULL DEFAULT '0', "
					+ "	 PR_TOPIC_GIVEN_DOCUMENT DOUBLE,"
					+ "	 PR_DOCUMENT_GIVEN_TOPIC DOUBLE"
					+ "	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");
//			@formatter:on
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		tableName = "DOCUMENT_TOPIC";
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
