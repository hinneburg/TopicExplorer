package cc.topicexplorer.plugin.time.preprocessing.tables.wordspertopicperweek;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.collect.Sets;

public class WordsPerTopicPerWeekCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(WordsPerTopicPerWeekCreate.class);

	@Override
	public void createTable() {
		try {
			this.database.executeUpdateQuery(" CREATE TABLE `" + this.tableName + "` ("
					+ " `ID` INT(11) NOT NULL AUTO_INCREMENT,  `TOPIC_ID` int(11) NOT NULL, "
					+ " `WEEK` int(11) NOT NULL,  `WORD_COUNT` int(11) NOT NULL,"
					+ " KEY `IDID` (`ID`)) ENGINE=InnoDB; ");
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		this.tableName = "TIME$WORDS_PER_TOPIC_PER_WEEK";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet();
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
