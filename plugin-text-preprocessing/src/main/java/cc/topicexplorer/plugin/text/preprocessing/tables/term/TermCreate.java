package cc.topicexplorer.plugin.text.preprocessing.tables.term;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableCreateCommand;

/**
 * @author user
 * 
 */
public class TermCreate extends TableCreateCommand {

	@Override
	public void createTable() throws SQLException {
		this.database.executeUpdateQuery("ALTER IGNORE TABLE " + this.tableName
				+ " ADD COLUMN TEXT$WORD_TYPE VARCHAR(255) COLLATE UTF8_BIN");
	}

	@Override
	public void dropTable() throws SQLException {
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName + " DROP COLUMN TEXT$WORD_TYPE");
		} catch (Exception e) {
			logger.warn("Document.dropColumns: Cannot drop column, perhaps it doesn't exists. Doesn't matter ;)");
		}
	}

	@Override
	public void setTableName() {
		tableName = "TERM";
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("TermCreate");
	}
}
