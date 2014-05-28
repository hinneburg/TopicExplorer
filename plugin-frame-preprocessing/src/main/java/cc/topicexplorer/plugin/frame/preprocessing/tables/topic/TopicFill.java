package cc.topicexplorer.plugin.frame.preprocessing.tables.topic;

/** MIT-JOOQ-START 
 import static jooq.generated.Tables.TOPIC;
 MIT-JOOQ-ENDE */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

import cc.topicexplorer.commands.TableFillCommand;

/**
 * @autor angefangen von Mattes weiterverarbeitet von Gert Kommaersetzung,
 *        Pfadangabe eingefügt, Tabellenname mit Jooq verknüpft
 * 
 */
public class TopicFill extends TableFillCommand {

	@Override
	public void fillTable() {
		try {
			this.prepareMetaDataAndFillTable();
		} catch (SQLException sqlEx) {
			logger.error("Table " + this.tableName + " could not be filled properly.");
			throw new RuntimeException(sqlEx);
		}
	}

	private void prepareMetaDataAndFillTable() throws SQLException {
		database.executeUpdateQuery("UPDATE " + this.tableName 
				+ ", (SELECT COUNT(*) AS FRAME_COUNT, COUNT(DISTINCT FRAME) AS UNIQUE_FRAME_COUNT, TOPIC_ID FROM `FRAMES` WHERE ACTIVE=1 GROUP BY TOPIC_ID) FRAME"
				+ " SET " + this.tableName + ".FRAME$FRAME_COUNT=FRAME.FRAME_COUNT, " + this.tableName + ".FRAME$UNIQUE_FRAME_COUNT=FRAME.UNIQUE_FRAME_COUNT"
				+ " WHERE FRAME.TOPIC_ID = TOPIC.TOPIC_ID");
	}

	@Override
	public void setTableName() {
		/**
		 * MIT-JOOQ-START this.tableName = TOPIC.getName(); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		this.tableName = "TOPIC";
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("TopicFill");
		beforeDependencies.add("TopicMetaData");
		beforeDependencies.add("FrameCreate");
		beforeDependencies.add("Frame_TopicCreate");
	}
}
