package cc.topicexplorer.plugin.time.preprocessing.tables.document;

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
				+ "` ADD COLUMN TIME$TIME_STAMP INT(11)");
	}

	@Override
	public void dropTable() throws SQLException {
		try {
			this.database
					.executeUpdateQuery("ALTER TABLE " + this.tableName
							+ " DROP COLUMN TIME$TIME_STAMP");
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