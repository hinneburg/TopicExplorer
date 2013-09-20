package cc.topicexplorer.plugin.time.preprocessing.tables.documenttermtopic;

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
				+ "` ADD COLUMN TIME$WEEK INT(11)");
	}

	@Override
	public void dropTable() throws SQLException {
		try {
			this.database
					.executeUpdateQuery("ALTER TABLE " + this.tableName
							+ " DROP COLUMN TIME$WEEK");
		} catch (Exception e) {
			logger.warn("DocumentTermTopic.dropColumns: Cannot drop column, perhaps it doesn't exists. Doesn't matter ;)");

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