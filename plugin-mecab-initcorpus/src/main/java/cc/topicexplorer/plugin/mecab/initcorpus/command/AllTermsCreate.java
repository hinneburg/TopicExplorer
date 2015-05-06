package cc.topicexplorer.plugin.mecab.initcorpus.command;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

import cc.topicexplorer.commands.TableCreateCommand;

public class AllTermsCreate extends TableCreateCommand {
	private static final Logger logger = Logger.getLogger(AllTermsCreate.class);
	private ArrayList<String> queries = new ArrayList<String>();

	@Override
	public void createTable() {
		runSqlFillQueriesSimple();
	}

	@Override
	public void setTableName() {
		this.tableName = "ALL_TERMS";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentTermFill", "PosTypeFill");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}

	public void printQueries(String strategy) {
		if (strategy.contentEquals("simple")) {
			generateFillQueriesSimple();
		} else if (strategy.contentEquals("iterateDocuments")) {
			generateFillQueriesIterateDocuments();
		} else if (strategy.contentEquals("iteratePos")) {
			generateFillQueriesIteratePos();
		} else {
			System.out.println("Strategy " + strategy + "unknown");
			return;
		}

		for (int i = 0; i < queries.size(); i++) {
			System.out.println(queries.get(i) + "\n");
		}
		return;
	}

	private void generateFillQueriesSimple() {
		queries.add("CREATE TABLE `"
				+ this.tableName
				+ "` AS "
				+ "SELECT DOCUMENT_TERM.TERM, COUNT(DISTINCT DOCUMENT_TERM.DOCUMENT_ID) AS COUNT, p2.POS "
				+ "FROM DOCUMENT_TERM, POS_TYPE p1, POS_TYPE p2 "
				+ "WHERE p1.POS=DOCUMENT_TERM.WORDTYPE_CLASS AND p1.LOW>=p2.LOW "
				+ "AND p1.HIGH<=p2.HIGH "
				+ "GROUP BY DOCUMENT_TERM.TERM, p2.POS");
	}

	private void runSqlFillQueriesSimple() {

		try {
				logger.info("Start Query");
				this.database.executeUpdateQuery(queries.get(0));
				logger.info("Finished Query");
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}

	}

	private void generateFillQueriesIterateDocuments() {

	}

	private void runSqlFillQueriesIterateDocuments() {

		// Simple idea: 1) create table ,
		// 2) compute min and max of document ids
		// 3) run the expensive query constraint to a small enough subset of
		// document and insert into table
		// 4) aggregate intermediate results by
		// summing document counts (nop error can happen, as complete documents
		// are processed)

		// INSERT INTO ALL_TERMS_temp
		// SELECT
		// DOCUMENT_TERM.TERM,
		// COUNT(DISTINCT DOCUMENT_TERM.DOCUMENT_ID) AS COUNT,
		// p2.POS
		// FROM
		// DOCUMENT_TERM,
		// POS_TYPE p1,
		// POS_TYPE p2
		// WHERE
		// p1.POS=DOCUMENT_TERM.WORDTYPE_CLASS AND
		// p1.LOW>=p2.LOW AND p1.HIGH<=p2.HIGH AND
		// 1000<=DOCUMENT_TERM.DOCUMENT_ID and DOCUMENT_TERM.DOCUMENT_ID<2000
		// GROUP BY
		// DOCUMENT_TERM.TERM,
		// p2.POS
		// ;
		// INSERT INTO ALL_TERMS
		// SELECT
		// TERM,
		// SUM(COUNT) AS COUNT,
		// POS
		// FROM
		// ALL_TERMS_temp
		// GROUP BY TERM,POS
		// ;

	}

	private void generateFillQueriesIteratePos() {

	}

	private void runSqlFillQueriesIteratePos() {

	}

}
