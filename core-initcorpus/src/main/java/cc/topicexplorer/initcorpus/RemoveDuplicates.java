package cc.topicexplorer.initcorpus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.commandmanager.core.ResultState;

import com.google.common.collect.Sets;

public class RemoveDuplicates implements Command {

	private static final Logger logger = Logger.getLogger(RemoveDuplicates.class);
	private String tableName;

	@Override
	public ResultState execute(Context context) {
		Properties properties = (Properties) context.get("properties");
		Connection crawlManagerConnection = (Connection) context.get("CrawlManagmentConnection");

		String dbName = properties.getProperty("database.DB");

		this.tableName = dbName + ".DUPLICATES";

		String topicExplorer_DefinitionIsPresent = "";

		try {
			Statement checkStmt = crawlManagerConnection.createStatement();
			ResultSet resultSetOfCheck = checkStmt.executeQuery("select count(*) as IS_THERE "
					+ "from  INFORMATION_SCHEMA.Tables "
					+ "where TABLE_NAME='TOPIC_EXPLORER_DEFINITION' and TABLE_SCHEMA=DATABASE();");
			if (resultSetOfCheck.next()) {
				topicExplorer_DefinitionIsPresent = resultSetOfCheck.getString("IS_THERE");
			}
		} catch (SQLException e) {
			logger.error("Can not check for table TOPIC_EXPLORER_DEFINITION in mangagement database.");
			return ResultState.failure(
					"Can not check for table TOPIC_EXPLORER_DEFINITION in mangagement database. Table "
							+ this.tableName + " could not be filled properly.", e);
		}

		if ("0".equals(topicExplorer_DefinitionIsPresent)) {
			// Old Crawl Management Database, this may contain duplicates in the
			// documents
			// that need to be handled.
			try {
				Statement stmt = crawlManagerConnection.createStatement();

				stmt.executeUpdate("create table " + this.tableName + " as "
						+ "select URL_MD5, min(DOCUMENT_DATE) mindate, min(DOCUMENT_ID) minid from " + dbName
						+ ".orgTable_meta " + "group by URL_MD5 having count(*) > 1");

				stmt.executeUpdate("create index url_idx on " + this.tableName + "(URL_MD5,mindate,minid)");

				stmt.executeUpdate("delete " + dbName + ".orgTable_meta, " + dbName + ".orgTable_text from " + dbName
						+ ".orgTable_meta, " + dbName + ".orgTable_text, " + this.tableName
						+ " where orgTable_meta.DOCUMENT_ID=orgTable_text.DOCUMENT_ID and orgTable_meta.URL_MD5="
						+ this.tableName + ".URL_MD5 and " + "(orgTable_meta.DOCUMENT_DATE>" + this.tableName
						+ ".mindate or (orgTable_meta.DOCUMENT_DATE=" + this.tableName + ".mindate "
						+ "and orgTable_meta.DOCUMENT_ID>" + this.tableName + ".minid))");

				stmt.executeUpdate("drop table " + this.tableName);

			} catch (SQLException e) {
				logger.error("Table " + this.tableName + " could not be filled properly.");
				return ResultState.failure("Table " + this.tableName + " could not be filled properly.", e);
			}
		}
		return ResultState.success();

	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("CopyOrgTable");
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
