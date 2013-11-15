package cc.topicexplorer.actions.getterms;

import static org.fest.assertions.Assertions.assertThat;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.sql.SQLException;

import net.sf.json.JSONObject;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cc.topicexplorer.util.DbUnitContainer;

public final class AllTermsTest {
	/*
	 * The database is the same with both dbunitConnection and
	 * getTopicExplorerDatabase()
	 */
	IDatabaseConnection dbUnitConnection = DbUnitContainer.getDbunitConnection();

	@Before
	public void setUp() throws SQLException, DatabaseUnitException, IOException {
		try {
			IDataSet dataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(
					"src/test/resources/dataset_maerchen_1st_row.xml"));
			DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, dataSet);
		} catch (DatabaseUnitException e) {
			System.err.println(e);
			throw e;
		}
	}

	@Test
	@Ignore("Todo: Reactivate test when HSQLDB was provided")
	public void testReadAllTermsAndGenerateJson_oneRow() throws SQLException, DataSetException,
			MalformedURLException {
		IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new File(
				"src/test/resources/dataset_maerchen_1st_row.xml"));
		ITable expectedTable = expectedDataSet.getTable("TERM");

		CharArrayWriter allTermsStream = new CharArrayWriter();
		AllTerms allTerms = new AllTerms(DbUnitContainer.getTopicExplorerDatabase(), new PrintWriter(allTermsStream));

		JSONObject row = new JSONObject();
		row.put("TERM_ID", Integer.parseInt((String) expectedTable.getValue(0, "TERM_ID")));
		row.put("DOCUMENT_FREQUENCY", Integer.parseInt((String) expectedTable.getValue(0, "DOCUMENT_FREQUENCY")));
		row.put("CORPUS_FREQUENCY", Integer.parseInt((String) expectedTable.getValue(0, "CORPUS_FREQUENCY")));
		row.put("INVERSE_DOCUMENT_FREQUENCY",
				Double.parseDouble((String) expectedTable.getValue(0, "INVERSE_DOCUMENT_FREQUENCY")));
		row.put("CF_IDF", Double.parseDouble((String) expectedTable.getValue(0, "CF_IDF")));

		JSONObject testTable = new JSONObject();
		testTable.put(expectedTable.getValue(0, "TERM_ID"), row);

		allTerms.readAllTermsAndGenerateJson();
		assertThat(testTable.toString()).isEqualTo(allTermsStream.toString());
	}
}
