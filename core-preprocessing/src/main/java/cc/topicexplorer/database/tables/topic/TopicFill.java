package cc.topicexplorer.database.tables.topic;

/** MIT-JOOQ-START 
 import static jooq.generated.Tables.TOPIC;
 import static jooq.generated.Tables.DOCUMENT_TERM_TOPIC; 
 MIT-JOOQ-ENDE */
import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;

import com.google.common.collect.Sets;

public class TopicFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(TopicFill.class);

	@Override
	public void setTableName() {
		/**
		 * MIT-JOOQ-START tableName = TOPIC.getName(); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		tableName = "TOPIC";
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public void fillTable() {
		/**
		 * MIT-JOOQ-START database.executeUpdateQuery("insert into `" + TOPIC.getName() + "` (`" +
		 * TOPIC.TOPIC_ID.getName() + "`, `" + TOPIC.NUMBER_OF_TOKENS.getName() + "`) select `" +
		 * DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + "`, count(*) from `" + DOCUMENT_TERM_TOPIC.getName() +
		 * "` group by `" + DOCUMENT_TERM_TOPIC.TOPIC_ID.getName() + "`"); database.executeUpdateQuery("ALTER TABLE " +
		 * TOPIC.getName() + " ADD KEY IDX0 (`" + TOPIC.TOPIC_ID.getName() + "`)"); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		try {
//			@formatter:off
			database.executeUpdateQuery("insert into " + "TOPIC"
					+ " (" + "TOPIC.TOPIC_ID"
					+ ", " + "TOPIC.NUMBER_OF_TOKENS"
					+ ") select " +  "DOCUMENT_TERM_TOPIC.TOPIC_ID"
					+ ", count(*) from " + "DOCUMENT_TERM_TOPIC"
					+ " group by " + "DOCUMENT_TERM_TOPIC.TOPIC_ID" + "");
			database.executeUpdateQuery("ALTER TABLE " + "TOPIC"
					+ " ADD KEY IDX0 (`" + "TOPIC_ID" + "`)");
//			@formatter:on
			/** OHNE_JOOQ-ENDE */
		} catch (SQLException e) {
			logger.error("Table " + "TOPIC" + " could not be filled properly.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("TopicCreate", "DocumentTermTopicFill");
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
