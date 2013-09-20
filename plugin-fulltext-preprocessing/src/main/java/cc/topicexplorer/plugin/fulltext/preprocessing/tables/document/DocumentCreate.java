package cc.topicexplorer.plugin.fulltext.preprocessing.tables.document;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableCreateCommand;

/**
 * @author user
 * 
 */
public class DocumentCreate extends TableCreateCommand {

	@Override
	public void createTable() throws SQLException {
		database.executeUpdateQuery("ALTER IGNORE TABLE `" + this.tableName
				+ "` ADD COLUMN FULLTEXT$FULLTEXT TEXT COLLATE UTF8_BIN;");
	}

	@Override
	public void dropTable() throws SQLException {
		// database.dropTable(this.tableName);
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName
					+ " DROP COLUMN ADD COLUMN FULLTEXT$FULLTEXT");
		} catch (Exception e) {
			logger.warn("Document.dropColumns: Cannot drop column, perhaps it doesn't exists. Doesn't matter ;)");

		}
	}

	@Override
	public void setTableName() {
		tableName = "DOCUMENT";
	}
	
	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentCreate");
	}	
}