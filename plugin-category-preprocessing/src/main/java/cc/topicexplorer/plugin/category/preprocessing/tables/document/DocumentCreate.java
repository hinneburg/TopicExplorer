package cc.topicexplorer.plugin.category.preprocessing.tables.document;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableCreateCommand;

public class DocumentCreate extends TableCreateCommand {

	@Override
	public void createTable() throws SQLException {

		database.executeUpdateQuery("ALTER IGNORE TABLE " + this.tableName 
				+ " ADD COLUMN CATEGORY$CATEGORY_ID INTEGER(11) NOT NULL ");
	}

	@Override
	public void dropTable() throws SQLException {
		try {
			database.executeUpdateQuery("ALTER IGNORE TABLE " + this.tableName 
						+ " DROP COLUMN CATEGORY$CATEGORY_ID");
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
