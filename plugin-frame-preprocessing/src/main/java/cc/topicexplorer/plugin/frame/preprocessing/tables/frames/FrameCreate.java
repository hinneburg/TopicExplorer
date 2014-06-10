package cc.topicexplorer.plugin.frame.preprocessing.tables.frames;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public final class FrameCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(FrameCreate.class);

	@Override
	public void createTable() {
		Preconditions.checkState(this.tableName != null, "Table name has not been set, yet");
		try {
			this.database
					.executeUpdateQuery("CREATE TABLE "
							+ this.tableName
							+ " (DOCUMENT_ID INT, TOPIC_ID INT, FRAME VARCHAR(255), START_POSITION INT, END_POSITION INT, ACTIVE BOOLEAN NOT NULL DEFAULT 1) DEFAULT CHARSET=utf8 COLLATE=utf8_bin");
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		this.tableName = "FRAMES";
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
