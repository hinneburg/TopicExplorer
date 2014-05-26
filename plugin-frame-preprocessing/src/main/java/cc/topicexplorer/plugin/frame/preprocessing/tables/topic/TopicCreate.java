package cc.topicexplorer.plugin.frame.preprocessing.tables.topic;

import java.sql.SQLException;

import cc.topicexplorer.commands.TableCreateCommand;

/*
 * angefangen von Mattes weiterverarbeitet von Gert Kommaersetzung, Pfadangabe
 * eingefügt, Tabellenname mit Jooq verknüpft
 * 
 */
public class TopicCreate extends TableCreateCommand {

	@Override
	public void createTable() {
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName
					+ " ADD COLUMN FRAME$UNIQUE_FRAME_COUNT INT,"
					+ " ADD COLUMN FRAME$FRAME_COUNT INT");
		} catch (SQLException e) {
			logger.error("Columns FRAME$UNIQUE_FRAME_COUNT and FRAME$FRAME_COUNTcould not be added to table " + this.tableName);
			throw new RuntimeException(e);
		}
	}

	private void dropColumns() {
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName + " DROP COLUMN FRAME$UNIQUE_FRAME_COUNT,"
					+ " DROP COLUMN FRAME$FRAME_COUNT");
		} catch (SQLException e) {
			if (e.getErrorCode() != 1091) { // MySQL Error code for 'Can't DROP
				// ..; check that column/key exists
				logger.error("TopicMetaData.dropColumns: Cannot drop columns.");
				throw new RuntimeException(e);
			} else {
				logger.info("dropColumns: ignored SQL-Exception with error code 1091.");
			}
		}
	}

	@Override
	public void dropTable() {
		this.dropColumns();
	}

	@Override
	public void setTableName() {
		this.tableName = "TOPIC";
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("TopicCreate");
	}

}
