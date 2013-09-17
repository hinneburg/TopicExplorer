package cc.topicexplorer.plugin.wiki.preprocessing.tables.documenttermtopic;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableCreateCommand;

/**
 * @author user
 * 
 */
public class DocumentTermTopicCreate extends TableCreateCommand {

	@Override
	public void createTable() throws SQLException {
		database.executeUpdateQuery("ALTER IGNORE TABLE `" + this.tableName
				+ "` ADD COLUMN WIKI$POSITION int(11) NOT NULL;");
		
	}

	@Override
	public void dropTable() throws SQLException {
		// database.dropTable(this.tableName);
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName
					+ " DROP COLUMN ADD COLUMN WIKI$POSITION");
		} catch (Exception e) {
			System.err
					.println("Document.dropColumns: Cannot drop column, perhaps it doesn't exists. Doesn't matter ;)");

		}
	}

	@Override
	public void setTableName() {
		tableName = "DOCUMENT_TERM_TOPIC";
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentTermTopicCreate");
	}	
}