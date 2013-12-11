package cc.topicexplorer.plugin.frame.preprocessing.tables.frames;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableCreateCommand;

import com.google.common.base.Preconditions;

public final class FrameCreate extends TableCreateCommand {

	@Override
	public void createTable() {
		Preconditions.checkState(this.tableName != null, "Table name is not set, yet");
		try {
			this.database.executeUpdateQuery("CREATE TABLE " + this.tableName
					+ " (DOCUMENT_ID INT, TOPIC_ID INT, FRAME VARCHAR(255), START_POSITION INT, END_POSITION INT)");
		} catch (SQLException e) {
			this.logger.error("Table Frames could not be created.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		this.tableName = "Frames";
	}
}