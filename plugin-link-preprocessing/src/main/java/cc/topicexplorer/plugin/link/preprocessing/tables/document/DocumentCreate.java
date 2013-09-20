package cc.topicexplorer.plugin.link.preprocessing.tables.document;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableCreateCommand;

/**
 * @author user
 * 
 */
public class DocumentCreate extends TableCreateCommand {

	@Override
	public void createTable() throws SQLException {
		database.executeUpdateQuery("ALTER IGNORE TABLE `"
					+ this.tableName
					+ "` ADD COLUMN LINK$URL VARCHAR(500) COLLATE UTF8_BIN,"
					+ " ADD COLUMN LINK$IN_DEGREE INT(11) DEFAULT 1;");
	}

	@Override
	public void dropTable() throws SQLException {
		try {
			this.database
					.executeUpdateQuery("ALTER TABLE "
							+ this.tableName
							+ " DROP COLUMN LINK$URL"
							+ ", DROP COLUMN LINK$IN_DEGREE");
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