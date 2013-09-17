package cc.topicexplorer.plugin.text.preprocessing.tables.document;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableCreateCommand;

/**
 * @author user
 * 
 */
public class DocumentCreate extends TableCreateCommand {

	@Override
	public void createTable() throws SQLException {
		this.database.executeUpdateQuery("ALTER IGNORE TABLE " + this.tableName
				+ " ADD COLUMN TEXT$FULLTEXT TEXT COLLATE UTF8_BIN,"
				+ " ADD COLUMN TEXT$TITLE VARCHAR(255) COLLATE UTF8_BIN");
	}

	@Override
	public void dropTable() throws SQLException {
		try {
			this.database
					.executeUpdateQuery("ALTER TABLE " + this.tableName
							+ " DROP COLUMN TEXT$FULLTEXT,"
							+ " DROP COLUMN TEXT$TITLE");
		} catch (Exception e) {
			System.err
					.println("Document.dropColumns: Cannot drop column, perhaps it doesn't exists. Doesn't matter ;)");

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