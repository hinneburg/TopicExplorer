package cc.topicexplorer.plugin.mecab.initcorpus.command;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

import cc.topicexplorer.commands.TableFillCommand;

public class PosTypeUpdate  extends TableFillCommand{
	private static final Logger logger = Logger.getLogger(PosTypeUpdate.class);
	
	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("PosTypeFill", "DocumentTermFill");
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
	public void fillTable() {
		try {
//			this.database.executeUpdateQuery("SET innodb_table_locks = 0;"); 
//			
//			this.database.executeUpdateQuery("CREATE TABLE TEMP_POSTYPE AS "
//					+ "SELECT dt.WORDTYPE_CLASS, COUNT(*) AS TOKEN_COUNT, "
//					+ "COUNT(DISTINCT dt.DOCUMENT_ID) as DOCUMENT_COUNT, "
//					+ "COUNT(DISTINCT dt.TERM) as TERM_COUNT, "
//					+ "MIN(char_length(dt.TOKEN)) as MIN_TOKEN_LENGTH, "
//					+ "AVG(char_length(dt.TOKEN)) as AVG_TOKEN_LENGTH, "
//					+ "MAX(char_length(dt.TOKEN)) as MAX_TOKEN_LENGTH  "
//					+ "FROM DOCUMENT_TERM dt GROUP BY dt.WORDTYPE_CLASS");
//			
//			this.database.executeUpdateQuery("SET innodb_table_locks = 1;"); 
//			
//			this.database.executeUpdateQuery("UPDATE " + this.tableName + ",("
//					+ "SELECT p.POS, COUNT(*) AS TOKEN_COUNT, COUNT(DISTINCT dt.DOCUMENT_ID) as DOCUMENT_COUNT,"
//					+ "COUNT(DISTINCT dt.TERM) as TERM_COUNT, MIN(char_length(dt.TOKEN)) as MIN_TOKEN_LENGTH,"
//					+ "AVG(char_length(dt.TOKEN)) as AVG_TOKEN_LENGTH, MAX(char_length(dt.TOKEN)) as MAX_TOKEN_LENGTH "
//					+ "FROM TEMP_POSTYPE dt, POS_TYPE subtype, POS_TYPE p "
//					+ "WHERE subtype.POS = dt.WORDTYPE_CLASS AND p.LOW <= subtype.LOW and subtype.HIGH <= p.HIGH "
//					+ "GROUP BY p.POS, p.DESCRIPTION, p.PARENT_POS,p.LOW, p.HIGH "
//					+ "ORDER BY p.LOW, p.HIGH desc)x SET "
//					+ this.tableName + ".TOKEN_COUNT=x.TOKEN_COUNT,"
//					+ this.tableName + ".DOCUMENT_COUNT=x.DOCUMENT_COUNT,"
//					+ this.tableName + ".TERM_COUNT=x.TERM_COUNT,"
//					+ this.tableName + ".MIN_TOKEN_LENGTH=x.MIN_TOKEN_LENGTH,"
//					+ this.tableName + ".AVG_TOKEN_LENGTH=x.AVG_TOKEN_LENGTH,"
//					+ this.tableName + ".MAX_TOKEN_LENGTH=x.MAX_TOKEN_LENGTH "
//					+ "WHERE " + this.tableName + ".POS=x.POS");
			
			this.database.executeUpdateQuery("UPDATE " + this.tableName + ", ("
					+ "SELECT p.POS, COUNT(*) AS TOKEN_COUNT, COUNT(DISTINCT dt.DOCUMENT_ID) as DOCUMENT_COUNT,"
					+ "COUNT(DISTINCT dt.TERM) as TERM_COUNT, MIN(char_length(dt.TOKEN)) as MIN_TOKEN_LENGTH,"
					+ "AVG(char_length(dt.TOKEN)) as AVG_TOKEN_LENGTH, MAX(char_length(dt.TOKEN)) as MAX_TOKEN_LENGTH "
					+ "FROM DOCUMENT_TERM dt, POS_TYPE subtype, POS_TYPE p "
					+ "WHERE subtype.POS = dt.WORDTYPE_CLASS AND p.LOW <= subtype.LOW and subtype.HIGH <= p.HIGH "
					+ "GROUP BY p.POS, p.DESCRIPTION, p.PARENT_POS,p.LOW, p.HIGH "
					+ "ORDER BY p.LOW, p.HIGH desc)x SET "
					+ this.tableName + ".TOKEN_COUNT=x.TOKEN_COUNT,"
					+ this.tableName + ".DOCUMENT_COUNT=x.DOCUMENT_COUNT,"
					+ this.tableName + ".TERM_COUNT=x.TERM_COUNT,"
					+ this.tableName + ".MIN_TOKEN_LENGTH=x.MIN_TOKEN_LENGTH,"
					+ this.tableName + ".AVG_TOKEN_LENGTH=x.AVG_TOKEN_LENGTH,"
					+ this.tableName + ".MAX_TOKEN_LENGTH=x.MAX_TOKEN_LENGTH "
					+ "WHERE " + this.tableName + ".POS=x.POS");
		} catch(SQLException e) {
			logger.error("Error updating POS_TYPE table.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		this.tableName = "POS_TYPE";
	}

}
