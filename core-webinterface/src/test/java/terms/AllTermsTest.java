package terms;

import static org.fest.assertions.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.junit.BeforeClass;
import org.junit.Test;

import cc.topicexplorer.database.Database;

public final class AllTermsTest {
	static Database database;

	@BeforeClass
	public static void initDb() throws Exception {
		Properties properties = new Properties();
		properties.setProperty("database.DbLocation", "localhost:3306");
		properties.setProperty("database.DbUser", "root");
		properties.setProperty("database.DbPassword", "TopicExplorer");
		properties.setProperty("database.DB", "maerchen");
		properties.setProperty("malletNumTopics", "1");
		database = new Database(properties);
	}

	@Test
	public void testExecuteQueriesAndWriteAllTerms() throws SQLException {
		List<String> columnNames = new ArrayList<String>(Arrays.asList("TERM_ID", "TERM_NAME", "DOCUMENT_FREQUENCY",
				"CORPUS_FREQUENCY", "INVERSE_DOCUMENT_FREQUENCY", "CF_IDF"));
		AllTerms allTerms = new AllTerms();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JSONObject row = new JSONObject();

		row.put(columnNames.get(0), 1);
		row.put(columnNames.get(1), "a");
		row.put(columnNames.get(2), 3);
		row.put(columnNames.get(3), 3);
		row.put(columnNames.get(4), 1.7805573201471228);
		row.put(columnNames.get(5), 5.341671960441368);

		allTerms.setDatabase(database);
		allTerms.setOutputStream(new PrintStream(out));

		allTerms.executeQueriesAndWriteAllTerms();
		assertThat(out.toString()).startsWith("{\"" + row.get("TERM_ID") + "\":" + row.toString());
	}
}
