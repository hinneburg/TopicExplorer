package cc.topicexplorer.plugin.text.preprocessing.tables.term;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableFillCommand;

public class TermFill extends TableFillCommand {

	@Override
	public void fillTable() {
		String query = "UPDATE " + this.tableName + " SET TEXT$WORD_TYPE=TERM_NAME";

		try {
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
	public void addDependencies() {
		beforeDependencies.add("TermFill");
	}

}
