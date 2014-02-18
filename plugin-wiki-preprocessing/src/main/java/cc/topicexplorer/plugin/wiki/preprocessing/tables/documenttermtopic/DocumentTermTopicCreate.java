package cc.topicexplorer.plugin.wiki.preprocessing.tables.documenttermtopic;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableCreateCommand;

/**
 * @author user
 * 
 */
public class DocumentTermTopicCreate extends TableCreateCommand {

	@Override
	public void createTable() {
		try {
			database.executeUpdateQuery("ALTER IGNORE TABLE `" + this.tableName
					+ "` ADD COLUMN WIKI$POSITION int(11) NOT NULL;");
		} catch (SQLException e) {
			logger.error("Column WIKI$POSITION could not be added to table " + this.tableName);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void dropTable() {
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName + " DROP COLUMN WIKI$POSITION");
		} catch (SQLException e) {
			if (e.getErrorCode() != 1091) { // MySQL Error code for: 'Can't
				//
				// DROP..; check that column/key //
				// exists
				logger.error("WIKI_DocumentTermTopicCreate.dropColumns: Cannot drop column.");
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void setTableName() {
		tableName = "DOCUMENT_TERM_TOPIC";
	}

	@Override
	public void addDependencies() {

		beforeDependencies.add("DocumentTermTopicCreate");
		afterDependencies.add("DocumentTermTopicFill");
		afterDependencies.add("InFilePreparation");

		optionalAfterDependencies.add("Prune");

	}
}