package cc.topicexplorer.actions.getterms;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cc.topicexplorer.database.Database;

public final class AllTermsTest {
	private ArrayList<String> _columnNames;
	private int _term_id1;
	private BigInteger _document_frequency1;
	private BigInteger _corpus_frequency1;
	private Double _inv_document_frequency1;
	private Double _cf_idf1;
	private int _term_id2;
	private BigInteger _document_frequency2;
	private BigInteger _corpus_frequency2;
	private Double _inv_document_frequency2;
	private Double _cf_idf2;
	private AllTerms _allTerms;
	private CharArrayWriter _testStream;

	@Mock
	private Database _mockedDatabase;
	@Mock
	private ResultSet _mockedResultSet;
	@Mock
	private ResultSetMetaData _mockedMetaData;

	@Before
	public void setUp() throws SQLException {
		_columnNames = new ArrayList<String>(Arrays.asList("TERM_ID", "DOCUMENT_FREQUENCY", "CORPUS_FREQUENCY",
				"INVERSE_DOCUMENT_FREQUENCY", "CF_IDF"));
		_term_id1 = 1;
		_document_frequency1 = BigInteger.valueOf(3);
		_corpus_frequency1 = BigInteger.valueOf(3);
		_inv_document_frequency1 = new Double(1.7805573201471228);
		_cf_idf1 = new Double(5.341671960441368);
		_term_id2 = 2;
		_document_frequency2 = BigInteger.valueOf(1);
		_corpus_frequency2 = BigInteger.valueOf(1);
		_inv_document_frequency2 = new Double(2.2576785748691846);
		_cf_idf2 = new Double(2.2576785748691846);

		MockitoAnnotations.initMocks(this);
		when(
				_mockedDatabase.executeQuery("SELECT TERM_ID as TERM_ID, DOCUMENT_FREQUENCY as DOCUMENT_FREQUENCY, "
						+ "CORPUS_FREQUENCY as CORPUS_FREQUENCY, "
						+ "INVERSE_DOCUMENT_FREQUENCY as INVERSE_DOCUMENT_FREQUENCY, CF_IDF as CF_IDF FROM TERM"))
				.thenReturn(_mockedResultSet);
		when(_mockedResultSet.getMetaData()).thenReturn(_mockedMetaData);
		when(_mockedMetaData.getColumnCount()).thenReturn(5);
		when(_mockedMetaData.getColumnName(1)).thenReturn(_columnNames.get(0));
		when(_mockedMetaData.getColumnName(2)).thenReturn(_columnNames.get(1));
		when(_mockedMetaData.getColumnName(3)).thenReturn(_columnNames.get(2));
		when(_mockedMetaData.getColumnName(4)).thenReturn(_columnNames.get(3));
		when(_mockedMetaData.getColumnName(5)).thenReturn(_columnNames.get(4));

		_testStream = new CharArrayWriter();
		_allTerms = new AllTerms(_mockedDatabase, new PrintWriter(_testStream));
	}

	@Test
	public void testReadAllTermsAndGenerateJson_oneRow() throws SQLException {
		when(_mockedResultSet.next()).thenReturn(true).thenReturn(false);
		when(_mockedResultSet.getObject(_columnNames.get(0))).thenReturn(_term_id1);
		when(_mockedResultSet.getObject(_columnNames.get(1))).thenReturn(_document_frequency1);
		when(_mockedResultSet.getObject(_columnNames.get(2))).thenReturn(_corpus_frequency1);
		when(_mockedResultSet.getObject(_columnNames.get(3))).thenReturn(_inv_document_frequency1);
		when(_mockedResultSet.getObject(_columnNames.get(4))).thenReturn(_cf_idf1);

		JSONObject row = new JSONObject();
		row.put(_columnNames.get(0), _term_id1);
		row.put(_columnNames.get(1), _document_frequency1);
		row.put(_columnNames.get(2), _corpus_frequency1);
		row.put(_columnNames.get(3), _inv_document_frequency1);
		row.put(_columnNames.get(4), _cf_idf1);
		JSONObject testTable = new JSONObject();
		testTable.put(_term_id1, row);

		_allTerms.readAllTermsAndGenerateJson();
		assertThat(_testStream.toString()).isEqualTo(testTable.toString());
	}

	@Test
	public void testReadAllTermsAndGenerateJson_twoRows() throws SQLException {
		when(_mockedResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
		when(_mockedResultSet.getObject(_columnNames.get(0))).thenReturn(_term_id1).thenReturn(_term_id2);
		when(_mockedResultSet.getObject(_columnNames.get(1))).thenReturn(_document_frequency1).thenReturn(
				_document_frequency2);
		when(_mockedResultSet.getObject(_columnNames.get(2))).thenReturn(_corpus_frequency1).thenReturn(
				_corpus_frequency2);
		when(_mockedResultSet.getObject(_columnNames.get(3))).thenReturn(_inv_document_frequency1).thenReturn(
				_inv_document_frequency2);
		when(_mockedResultSet.getObject(_columnNames.get(4))).thenReturn(_cf_idf1).thenReturn(_cf_idf2);

		JSONObject row1 = new JSONObject();
		row1.put(_columnNames.get(0), _term_id1);
		row1.put(_columnNames.get(1), _document_frequency1);
		row1.put(_columnNames.get(2), _corpus_frequency1);
		row1.put(_columnNames.get(3), _inv_document_frequency1);
		row1.put(_columnNames.get(4), _cf_idf1);
		JSONObject row2 = new JSONObject();
		row2.put(_columnNames.get(0), _term_id2);
		row2.put(_columnNames.get(1), _document_frequency2);
		row2.put(_columnNames.get(2), _corpus_frequency2);
		row2.put(_columnNames.get(3), _inv_document_frequency2);
		row2.put(_columnNames.get(4), _cf_idf2);
		JSONObject testTable = new JSONObject();
		testTable.put(_term_id1, row1);
		testTable.put(_term_id2, row2);

		_allTerms.readAllTermsAndGenerateJson();
		assertThat(_testStream.toString()).isEqualTo(testTable.toString());
	}
}
