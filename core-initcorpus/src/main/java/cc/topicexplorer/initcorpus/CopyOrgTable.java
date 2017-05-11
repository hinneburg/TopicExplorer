package cc.topicexplorer.initcorpus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.commandmanager.core.ResultState;

import com.google.common.collect.Sets;

public class CopyOrgTable implements Command {
	private static final Logger logger = Logger.getLogger(CopyOrgTable.class);

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("CreateDB", "CreateUser");
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
	public ResultState execute(Context context) {
		Properties properties = (Properties) context.get("properties");
		Connection crawlManagerConnection = (Connection) context.get("CrawlManagmentConnection");

		String dbName = properties.getProperty("database.DB");
		String searchStringId = properties.getProperty("database.SearchStringId");

		// Construct the filter for the new corpus
		String corpusFilter = "";
		String topicExplorer_DefinitionIsPresent = "";
		try {

			Statement checkStmt = crawlManagerConnection.createStatement();
			ResultSet resultSetOfCheck = checkStmt.executeQuery("select count(*) as IS_THERE "
					+ "from  INFORMATION_SCHEMA.Tables "
					+ "where TABLE_NAME='TOPIC_EXPLORER_DEFINITION' and TABLE_SCHEMA=DATABASE();");
			if (resultSetOfCheck.next()) {
				topicExplorer_DefinitionIsPresent = resultSetOfCheck.getString("IS_THERE");
				logger.info("Test if TOPIC_EXPLORER_DEFINITION table is present, gave result: "
						+ topicExplorer_DefinitionIsPresent);

			}
		} catch (SQLException e) {
			logger.error("Can not check if TOPIC_EXPLORER_DEFINITION table is present.");
			throw new RuntimeException(e);
		}

		if ("1".equals(topicExplorer_DefinitionIsPresent)) {
			try {

				Statement filterStmt = crawlManagerConnection.createStatement();
				ResultSet resultSetOfFilter = filterStmt.executeQuery("select " + "  FILTER_START_DATETIME,"
						+ "  FILTER_END_DATETIME," + "  FILTER_TEXT_SQL " + "from " + "  TOPIC_EXPLORER_DEFINITION "
						+ "where" + "  TE_IDENTIFIER = '" + dbName + "';");
				// using this filter in a query assumes that org_table_meta and
				// org_table_text have been joined
				logger.info("Construct corpus filter with statement:" + "select " + "  FILTER_START_DATETIME,"
						+ "  FILTER_END_DATETIME," + "  FILTER_TEXT_SQL " + "from " + "  TOPIC_EXPLORER_DEFINITION "
						+ "where" + "  TE_IDENTIFIER = '" + dbName + "';");
				if (resultSetOfFilter.next()) {
					corpusFilter = " " + " ( DOCUMENT_DATE BETWEEN " + "CAST('"
							+ resultSetOfFilter.getString("FILTER_START_DATETIME") + "' AS DATE) AND " + "CAST('"
							+ resultSetOfFilter.getString("FILTER_END_DATETIME") + "' AS DATE) " + ") AND ( "
							+ resultSetOfFilter.getString("FILTER_TEXT_SQL") + " ) ";

				}
				logger.info("Corpus Filter is: " + corpusFilter);
			} catch (SQLException e) {
				logger.error("Filter Condition could not be created.");
				throw new RuntimeException(e);
			}
		}

		if ("1".equals(topicExplorer_DefinitionIsPresent)) {
			// when the new table Topic_EXPLORER_DEFINITION is there
			// than we can use the filter
			try {
				logger.info("Use corpus filter to : create orgTables");

				Statement copyStmt = crawlManagerConnection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
						java.sql.ResultSet.CONCUR_READ_ONLY);
				copyStmt.setFetchSize(Integer.MIN_VALUE);

				ResultSet tableRs = copyStmt.executeQuery("SELECT TABLE_NAME FROM CRAWL WHERE SEARCH_STRING_ID="
						+ searchStringId);
				// there should be only one table per SEARCH_STRING_ID
				if (tableRs.next()) {
					String tableName = tableRs.getString("TABLE_NAME");
					copyStmt.close();

					Statement tmpStmt = crawlManagerConnection.createStatement();
					//@formatter:off
					tmpStmt.executeUpdate(
							"CREATE TABLE " + dbName + ".orgTable_meta AS ("
									+ "SELECT " + tableName + ".*, MD5(URL) AS URL_MD5 "
									+ "FROM " + tableName + " join " + tableName + "_TEXT using (DOCUMENT_ID) "
									+ "WHERE " + corpusFilter 
									+ " )"
							);
					tmpStmt.executeUpdate(
							"CREATE TABLE " + dbName + ".orgTable_text AS ("
									+ "SELECT " + tableName	+ "_TEXT.* "
									+ "FROM " + tableName + " join " + tableName + "_TEXT using (DOCUMENT_ID) "
									+ "WHERE " + corpusFilter 
									+ " )"
							);
					//@formatter:on
					tmpStmt.executeUpdate("create index url_idx on " + dbName + ".orgTable_meta(URL_MD5,DOCUMENT_DATE)");
					tmpStmt.executeUpdate("create index id_idx on " + dbName + ".orgTable_meta(DOCUMENT_ID)");
					tmpStmt.executeUpdate("create index id_idx on " + dbName + ".orgTable_text(DOCUMENT_ID)");
					tmpStmt.close();
				} else {
					logger.error("No table name found for SEARCH_STRING_ID" + searchStringId);
					throw new RuntimeException();
				}

			} catch (SQLException e) {
				logger.error("OrgTables could not be created with Filter Condition.");
				throw new RuntimeException(e);
			}
		} else {
			// when the new table Topic_EXPLORER_DEFINITION is not there
			// than we fall back to the old code

			try {
				Statement copyStmt = crawlManagerConnection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
						java.sql.ResultSet.CONCUR_READ_ONLY);
				copyStmt.setFetchSize(Integer.MIN_VALUE);

				Statement tmpStmt;

				ResultSet tableRs = copyStmt.executeQuery("SELECT TABLE_NAME FROM CRAWL WHERE SEARCH_STRING_ID="
						+ searchStringId);
				int maxId = 0;
				List<String> tableNames = new ArrayList<String>();
				while (tableRs.next()) {
					tableNames.add(tableRs.getString("TABLE_NAME"));
				}
				copyStmt.close();
				for (String tableName : tableNames) {
					tmpStmt = crawlManagerConnection.createStatement();
					if (maxId == 0) {
						tmpStmt.executeUpdate("CREATE TABLE " + dbName
								+ ".orgTable_meta AS (SELECT *, MD5(URL) AS URL_MD5 FROM " + tableName + ")");
						tmpStmt.executeUpdate("CREATE TABLE " + dbName + ".orgTable_text AS (SELECT * FROM "
								+ tableName + "_TEXT)");

						tmpStmt.executeUpdate("create index url_idx on " + dbName
								+ ".orgTable_meta(URL_MD5,DOCUMENT_DATE)");
						tmpStmt.executeUpdate("create index id_idx on " + dbName + ".orgTable_meta(DOCUMENT_ID)");

						tmpStmt.executeUpdate("create index id_idx on " + dbName + ".orgTable_text(DOCUMENT_ID)");
					} else {
						// create temp tables
						tmpStmt.executeUpdate("CREATE TABLE " + dbName + ".tempTable_meta AS (SELECT * FROM "
								+ tableName + ")");
						tmpStmt.executeUpdate("CREATE TABLE " + dbName + ".tempTable_text AS (SELECT * FROM "
								+ tableName + "_TEXT)");
						// update ids
						tmpStmt.executeUpdate("UPDATE " + dbName + ".tempTable_meta SET DOCUMENT_ID=DOCUMENT_ID+"
								+ maxId);
						tmpStmt.executeUpdate("UPDATE " + dbName + ".tempTable_text SET DOCUMENT_ID=DOCUMENT_ID+"
								+ maxId);
						// copy
						tmpStmt.executeUpdate("INSERT INTO " + dbName
								+ ".orgTable_meta SELECT *, MD5(URL) AS URL_MD5 FROM " + dbName + ".tempTable_meta");
						tmpStmt.executeUpdate("INSERT INTO " + dbName + ".orgTable_text SELECT * FROM " + dbName
								+ ".tempTable_text");
						// delete
						tmpStmt.executeUpdate("DROP TABLE " + dbName + ".tempTable_meta");
						tmpStmt.executeUpdate("DROP TABLE " + dbName + ".tempTable_text");
					}
					ResultSet maxIdRs = tmpStmt.executeQuery("SELECT MAX(DOCUMENT_ID) FROM " + dbName
							+ ".orgTable_meta");
					if (maxIdRs.next()) {
						maxId = maxIdRs.getInt(1);
					}
					tmpStmt.close();
				}

			} catch (SQLException e) {
				logger.error("OrgTables could not be created.");
				throw new RuntimeException(e);
			}
		}
		return ResultState.success();

	}

}
