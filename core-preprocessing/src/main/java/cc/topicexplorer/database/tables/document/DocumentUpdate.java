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

	private static final Logger logger = Logger.getLogger(DocumentFill.class);
	
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
		int docId = -1;
		
		ArrayList<Integer> topicList = new ArrayList<Integer>();
		try {
			Statement updateStmt = database.getConnection().createStatement();
			ResultSet bestTopicsRS = database.executeQuery("SELECT DOCUMENT_ID,TOPIC_ID FROM DOCUMENT_TOPIC ORDER BY DOCUMENT_ID, PR_TOPIC_GIVEN_DOCUMENT DESC");
			while(bestTopicsRS.next()) {
				if(docId != bestTopicsRS.getInt("DOCUMENT_ID")) {
					if(!topicList.isEmpty()) {
						updateStmt.executeUpdate("UPDATE " + this.tableName + " SET BEST_TOPICS='"
								+ StringUtils.join(topicList, ",") + "' WHERE DOCUMENT_ID=" + docId);
					}
					topicList = new ArrayList<Integer>();
					docId = bestTopicsRS.getInt("DOCUMENT_ID");
				} else {
					topicList.add(bestTopicsRS.getInt("TOPIC_ID"));
				}
			}
			updateStmt.executeUpdate("UPDATE " + this.tableName + " SET BEST_TOPICS='"
					+ StringUtils.join(topicList, ",") + "' WHERE DOCUMENT_ID=" + docId);
		} catch (SQLException e) {
			logger.error("Error while updating " + this.tableName);
			throw new RuntimeException(e);
		}

			
		
	}

	@Override
	public void setTableName() {
		tableName = "DOCUMENT";
		
	}

}
