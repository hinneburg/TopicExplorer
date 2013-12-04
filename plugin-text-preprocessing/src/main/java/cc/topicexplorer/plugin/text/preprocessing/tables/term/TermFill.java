package cc.topicexplorer.plugin.text.preprocessing.tables.term;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableFillCommand;

public class TermFill extends TableFillCommand {

	@Override
	public void fillTable() throws SQLException {
		String query = "UPDATE " + this.tableName + " SET TEXT$WORD_TYPE=TERM_NAME";
		database.executeUpdateQuery(query);

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
