package cc.topicexplorer.plugin.frame.preprocessing.tables.delimiterpositions;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public final class DelimiterPositionsCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(DelimiterPositionsCreate.class);

	@Override
	public void createTable() {
		Preconditions.checkState(this.tableName != null, "Table name has not been set, yet");
		try {
			this.database
					.executeUpdateQuery("CREATE TABLE "
							+ this.tableName
							+ " (DOCUMENT_ID INT, POSITION INT)");
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		this.tableName = "FRAME$DELIMITER_POSITIONS";
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
