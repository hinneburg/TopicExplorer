package cc.topicexplorer.plugin.hierarchicaltopic.preprocessing.tables.topic;

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
						+ " ADD COLUMN HIERARCHICAL_TOPIC$START INT(11) , "
						+ " ADD COLUMN HIERARCHICAL_TOPIC$END INT(11) , "
						+ " ADD COLUMN HIERARCHICAL_TOPIC$DEPTH INT(11) , "
						+ " ADD COLUMN HIERARCHICAL_TOPIC$CLUSTER_MEMBERSHIP TEXT COLLATE utf8_bin");
	}

	private void dropColumns() throws SQLException {

		try {
			this.database
					.executeUpdateQuery("ALTER TABLE "
							+ this.tableName
							+ " DROP COLUMN HIERARCHICAL_TOPIC$START"
							+ ", DROP COLUMN HIERARCHICAL_TOPIC$END"
							+ ", DROP COLUMN HIERARCHICAL_TOPIC$DEPTH"
							+ ", DROP COLUMN HIERARCHICAL_TOPIC$CLUSTER_MEMBERSHIP");
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
