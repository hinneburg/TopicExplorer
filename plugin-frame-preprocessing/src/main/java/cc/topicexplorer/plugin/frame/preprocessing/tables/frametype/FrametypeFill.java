package cc.topicexplorer.plugin.frame.preprocessing.tables.frametype;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;
import cc.topicexplorer.plugin.frame.preprocessing.implementation.FrameCommon;
import cc.topicexplorer.plugin.frame.preprocessing.implementation.Frametype;

import com.google.common.collect.Sets;

public class FrametypeFill extends TableFillCommand {
	private static final Logger logger = Logger
			.getLogger(FrametypeFill.class);

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("Frame_FrametypeCreate");
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
					Frametype.getInsertFrameTypes(properties)
			);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQL Exception while filling " + this.tableName + " table.");
			throw new RuntimeException(e);
		}
	}
	
	
	@Override
	public void setTableName() {
		this.tableName = FrameCommon.pluginPrefix + FrameCommon.delimiter
				+ Frametype.tableName;
	}


}
