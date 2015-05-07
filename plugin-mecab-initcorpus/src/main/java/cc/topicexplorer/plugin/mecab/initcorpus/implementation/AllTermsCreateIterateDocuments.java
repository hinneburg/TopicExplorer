package cc.topicexplorer.plugin.mecab.initcorpus.implementation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import cc.topicexplorer.database.Database;

public class AllTermsCreateIterateDocuments {
	private static final Logger logger = Logger.getLogger(AllTermsCreateIterateDocuments.class);
	private static ArrayList<String> queries = new ArrayList<String>();
	private static Integer minDocumentId=0, maxDocumentId=0;

	
	private static String generateCreateAllTermTmpTable() {
//		return "CREATE TABLE ALL_TERMS_TMP (TERM VARCHAR(255),COUNT INTEGER(11),POS INTEGER(11)) ENGINE=MEMORY;"; 
		return "CREATE TABLE ALL_TERMS_TMP (TERM VARCHAR(100),COUNT INTEGER(11),POS INTEGER(11)) ENGINE=INNODB;"; 
	}
	
	private static String generateInsertTermPosCountOfDocuments(Integer low, Integer high) {
		return 
				"INSERT INTO ALL_TERMS_TMP \n" +
				 "SELECT \n" +
				 "DOCUMENT_TERM.TERM, \n" +
				 "COUNT(DISTINCT DOCUMENT_TERM.DOCUMENT_ID) AS COUNT, \n" +
				 "p2.POS \n" +
				 "FROM \n" +
				 "DOCUMENT_TERM, \n" +
				 "POS_TYPE p1, \n" +
				 "POS_TYPE p2 \n" +
				 "WHERE \n" +
				 "p1.POS=DOCUMENT_TERM.WORDTYPE_CLASS AND \n" +
				 "p1.LOW>=p2.LOW AND p1.HIGH<=p2.HIGH AND \n" +
				 low.toString() + "<=DOCUMENT_TERM.DOCUMENT_ID and "+ 
				 "DOCUMENT_TERM.DOCUMENT_ID<"+ high.toString() + " \n" +
				 "GROUP BY \n" +
				 "DOCUMENT_TERM.TERM, \n" +
				 "p2.POS \n" +
				 ";";
	}
	
	private static String generateSumTermPosCountsInNewTmpTable() {
//		return "CREATE TABLE ALL_TERMS_TMP_NEW AS \n ENGINE=MEMORY AS \n" +
		return "CREATE TABLE ALL_TERMS_TMP_NEW AS \n ENGINE=INNODB AS \n" +
				 "SELECT \n" +
				 "TERM, \n" +
				 "SUM(COUNT) AS COUNT, \n" +
				 "POS \n" +
				 "FROM \n" +
				 "ALL_TERMS_TMP \n" +
				 "GROUP BY TERM,POS \n" +
				 "; \n"
				 ;
	}
	
	private static String generateDropAllTermsTmpTable() {
		return "DROP TABLE ALL_TERMS_TMP; \n";
	}
	
	private static String generateRenameNewTmpTableToTmpTable() {
		return "ALTER TABLE ALL_TERMS_TMP_NEW RENAME TO ALL_TERMS_TMP; \n";
	}
	
	private static void addAggregationQueries() {
		queries.add( generateSumTermPosCountsInNewTmpTable() );
		queries.add( generateDropAllTermsTmpTable() );
		queries.add( generateRenameNewTmpTableToTmpTable() );
	}
	
	private static String generateSumTermPosCountInFinalAllTerms() {
		return "CREATE TABLE ALL_TERMS ENGINE=INNODB AS \n" +
				 "SELECT \n" +
				 "TERM, \n" +
				 "SUM(COUNT) AS COUNT, \n" +
				 "POS \n" +
				 "FROM \n" +
				 "ALL_TERMS_TMP \n" +
				 "GROUP BY TERM,POS \n" +
				 "; \n"
				 ;
	}
	private static String generateCreateIndexTermPosCount() {
		return "CREATE INDEX TERM_POS_IDX ON ALL_TERMS(TERM,POS,COUNT);" ;
	}
	
	private static void generateFillQueriesIterateDocuments(Integer minDocumentId, Integer maxDocumentId, Integer documentIdStepSize, Integer numberOfStepsUntilAggregation) {
		if (queries.size()>0) {
			throw new RuntimeException("Query list should be empty.");
		}
		queries.add( generateCreateAllTermTmpTable() );
		
		for (Integer id=minDocumentId,step=0; id<maxDocumentId; id+=documentIdStepSize,step++) {
			queries.add( generateInsertTermPosCountOfDocuments(id, id+documentIdStepSize) );
			if (step>=numberOfStepsUntilAggregation) {
				addAggregationQueries();
				step=0;
			}
		}
		queries.add( generateSumTermPosCountInFinalAllTerms() );
		queries.add( generateDropAllTermsTmpTable() );
		queries.add( generateCreateIndexTermPosCount() );
		
		// Simple idea: 1) create table ,
		// 2) compute min and max of document ids
		// 3) run the expensive query constraint to a small enough subset of
		// document and insert into table
		// 4) aggregate intermediate results by
		// summing document counts (no error can happen, as complete documents
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

	private void getMinMaxDocumentIdRange(Database database) {
		try {
			logger.info("Start getMinMaxDocumentIdRange Query");
			ResultSet documentIdRange = database.executeQuery(
					"SELECT " +
				    "MIN(DOCUMENT_TERM.DOCUMENT_ID) as MIN_ID, " +
				    "MAX(DOCUMENT_TERM.DOCUMENT_ID) as MAX_ID " +
				    "FROM "+
				    "DOCUMENT_TERM;"
					);
			logger.info("Finished getMinMaxDocumentIdRange Query");
			
			documentIdRange.next();
			minDocumentId = documentIdRange.getInt("MIN_ID");
			maxDocumentId = documentIdRange.getInt("MAX_ID");
			
			logger.info("DOCUMENT_ID range [" + minDocumentId.toString() + ","
					+ maxDocumentId.toString() + "]");
			
		} catch (SQLException e) {
			logger.error("Error while getting MIN and MAX DOCUMENT_ID FROM DOCUMENT_TERM");
			throw new RuntimeException(e);
		}
		
	}
	
	private void executeFillQueries(Database database) {
		logger.info("Start execution of " + String.valueOf(queries.size()) + 
				" queries of the iterate-documents strategy " + 
				"to create, fill and index table ALL_TERMS.");
		for (Integer i=0;i<queries.size();i++) {
			try {
				database.executeUpdateQuery(queries.get(i));
			} catch (SQLException e) {
				logger.error("SQL Error during create, fill and index table ALL_TERMS with the iterate-documents strategy.\n" +
						"Query " + i.toString() + " failed:\n" +
						queries.get(i)
						);
				throw new RuntimeException(e);
			}
		}
	}
	
	
	public void runSqlFillQueriesIterateDocuments(
			Database database,
			Integer documentIdStepSize, 
			Integer numberOfStepsUntilAggregation) {
		getMinMaxDocumentIdRange(database);
		generateFillQueriesIterateDocuments(minDocumentId, maxDocumentId, documentIdStepSize, numberOfStepsUntilAggregation);
		executeFillQueries(database);
	}

	public static void main(String[] args){
		generateFillQueriesIterateDocuments(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		for (int i = 0; i < queries.size(); i++) {
			System.out.println(queries.get(i) + "\n");
		}
		return;
	}

	

}
