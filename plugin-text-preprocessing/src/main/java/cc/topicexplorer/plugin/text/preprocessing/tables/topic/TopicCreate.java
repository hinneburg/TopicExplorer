package cc.topicexplorer.plugin.text.preprocessing.tables.topic;

import java.sql.SQLException;

import cc.topicexplorer.chain.commands.TableCreateCommand;

/**
 * angefangen von Mattes weiterverarbeitet von Gert Kommaersetzung, Pfadangabe
 * eingefügt, Tabellenname mit Jooq verknüpft
 * 
 */
public class TopicCreate extends TableCreateCommand {
	
	@Override
	public void createTable() throws SQLException {
		this.database
				.executeUpdateQuery("ALTER TABLE "
						+ this.tableName
						+ " ADD COLUMN TOPIC.TEXT$TOPIC_LABEL VARCHAR(255) COLLATE utf8_bin DEFAULT ''");
	}

	private void dropColumns() throws SQLException {

		try {
			this.database
					.executeUpdateQuery("ALTER TABLE "
							+ this.tableName
							+ " DROP COLUMN TOPIC.TEXT$TOPIC_LABEL");
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
		beforeDependencies.add("DocumentTermTopicCreate");
		beforeDependencies.add("TopicCreate");
	}
}
