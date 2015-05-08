package cc.topicexplorer.plugin.hierarchicaltopic.preprocessing.tables.topic;

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
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName
					+ " ADD COLUMN HIERARCHICAL_TOPIC$START INT(11) , "
					+ " ADD COLUMN HIERARCHICAL_TOPIC$END INT(11) , "
					+ " ADD COLUMN HIERARCHICAL_TOPIC$DEPTH INT(11) , "
					+ " ADD COLUMN HIERARCHICAL_TOPIC$CLUSTER_MEMBERSHIP TEXT");
		} catch (SQLException e) {
			logger.error("Columns HIERARCHICAL_TOPIC$START, $END, $DEPTH, $CLUSTER_MEMBERSHIP could not be added to table "
					+ this.tableName);
			throw new RuntimeException(e);
		}
	}

	private void dropColumns() {
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName + " DROP COLUMN HIERARCHICAL_TOPIC$START"
					+ ", DROP COLUMN HIERARCHICAL_TOPIC$END" + ", DROP COLUMN HIERARCHICAL_TOPIC$DEPTH"
					+ ", DROP COLUMN HIERARCHICAL_TOPIC$CLUSTER_MEMBERSHIP");
		} catch (SQLException e) {
			if (e.getErrorCode() != 1091) { // MySQL Error code for 'Can't DROP
				// ..; check that column/key exists
				logger.error("TopicMetaData.dropColumns: Cannot drop column.");
				throw new RuntimeException(e);
			} else {
				logger.info("dropColumns: ignored SQL-Exception with error code 1091.");
			}
		}
	}

	@Override
	public void dropTable() {
		this.dropColumns();
	}

	@Override
	public void setTableName() {
		this.tableName = "TOPIC";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("TopicCreate");
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
