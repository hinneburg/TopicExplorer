package cc.topicexplorer.database.tables.term;

/** MIT-JOOQ-START 
 import static jooq.generated.Tables.TERM;
 import static jooq.generated.Tables.DOCUMENT_TERM_TOPIC;
 import static jooq.generated.Tables.DOCUMENT; 
 MIT-JOOQ-ENDE */
import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;

import com.google.common.collect.Sets;

public class TermFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(TermFill.class);

	@Override
	public void fillTable() {
// @formatter:off
/** MIT-JOOQ-START 
		String query = "insert into `"
				+ TERM.getName()
				+ "` (`"
				+ TERM.TERM_NAME.getName()
				+ "`, `"
				+ TERM.CORPUS_FREQUENCY.getName()
				+ "`, `"
				+ TERM.DOCUMENT_FREQUENCY.getName()
				+ "`, `"
				+ TERM.INVERSE_DOCUMENT_FREQUENCY.getName()
				+ "`, `"
				+ TERM.CF_IDF.getName()
				+ "`) select `nested1`.`"
				+ DOCUMENT_TERM_TOPIC.TERM.getName()
				+ "`, `nested1`.`cfcount`, `nested1`.`dfcount`, log(10, ((select count(*) as `numberOfDocuments` from `"
				+ DOCUMENT.getName() + "`) / `nested1`.`dfcount`))"
				+ ", (log(10, ((select count(*) as `numberOfDocuments` from `"
				+ DOCUMENT.getName()
				+ "`) / `nested1`.`dfcount`)) * `nested1`.`cfcount`)"
				+ " from (select `" + DOCUMENT_TERM_TOPIC.TERM.getName()
				+ "`, count(*) as `cfcount`, count(distinct `"
				+ DOCUMENT_TERM_TOPIC.DOCUMENT_ID.getName()
				+ "`) as `dfcount` from `" + DOCUMENT_TERM_TOPIC.getName()
				+ "` group by `" + DOCUMENT_TERM_TOPIC.TERM.getName()
				+ "`) as `nested1`";

		database.executeUpdateQuery(query);
		database.executeUpdateQuery("ALTER TABLE " + TERM.getName()
				+ "	ADD UNIQUE KEY `TERM_NAME_IDX` (`"
				+ TERM.TERM_NAME.getName() + "`)," + "	ADD KEY DF_IDX ("
				+ TERM.DOCUMENT_FREQUENCY.getName() + "),"
				+ "	ADD KEY CD_IDX (" + TERM.CORPUS_FREQUENCY.getName() + "),"
				+ "	ADD KEY IDF_IDX ("
				+ TERM.INVERSE_DOCUMENT_FREQUENCY.getName() + "),"
				+ "	ADD KEY CF_IDF_IDX (" + TERM.CF_IDF.getName() + ")");
 MIT-JOOQ-ENDE */ 
		// @formatter:on
		/** OHNE_JOOQ-START */
//		@formatter:off
		String query = "insert into "
				+ "TERM"
				+ " ("
				+ "TERM.TERM_NAME"
				+ ", "
				+ "TERM.CORPUS_FREQUENCY"
				+ ", "
				+ "TERM.DOCUMENT_FREQUENCY"
				+ ", "
				+ "TERM.INVERSE_DOCUMENT_FREQUENCY"
				+ ", "
				+ "TERM.CF_IDF"
				+ ") select nested1."
				+ "TERM"
				+ ", nested1.cfcount, nested1.dfcount, log(10, ((select count(*) as numberOfDocuments from "
				+ "DOCUMENT" + ") / nested1.dfcount))"
				+ ", (log(10, ((select count(*) as numberOfDocuments from "
				+ "DOCUMENT"
				+ ") / nested1.dfcount)) * nested1.cfcount)"
				+ " from (select " + "DOCUMENT_TERM_TOPIC.TERM"
				+ ", count(*) as cfcount, count(distinct "
				+ "DOCUMENT_TERM_TOPIC.DOCUMENT_ID"
				+ ") as dfcount from " + "DOCUMENT_TERM_TOPIC"
				+ " group by " + "DOCUMENT_TERM_TOPIC.TERM"
				+ ") as nested1";
//		@formatter:on

		try {
			database.executeUpdateQuery(query);
//		@formatter:off
			database.executeUpdateQuery("ALTER TABLE " + "TERM"
					+ "	ADD UNIQUE KEY TERM_NAME_IDX ("
					+ "TERM_NAME" + ")," + "	ADD KEY DF_IDX ("
					+ "DOCUMENT_FREQUENCY" + "),"
					+ "	ADD KEY CD_IDX (" + "CORPUS_FREQUENCY" + "),"
					+ "	ADD KEY IDF_IDX ("
					+ "INVERSE_DOCUMENT_FREQUENCY" + "),"
					+ "	ADD KEY CF_IDF_IDX (" + "CF_IDF" + ")");
//		@formatter:on
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be filled properly.");
			throw new RuntimeException(e);
		}
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public void setTableName() {

		/**
		 * MIT-JOOQ-START this.tableName = TERM.getName(); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		this.tableName = "TERM";
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("TermCreate", "DocumentTermTopicFill", "DocumentFill");

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
