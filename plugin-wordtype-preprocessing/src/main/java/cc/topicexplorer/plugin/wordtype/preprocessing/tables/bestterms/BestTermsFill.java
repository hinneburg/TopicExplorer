package cc.topicexplorer.plugin.wordtype.preprocessing.tables.bestterms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;

import com.google.common.collect.Sets;

public class BestTermsFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(BestTermsFill.class);

	@Override
	public void fillTable() {
		int bestTermCount = Integer.parseInt((String) properties.get("TopicBestItemLimit"));
		try {	
			String[] wordtypes =  ((String) properties.get("Wordtype_wordtypes")).split(",");
			ResultSet topipcIdsRs = database.executeQuery("SELECT TOPIC_ID FROM TOPIC");
			List<Integer> topicIds = new ArrayList<Integer>();
			while(topipcIdsRs.next()){
				topicIds.add(topipcIdsRs.getInt("TOPIC_ID"));
			}
			for (int topicId : topicIds) {
				for(String wordtype : wordtypes) {
					if(properties.getProperty("plugins").contains("mecab")) {
						database.executeUpdateQueryForUpdate("INSERT INTO " + this.tableName 
								+ "(TERM_ID, TERM_NAME, TOPIC_ID, NUMBER_OF_DOCUMENT_TOPIC, "
								+ "WORDTYPE) SELECT TERM.TERM_ID, TERM.TERM_NAME, " 
								+ topicId + ", TERM_TOPIC.NUMBER_OF_DOCUMENT_TOPIC, " 
								+ wordtype + " FROM TERM, TERM_TOPIC "
								+ "WHERE TERM.TERM_ID=TERM_TOPIC.TERM_ID AND TOPIC_ID=" + topicId 
								+ " AND TERM.WORDTYPE$WORDTYPE IN (SELECT p1.POS FROM POS_TYPE p1, POS_TYPE p2 "
								+ "WHERE p2.POS='" + wordtype + "' AND p2.LOW<=p1.LOW AND p2.HIGH>=p1.HIGH) "
								+ "ORDER BY NUMBER_OF_DOCUMENT_TOPIC DESC LIMIT " + bestTermCount);
					} else {
						database.executeUpdateQueryForUpdate("INSERT INTO " + this.tableName 
								+ "(TERM_ID, TERM_NAME, TOPIC_ID, NUMBER_OF_DOCUMENT_TOPIC, "
								+ "WORDTYPE) SELECT TERM.TERM_ID, TERM.TERM_NAME, " 
								+ topicId + ", TERM_TOPIC.NUMBER_OF_DOCUMENT_TOPIC, " 
								+ "TERM.WORDTYPE$WORDTYPE FROM TERM, TERM_TOPIC "
								+ "WHERE TERM.TERM_ID=TERM_TOPIC.TERM_ID AND TOPIC_ID=" + topicId 
								+ " AND TERM.WORDTYPE$WORDTYPE='" + wordtype + "' "
								+ "ORDER BY NUMBER_OF_DOCUMENT_TOPIC DESC LIMIT " + bestTermCount);
					}
				}
			}
		}catch (SQLException e) {
			logger.error("Exception while creating wordType-bestTerms table.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		tableName = "WORDTYPE$BEST_TERMS";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet( "WordType_BestTermsCreate", "WordType_TermFill", "TermTopicFill");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet("HierarchicalTopic_TopicFill", "HierarchicalTopic_TermTopicFill");
	}

}
