package cc.topicexplorer.actions.getterms;

import static org.fest.assertions.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
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
		AllTerms allTerms = new AllTerms();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JSONObject row = new JSONObject();

		row.put("TERM_ID", 1);
		row.put("DOCUMENT_FREQUENCY", 3);
		row.put("CORPUS_FREQUENCY", 3);
		row.put("INVERSE_DOCUMENT_FREQUENCY", 1.7805573201471228);
		row.put("CF_IDF", 5.341671960441368);

		allTerms.setDatabase(database);
		allTerms.setOutputStream(new PrintStream(out));

		allTerms.executeQueriesAndWriteAllTerms();
		assertThat(out.toString()).startsWith("{\"" + row.get("TERM_ID") + "\":" + row.toString());
	}
}
