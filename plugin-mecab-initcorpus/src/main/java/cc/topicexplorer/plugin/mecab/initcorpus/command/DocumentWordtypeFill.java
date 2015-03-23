package cc.topicexplorer.plugin.mecab.initcorpus.command;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;

import com.google.common.collect.Sets;


public class DocumentWordtypeFill extends TableFillCommand {
	private static final Logger logger = Logger.getLogger(DocumentWordtypeFill.class);

	@Override
	public void fillTable() { 
		try {
			ArrayList<Integer> docIds = new ArrayList<Integer>();
			ResultSet docIdRS = database.executeQuery("SELECT DOCUMENT_ID FROM orgTable_meta");
			while(docIdRS.next()) {
				docIds.add(docIdRS.getInt("DOCUMENT_ID"));
			}
			for(int docId : docIds) {
				database.executeUpdateQuery("INSERT INTO " + this.tableName + " ("
						+ "SELECT " + docId + " as DOCUMENT_ID, p.POS, COUNT(*) AS TOKEN_COUNT,COUNT(DISTINCT dt.TERM) as TERM_COUNT, "
						+ "MIN(char_length(dt.TOKEN)) as MIN_TOKEN_LENGTH, MAX(char_length(dt.TOKEN)) as MAX_TOKEN_LENGTH, "
						+ "SUM(char_length(dt.TOKEN)) as SUM_TOKEN_LENGTH FROM DOCUMENT_TERM dt, POS_TYPE subtype, POS_TYPE p "
						+ "WHERE subtype.POS = dt.WORDTYPE_CLASS AND p.LOW <= subtype.LOW and subtype.HIGH <= p.HIGH and "
						+ "dt.DOCUMENT_ID=" + docId + " GROUP BY p.POS)");
			}
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be filled.");
			throw new RuntimeException(e);
		} 
	}

	@Override
	public void setTableName() {
		this.tableName = "DOCUMENT_WORDTYPE";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("PosTypeFill", "DocumentTermFill", "DocumentWordtypeCreate");
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
