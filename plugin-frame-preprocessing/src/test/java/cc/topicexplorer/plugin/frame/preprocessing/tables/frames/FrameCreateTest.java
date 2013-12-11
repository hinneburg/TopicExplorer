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
		verify(this.dbMock).executeUpdateQuery(
				eq("CREATE TABLE Frames "
						+ "(DOCUMENT_ID INT, TOPIC_ID INT, FRAME VARCHAR(255), START_POSITION INT, END_POSITION INT)"));
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateTableThrowsException() throws SQLException {
		this.frameCreate.createTable();
	}

	@Test
	public void testDropTable() throws SQLException {
		this.frameCreate.setTableName();
		this.frameCreate.dropTable();
		verify(this.dbMock).executeUpdateQuery("DROP TABLE Frames");
	}

	@Test(expected = IllegalStateException.class)
	public void testDropTableThrowsException() throws SQLException {
		this.frameCreate.dropTable();
	}
}
