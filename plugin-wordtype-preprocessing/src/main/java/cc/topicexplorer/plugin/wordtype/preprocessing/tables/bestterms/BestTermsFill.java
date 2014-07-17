package cc.topicexplorer.plugin.wordtype.preprocessing.tables.bestterms;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;

import com.google.common.collect.Sets;

public class BestTermsFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(BestTermsFill.class);

	@Override
	public void fillTable() {
		int numTopics = Integer.parseInt((String) properties.get("malletNumTopics"));
		String wordTypes[] = ((String) properties.get("Wordtype_wordtypes")).split(",");
		try {	
			for (int i = 0; i < numTopics; i++) {
				for(int j = 0; j < wordTypes.length; j++) {
					database.executeUpdateQueryForUpdate("INSERT INTO " + this.tableName 
							+ "(TERM_ID, TERM_NAME, TOPIC_ID, NUMBER_OF_DOCUMENT_TOPIC, "
							+ "WORDTYPE) SELECT TERM.TERM_ID, TERM.TERM_NAME, " 
							+ i + ", TERM_TOPIC.NUMBER_OF_DOCUMENT_TOPIC, " 
							+ "TERM.WORDTYPE$WORDTYPE FROM TERM, TERM_TOPIC "
							+ "WHERE TERM.TERM_ID=TERM_TOPIC.TERM_ID AND TOPIC_ID=" + i 
							+ " AND TERM.WORDTYPE$WORDTYPE='" + wordTypes[j] +"' "
							+ "ORDER BY NUMBER_OF_DOCUMENT_TOPIC DESC LIMIT 20");
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
		return Sets.newHashSet();
	}

}
