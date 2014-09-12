package cc.topicexplorer.plugin.frame.preprocessing.tables.topicframe;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.collect.Sets;

public class TopicFrameCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(TopicFrameCreate.class);

	@Override
	public void createTable() {
		try {
			this.database.executeUpdateQuery("CREATE TABLE "
							+ this.tableName
							+ " (FRAME_COUNT INT, UNIQUE_FRAME_COUNT INT, TOPIC_ID INT, FRAME_TYPE VARCHAR(255))");
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		this.tableName = "FRAME$TOPIC_FRAME";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("Frame_TopicFrameFill");
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
