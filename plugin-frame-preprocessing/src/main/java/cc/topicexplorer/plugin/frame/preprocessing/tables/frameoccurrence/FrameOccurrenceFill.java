package cc.topicexplorer.plugin.frame.preprocessing.tables.frameoccurrence;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;
import cc.topicexplorer.plugin.frame.preprocessing.implementation.FrameOccurrence;
import cc.topicexplorer.plugin.frame.preprocessing.implementation.FrameOccurrence.tbl.FRAME$FRAME_OCCURRENCE;

import com.google.common.collect.Sets;

public class FrameOccurrenceFill extends TableFillCommand {
	private static final Logger logger = Logger
			.getLogger(FrameOccurrenceFill.class);

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet(
				"Frame_FrameOccurrenceCreate",
				"Frame_FrametypeFill",
				"Frame_DocumentChunkFill",
				"HierarchicalTopic_TopicFill",
				"DocumentTermTopicFill");
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
	public void fillTable() {
		try {
			database.executeUpdateQuery(
					FrameOccurrence.getInsertTableStatement()
			);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQL Exception while filling " + this.tableName + " table.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		this.tableName = FRAME$FRAME_OCCURRENCE.name;
	}


}
