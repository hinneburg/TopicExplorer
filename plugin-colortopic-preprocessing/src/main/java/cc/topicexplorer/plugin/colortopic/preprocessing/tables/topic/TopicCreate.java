package cc.topicexplorer.plugin.colortopic.preprocessing.tables.topic;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableCreateCommand;

/**
 * angefangen von Mattes weiterverarbeitet von Gert Kommaersetzung, Pfadangabe
 * eingefügt, Tabellenname mit Jooq verknüpft
 * 
 */
public class TopicCreate extends TableCreateCommand {

	public void createTable() throws SQLException {
		this.database
				.executeUpdateQuery("ALTER TABLE "
						+ this.tableName
						+ " ADD COLUMN TOPIC.COLOR_TOPIC$COLOR VARCHAR(10) COLLATE utf8_bin");
	}

	private void dropColumns() throws SQLException {

		try {
			this.database
					.executeUpdateQuery("ALTER TABLE "
							+ this.tableName
							+ " DROP COLUMN TOPIC.COLOR_TOPIC$COLOR");
		} catch (Exception e) {
			System.err
					.println("TopicMetaData.dropColumns: Cannot drop column, perhaps it doesn't exists. Doesn't matter ;)");

		}
	}

	@Override
	public void dropTable() throws SQLException {
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
