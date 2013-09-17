package cc.topicexplorer.plugin.time.preprocessing.tables.wordspertopicperweek;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableCreateCommand;

/**
 * @author user
 * 
 */
public class WordsPerTopicPerWeekCreate extends TableCreateCommand {

	@Override
	public void createTable() throws SQLException {
		this.database.executeUpdateQuery(" CREATE TABLE `" + this.tableName + "` ("
				+ " `ID` INT(11) NOT NULL AUTO_INCREMENT, "
				+ " `TOPIC_ID` int(11) NOT NULL, "
				+ " `WEEK` int(11) NOT NULL, "
				+ " `WORD_COUNT` int(11) NOT NULL,"
				+ " KEY `IDID` (`ID`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin; ");
	}

	@Override
	public void dropTable() throws SQLException {
		this.database.dropTable(this.tableName);
	}

	@Override
	public void setTableName() {
		this.tableName = "TIME$WORDS_PER_TOPIC_PER_WEEK";
	}
}