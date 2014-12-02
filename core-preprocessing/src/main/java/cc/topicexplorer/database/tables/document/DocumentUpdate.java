package cc.topicexplorer.database.tables.document;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jooq.tools.StringUtils;

import com.google.common.collect.Sets;

import cc.topicexplorer.commands.TableFillCommand;

public class DocumentUpdate extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(DocumentUpdate.class);
	private ArrayList<Integer> documentList = new ArrayList<Integer>();
	
	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentCreate", "DocumentTopicFill", "DocumentTermTopicFill");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet("HierarchicalTopic_DocumentTopicFill");
	}

	@Override
	public void fillTable() {
		try {
			updateBestTopics();
			updateFulltext();
			updateTitle();
		}catch (SQLException e) {
			logger.error("Error while updating " + this.tableName);
			throw new RuntimeException(e);
		}
	}
	
	private void updateBestTopics() throws SQLException {
		int docId = -1;	
		ArrayList<Integer> topicList = new ArrayList<Integer>();
		
		Statement updateStmt = database.getConnection().createStatement();
		ResultSet fulltextRS = database.executeQuery("SELECT DOCUMENT_ID,TOPIC_ID FROM DOCUMENT_TOPIC ORDER BY DOCUMENT_ID, PR_TOPIC_GIVEN_DOCUMENT DESC");
		while(fulltextRS.next()) {
			if(docId != fulltextRS.getInt("DOCUMENT_ID")) {
				if(!topicList.isEmpty()) {
					updateStmt.executeUpdate("UPDATE " + this.tableName + " SET BEST_TOPICS='"
							+ StringUtils.join(topicList.toArray(), ",") + "' WHERE DOCUMENT_ID=" + docId);
				}
				topicList = new ArrayList<Integer>();
				docId = fulltextRS.getInt("DOCUMENT_ID");
				documentList.add(docId);
			} else {
				topicList.add(fulltextRS.getInt("TOPIC_ID"));
			}
		}
		updateStmt.executeUpdate("UPDATE " + this.tableName + " SET BEST_TOPICS='"
				+ StringUtils.join(topicList.toArray(), ",") + "' WHERE DOCUMENT_ID=" + docId);
	}
	
	private void updateTitle() throws SQLException {
		String termList = new String();
		
		for(int documentId : documentList) {
			ResultSet bestTermsRS = database.executeQuery("SELECT TERM, COUNT(TERM) FROM DOCUMENT_TERM_TOPIC WHERE DOCUMENT_ID="
						+ documentId + " GROUP BY TERM ORDER BY COUNT(TERM) DESC LIMIT 3");
			if(bestTermsRS.next()) {
				termList = bestTermsRS.getString("TERM");
				while(bestTermsRS.next()) {
					termList += " " + bestTermsRS.getString("TERM");
				}
			}
			database.executeUpdateQuery("UPDATE " + this.tableName + " SET TITLE='"
				+ termList + "' WHERE DOCUMENT_ID=" + documentId);
		}
		
	}
	
	private void updateFulltext() throws SQLException {
		int docId = -1;	
		String termList = new String();
		
		Statement updateStmt = database.getConnection().createStatement();
		ResultSet bestTopicsRS = database.executeQuery("SELECT DOCUMENT_ID,TERM FROM DOCUMENT_TERM_TOPIC ORDER BY DOCUMENT_ID, POSITION_OF_TOKEN_IN_DOCUMENT");
		while(bestTopicsRS.next()) {
			if(docId != bestTopicsRS.getInt("DOCUMENT_ID")) {
				if(!termList.isEmpty()) {
					updateStmt.executeUpdate("UPDATE " + this.tableName + " SET TEXT='"
							+ termList + "' WHERE DOCUMENT_ID=" + docId);
				}
				termList = bestTopicsRS.getString("TERM");
				docId = bestTopicsRS.getInt("DOCUMENT_ID");
			} else {
				termList += " " + bestTopicsRS.getString("TERM");
			}
		}
		updateStmt.executeUpdate("UPDATE " + this.tableName + " SET TEXT='"
				+ termList + "' WHERE DOCUMENT_ID=" + docId);
	}

	@Override
	public void setTableName() {
		tableName = "DOCUMENT";		
	}

}
