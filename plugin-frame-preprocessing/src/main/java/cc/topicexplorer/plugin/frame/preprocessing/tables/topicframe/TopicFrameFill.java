package cc.topicexplorer.plugin.frame.preprocessing.tables.topicframe;

/** MIT-JOOQ-START 
 import static jooq.generated.Tables.TOPIC;
 MIT-JOOQ-ENDE */
import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;

import com.google.common.collect.Sets;

public class TopicFrameFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(TopicFrameFill.class);

	@Override
	public void fillTable() {
		try {
			this.prepareMetaDataAndFillTable();
		} catch (SQLException sqlEx) {
			logger.error("Table " + this.tableName + " could not be filled properly.");
			throw new RuntimeException(sqlEx);
		}
	}

	private void prepareMetaDataAndFillTable() throws SQLException {
		database.executeUpdateQuery("INSERT INTO "
				+ this.tableName
				+ " SELECT COUNT(*) AS FRAME_COUNT, COUNT(DISTINCT FRAME) AS UNIQUE_FRAME_COUNT," 
				+ " TOPIC_ID, FRAME_TYPE FROM `FRAME$FRAMES` WHERE ACTIVE=1 GROUP BY TOPIC_ID, FRAME_TYPE");
	}

	@Override
	public void setTableName() {
		/**
		 * MIT-JOOQ-START this.tableName = TOPIC.getName(); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		this.tableName = "FRAME$TOPIC_FRAME";
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("TopicFill", "TopicMetaData", "FrameFill", "TopicFrameCreate");
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
