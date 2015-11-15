package cc.topicexplorer.plugin.frame.preprocessing.tables.frameoccurrence;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;
import cc.topicexplorer.plugin.frame.preprocessing.implementation.FrameOccurrence;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import cc.topicexplorer.plugin.frame.preprocessing.implementation.FrameOccurrence.tbl.FRAME$FRAME_OCCURRENCE;


public class FrameOccurrenceCreate  extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(FrameOccurrenceCreate.class);

	@Override
	public void createTable() {
		Preconditions.checkState(this.tableName != null, "Table name has not been set, yet");
		try {
			this.database
					.executeUpdateQuery( FrameOccurrence.getCreateTableStatement() );
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		this.tableName = FRAME$FRAME_OCCURRENCE.name;
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
