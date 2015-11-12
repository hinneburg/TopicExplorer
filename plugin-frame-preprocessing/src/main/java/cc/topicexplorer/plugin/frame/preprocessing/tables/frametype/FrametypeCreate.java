package cc.topicexplorer.plugin.frame.preprocessing.tables.frametype;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;
import cc.topicexplorer.plugin.frame.preprocessing.implementation.FrameCommon;
import cc.topicexplorer.plugin.frame.preprocessing.implementation.Frametype;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public class FrametypeCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(FrametypeCreate.class);

	@Override
	public void createTable() {
		Preconditions.checkState(this.tableName != null, "Table name has not been set, yet");
		try {
			this.database
					.executeUpdateQuery( Frametype.getCreateTableStatement() );
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		this.tableName = FrameCommon.pluginPrefix + 
				FrameCommon.delimiter + 
				Frametype.tableName;
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
