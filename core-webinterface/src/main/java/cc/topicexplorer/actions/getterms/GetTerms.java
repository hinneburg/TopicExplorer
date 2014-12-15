package cc.topicexplorer.actions.getterms;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import cc.topicexplorer.database.Database;
import cc.topicexplorer.database.SelectMap;

public final class GetTerms {
	private PrintWriter outWriter;
	private Database database;
	private final SelectMap databaseQuery;
	private static final Logger logger = Logger.getLogger(GetTerms.class);
	private int topicId;

	/*ResultSet topicTermRS = database
						.executeQuery("SELECT TERM_ID, NUMBER_OF_DOCUMENT_TOPIC FROM TERM_TOPIC WHERE TOPIC_ID="
								+ topicRS.getString("TOPIC_ID")
								+ " ORDER BY NUMBER_OF_DOCUMENT_TOPIC DESC LIMIT 20");*/
	
	
	public GetTerms(Database db, PrintWriter out, int topicId, int offset) {
		this.setDatabase(db);
		this.setServletWriter(out);
		this.databaseQuery = new SelectMap();
		
		this.addTableColumn("TERM.TERM_ID", "TERM_ID");
		this.addTableColumn("TERM_TOPIC.NUMBER_OF_DOCUMENT_TOPIC", "NUMBER_OF_DOCUMENT_TOPIC");
		this.addTableColumn("TERM.TERM_NAME", "TERM_NAME");
		
		this.databaseQuery.from.add("TERM_TOPIC");
		this.databaseQuery.from.add("TERM");
		
		this.databaseQuery.where.add("TERM.TERM_ID=TERM_TOPIC.TERM_ID");
		this.databaseQuery.where.add("TERM_TOPIC.TOPIC_ID=" + topicId);
		
		this.databaseQuery.orderBy.add("NUMBER_OF_DOCUMENT_TOPIC DESC");
		
		this.databaseQuery.limit = 20;
		this.databaseQuery.offset = offset;
		
		this.topicId = topicId;
	}

	private void setServletWriter(PrintWriter out) {
		if (out == null) {
			logger.error("ServletWriter could not be set.");
			throw new IllegalArgumentException("PrintWriter argument must not be null.");
		}
		this.outWriter = out;
	}

	private void setDatabase(Database database) {
		if (database == null) {
			logger.error("Database could not be set.");
			throw new IllegalArgumentException("Database argument must not be null.");
		}
		this.database = database;
	}

	public void addTableColumn(String tableColumn, String tableColumnName) {
		this.databaseQuery.select.add(tableColumn + " as " + tableColumnName);
	}
	
	public void addWhereClause(String where) {
		this.databaseQuery.where.add(where);
	}

	
	public void readAllTermsAndGenerateJson() {
		List<String> columnNamesWithoutRelevance = new ArrayList<String>();
		ArrayList<Integer> termList = new ArrayList<Integer>();

		JSONArray topTerms = new JSONArray();
		JSONObject topic = new JSONObject();
		JSONObject topics = new JSONObject();
		JSONObject topTerm = new JSONObject();
		JSONObject term = new JSONObject();
		JSONObject terms = new JSONObject();
		JSONObject all = new JSONObject();
		ResultSet resultSet;

		try {
			resultSet = database.executeQuery(this.databaseQuery.getSQLString());

			String columnNameTmp;
			for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
				columnNameTmp = resultSet.getMetaData().getColumnName(i + 1);
				if (!columnNameTmp.equals("NUMBER_OF_DOCUMENT_TOPIC")) {
					columnNamesWithoutRelevance.add(columnNameTmp);
				}
			}

			
			while (resultSet.next()) {
				topTerm.put("TermId", resultSet.getString("TERM_ID"));
				topTerm.put("relevance", resultSet.getString("NUMBER_OF_DOCUMENT_TOPIC"));
				topTerms.add(topTerm);
				termList.add(resultSet.getInt("TERM_ID"));
				for (String columnName : columnNamesWithoutRelevance) {
					term.put(columnName, resultSet.getObject(columnName));
				}
				terms.put(resultSet.getString("TERM_ID"), term);
				
			}
			topic.put("Top_Terms", topTerms);
			topTerms.clear();
			topics.put(topicId, topic);
			all.put("Topic", topics);
			all.put("Term", terms);

		} catch (SQLException e) {
			logger.error("Error in Query: " + databaseQuery.getSQLString());
			throw new RuntimeException(e);
		}

		this.outWriter.print(all);
	}
}
