package cc.topicexplorer.plugin.wordtype.preprocessing.tables.documenttermtopic;

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
				+ "` ADD COLUMN WORDTYPE$WORDTYPE varchar(255) COLLATE utf8_bin NOT NULL;");
	}

	@Override
	public void dropTable() throws SQLException {
		// database.dropTable(this.tableName);
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName
					+ " DROP COLUMN WORDTYPE$WORDTYPE");
		} catch (Exception e) {
			logger.warn("Document.dropColumns: Cannot drop column, perhaps it doesn't exists. Doesn't matter ;)");

		}
	}

	@Override
	public void setTableName() {
		tableName = "DOCUMENT_TERM_TOPIC";
	}
	
	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentTermTopicCreate");
		afterDependencies.add("InFilePreparation");
	}	
}