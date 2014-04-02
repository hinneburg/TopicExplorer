package cc.topicexplorer.plugin.wordtype.preprocessing.tables.documenttermtopic;

import java.sql.SQLException;

import cc.topicexplorer.commands.TableCreateCommand;

public class DocumentTermTopicCreate extends TableCreateCommand {

	@Override
	public void createTable() {
		try {
			database.executeUpdateQuery("ALTER IGNORE TABLE `" + this.tableName
					+ "` ADD COLUMN WORDTYPE$WORDTYPE varchar(255) COLLATE utf8_bin NOT NULL;");
		} catch (SQLException e) {
			logger.error("Column WORDTYPE$WORDTYPE could not be added to table " + this.tableName);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void dropTable() {
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName + " DROP COLUMN WORDTYPE$WORDTYPE");
		} catch (SQLException e) {
			if (e.getErrorCode() != 1091) { // MySQL Error code for: 'Can't //
											// DROP..; check that column/key //
											// exists
				logger.error("Document.dropColumns: Cannot drop column.");
				throw new RuntimeException(e);
			} else {
				logger.info("dropColumns: ignored SQL-Exception with error code 1091.");
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
		afterDependencies.add("InFilePreparation");
	}
}