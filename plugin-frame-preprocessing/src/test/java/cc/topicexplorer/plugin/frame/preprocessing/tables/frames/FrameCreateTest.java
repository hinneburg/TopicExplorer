package cc.topicexplorer.plugin.frame.preprocessing.tables.frames;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cc.topicexplorer.database.Database;

public class FrameCreateTest {

	@Mock(name = "database")
	private Database dbMock;

	@InjectMocks
	FrameCreate frameCreate = new FrameCreate();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCreateTable() throws SQLException {
		this.frameCreate.setTableName();
		this.frameCreate.createTable();
		verify(this.dbMock)
				.executeUpdateQuery(
						eq("CREATE TABLE FRAME$FRAMES (DOCUMENT_ID INT, TOPIC_ID INT, FRAME VARCHAR(255), START_POSITION INT, END_POSITION INT, ACTIVE BOOLEAN NOT NULL DEFAULT 1, FRAME_TYPE VARCHAR(255)) DEFAULT CHARSET=utf8 COLLATE=utf8_bin"));
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateTableThrowsException() throws SQLException {
		this.frameCreate.createTable();
	}

}
