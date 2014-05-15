package cc.topicexplorer.plugin.frame.preprocessing.tables.frames;

import java.sql.SQLException;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.base.Preconditions;

public final class FrameCreate extends TableCreateCommand {

	@Override
	public void createTable() {
		Preconditions.checkState(this.tableName != null, "Table name has not been set, yet");
		try {
			this.database
					.executeUpdateQuery("CREATE TABLE "
							+ this.tableName
							+ " (DOCUMENT_ID INT, TOPIC_ID INT, FRAME VARCHAR(255), START_POSITION INT, END_POSITION INT) DEFAULT CHARSET=utf8 COLLATE=utf8_bin");
		} catch (SQLException e) {
			this.logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		this.tableName = "FRAMES";
	}
}