package cc.topicexplorer.plugin.wordtype.preprocessing.tables.term;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;

import com.google.common.collect.Sets;

public class TermFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(TermFill.class);

	@Override
	public void fillTable() {
		try {
			String query = "UPDATE " + this.tableName + ", DOCUMENT_TERM_TOPIC SET "
					+ this.tableName + ".WORDTYPE$WORDTYPE=DOCUMENT_TERM_TOPIC.WORDTYPE$WORDTYPE "
					+ "WHERE " + this.tableName + ".TERM_NAME=DOCUMENT_TERM_TOPIC.TERM";
			database.executeUpdateQuery(query);
		} catch (SQLException e) {
			logger.error("Column TEXT$WORD_TYPE in table " + this.tableName + " could not be set to TERM_NAME.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		tableName = "TERM";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("TermFill", "WordType_TermCreate", "DocumentTermTopicFill");
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
