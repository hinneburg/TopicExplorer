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

import com.google.common.collect.Sets;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;

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
	public void execute(Context context) {
		Properties properties = (Properties) context.get("properties");
		Connection crawlManagerConnection = (Connection) context.get("CrawlManagmentConnection");
		
		String dbName = properties.getProperty("database.DB");
		String searchStringId = properties.getProperty("database.SearchStringId");
		
		try {
			Statement copyStmt = crawlManagerConnection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
			copyStmt.setFetchSize(Integer.MIN_VALUE);
			
			Statement tmpStmt;
			
			ResultSet tableRs = copyStmt.executeQuery("SELECT TABLE_NAME FROM CRAWL WHERE SEARCH_STRING_ID=" + searchStringId);
			int maxId = 0;
			List<String> tableNames = new ArrayList<String>();
			while(tableRs.next()) {
				tableNames.add(tableRs.getString("TABLE_NAME"));
			}
			copyStmt.close();
			for(String tableName : tableNames) {
				tmpStmt = crawlManagerConnection.createStatement();
				if(maxId == 0) {
					tmpStmt.executeUpdate("CREATE TABLE " + dbName + ".orgTable_meta AS (SELECT * FROM " + tableName + ")");
					tmpStmt.executeUpdate("CREATE TABLE " + dbName + ".orgTable_text AS (SELECT * FROM " + tableName + "_TEXT)");
				} else {
					//create temp tables
					tmpStmt.executeUpdate("CREATE TABLE " + dbName + ".tempTable_meta AS (SELECT * FROM " + tableName + ")");
					tmpStmt.executeUpdate("CREATE TABLE " + dbName + ".tempTable_text AS (SELECT * FROM " + tableName + "_TEXT)");
					//update ids
					tmpStmt.executeUpdate("UPDATE " + dbName + ".tempTable_meta SET DOCUMENT_ID=DOCUMENT_ID+" + maxId);
					tmpStmt.executeUpdate("UPDATE " + dbName + ".tempTable_text SET DOCUMENT_ID=DOCUMENT_ID+" + maxId);
					//copy
					tmpStmt.executeUpdate("INSERT INTO " + dbName + ".orgTable_meta SELECT * FROM " + dbName + ".tempTable_meta");
					tmpStmt.executeUpdate("INSERT INTO " + dbName + ".orgTable_text SELECT * FROM " + dbName + ".tempTable_text");
					//delete
					tmpStmt.executeUpdate("DROP TABLE " + dbName + ".tempTable_meta");
					tmpStmt.executeUpdate("DROP TABLE " + dbName + ".tempTable_text");
				}
				ResultSet maxIdRs = tmpStmt.executeQuery("SELECT MAX(DOCUMENT_ID) FROM " + dbName + ".orgTable_meta");
				if(maxIdRs.next()) {
					maxId = maxIdRs.getInt(1);
				}
				tmpStmt.close();
			}
			
		} catch (SQLException e) {
			logger.error("OrgTables could not be created.");
			throw new RuntimeException(e);
		}
		
	}

}
