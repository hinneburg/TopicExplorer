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
		return Sets.newHashSet("PosTypeFill", "DocumentTermFill", "AllTermsCreate", "DocumentWordtypeFill");
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
			this.database.executeUpdateQuery("UPDATE " + this.tableName + ", ("
					+ "SELECT POS, COUNT(TERM) AS TERM_COUNT, SUM(COUNT) AS TOKEN_COUNT "
					+ "FROM ALL_TERMS GROUP BY POS) x SET " + this.tableName + ".TERM_COUNT=x.TERM_COUNT,"
					+ this.tableName + ".TOKEN_COUNT=x.TOKEN_COUNT WHERE x.POS=" + this.tableName + ".POS ");
			this.database.executeUpdateQuery("UPDATE " + this.tableName + ", ("
					+ "SELECT WORDTYPE_CLASS, COUNT(DISTINCT DOCUMENT_ID) AS DOCUMENT_COUNT,"
					+ "MIN(MIN_TOKEN_LENGTH) AS MIN_TOKEN_LENGTH, "
					+ "MAX(MAX_TOKEN_LENGTH) AS MAX_TOKEN_LENGTH, "
					+ "SUM(SUM_TOKEN_LENGTH)/ "
					+ "SUM(TOKEN_COUNT) AS AVG_TOKEN_LENGTH "
					+ "FROM DOCUMENT_WORDTYPE GROUP BY WORDTYPE_CLASS)x "
					+ "SET " + this.tableName + ".DOCUMENT_COUNT=x.DOCUMENT_COUNT,"
					+ this.tableName + ".MIN_TOKEN_LENGTH=x.MIN_TOKEN_LENGTH,"
					+ this.tableName + ".MAX_TOKEN_LENGTH=x.MAX_TOKEN_LENGTH, "
					+ this.tableName + ".AVG_TOKEN_LENGTH=x.AVG_TOKEN_LENGTH "
					+ "WHERE " + this.tableName + ".POS=x.WORDTYPE_CLASS ");
		} catch(SQLException e) {
			logger.error("Error updating " + this.tableName + " table.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		this.tableName = "POS_TYPE";
	}

}
