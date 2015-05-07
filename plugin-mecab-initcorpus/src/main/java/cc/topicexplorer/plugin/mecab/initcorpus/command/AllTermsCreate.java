package cc.topicexplorer.plugin.mecab.initcorpus.command;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

import cc.topicexplorer.commands.TableCreateCommand;
import cc.topicexplorer.plugin.mecab.initcorpus.implementation.AllTermsCreateIterateDocuments;

public class AllTermsCreate extends TableCreateCommand {
	private static final Logger logger = Logger.getLogger(AllTermsCreate.class);

	@Override
	public void createTable() {
		AllTermsCreateIterateDocuments allTermsCreateIterateDocuments = new AllTermsCreateIterateDocuments();
		allTermsCreateIterateDocuments.runSqlFillQueriesIterateDocuments(this.database, 1000,10);
//		runSqlFillQueriesSimple();
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
//	public static void main(String[] args){
//		String strategy = args[0];
//		if (strategy.contentEquals("simple")) {
//			generateFillQueriesSimple();
//		} else if (strategy.contentEquals("iterateDocuments")) {
//			generateFillQueriesIterateDocuments(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
//		} else if (strategy.contentEquals("iteratePos")) {
//			generateFillQueriesIteratePos();
//		} else {
//			System.out.println("Strategy " + strategy + " unknown.");
//			return;
//		}
//
//		for (int i = 0; i < queries.size(); i++) {
//			System.out.println(queries.get(i) + "\n");
//		}
//		return;
//	}

	@SuppressWarnings("unused")
	private void runSqlFillQueriesSimple() {
		try {
				logger.info("Start Fill Query");
				this.database.executeUpdateQuery(
						"CREATE TABLE \n"
								+ "ALL_TERMS \n"
								+ " AS \n"
								+ "SELECT DOCUMENT_TERM.TERM, COUNT(DISTINCT DOCUMENT_TERM.DOCUMENT_ID) AS COUNT, p2.POS \n"
								+ "FROM DOCUMENT_TERM, POS_TYPE p1, POS_TYPE p2 \n"
								+ "WHERE p1.POS=DOCUMENT_TERM.WORDTYPE_CLASS AND p1.LOW>=p2.LOW \n"
								+ "AND p1.HIGH<=p2.HIGH \n"
								+ "GROUP BY DOCUMENT_TERM.TERM, p2.POS;\n"
						);
				logger.info("Finished Fill Query");
				logger.info("Start Create Index");
				this.database.executeUpdateQuery(
						"CREATE INDEX TERM_POS_IDX ON DOCUMENT_TERM(TERM,POS,COUNT);"
						);
				logger.info("Finished Create Index");
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}

	}

	private static void generateFillQueriesIteratePos() {

	}

	private void runSqlFillQueriesIteratePos() {

	}

}
