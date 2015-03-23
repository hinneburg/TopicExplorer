package cc.topicexplorer.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cc.commandmanager.core.Context;
import cc.topicexplorer.database.Database;

/**
 * Servlet implementation class TestServlet
 */
public class JsonServlet extends HttpServlet {

	private static final Logger logger = Logger.getLogger(JsonServlet.class);
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String command = request.getParameter("Command");

		response.setCharacterEncoding("UTF8");

		PrintWriter writer = response.getWriter();

		Context context = new Context(WebChainManagement.getContext());
		context.bind("SERVLET_WRITER", writer);
		
		Database database = (Database) context.get("database");
	
		try {
			if (command.equals("init")) {
				long before = System.currentTimeMillis();
				ResultSet docCountRs = database
						.executeQuery("SELECT COUNT(distinct DOCUMENT_ID) AS docCount, "
								+ "SUM(TOKEN_COUNT) AS wordCount, COUNT(DISTINCT WORDTYPE_CLASS) AS wordtypeCount FROM DOCUMENT_WORDTYPE");
				if (docCountRs.next()) {
					writer.write("{\"DOCUMENT_COUNT\": "
							+ docCountRs.getInt("docCount")
							+ ", \"WORD_COUNT\": "
							+ docCountRs.getInt("wordCount")
							+ ", \"WORDTYPE_COUNT\": "
							+ docCountRs.getInt("wordtypeCount") + ",");
				}
				logger.info("DocCount: " + (System.currentTimeMillis() - before) + "ms");
				before = System.currentTimeMillis();
				ResultSet maxWordCountRs = database
						.executeQuery("SELECT SUM(TOKEN_COUNT) AS maxWordCount "
								+ "FROM DOCUMENT_WORDTYPE "
								+ "WHERE WORDTYPE_CLASS IN (SELECT POS FROM POS_TYPE WHERE PARENT_POS=-1) "
								+ "GROUP BY DOCUMENT_ID "
								+ "ORDER BY maxWordCount DESC LIMIT 1");
				if (maxWordCountRs.next()) {
					writer.write("\"MAX_WORD_COUNT\": "
							+ maxWordCountRs.getInt("maxWordCount") + ",");
				}
				logger.info("MaxWordCount: " + (System.currentTimeMillis() - before) + "ms");
				before = System.currentTimeMillis();
				ResultSet minWordCountRs = database
						.executeQuery("SELECT SUM(TOKEN_COUNT) AS minWordCount "
								+ "FROM DOCUMENT_WORDTYPE "
								+ "WHERE WORDTYPE_CLASS IN (SELECT POS FROM POS_TYPE WHERE PARENT_POS=-1) "
								+ "GROUP BY DOCUMENT_ID "
								+ "ORDER BY minWordCount LIMIT 1");
				if (minWordCountRs.next()) {
					writer.write("\"MIN_WORD_COUNT\": "
							+ minWordCountRs.getInt("minWordCount") + ",");
				}
				logger.info("MinWordCount: " + (System.currentTimeMillis() - before) + "ms");
				before = System.currentTimeMillis();
				ResultSet wordLengthRs = database
						.executeQuery("SELECT MIN(MIN_TOKEN_LENGTH) AS minWordLength, "
								+ "MAX(MAX_TOKEN_LENGTH) AS maxWordLength, SUM(SUM_TOKEN_LENGTH)/SUM(TOKEN_COUNT)  AS avgWordLength "
								+ "FROM DOCUMENT_WORDTYPE WHERE WORDTYPE_CLASS IN (SELECT POS FROM POS_TYPE WHERE PARENT_POS=-1)");
				if (wordLengthRs.next()) {
					writer.write("\"MIN_WORD_LENGTH\": "
							+ wordLengthRs.getInt("minWordLength") + ",");
					writer.write("\"MAX_WORD_LENGTH\": "
							+ wordLengthRs.getInt("maxWordLength") + ",");
					writer.write("\"AVG_WORD_LENGTH\": "
							+ wordLengthRs.getFloat("avgWordLength") + ",");
				}
				logger.info("WordLengths: " + (System.currentTimeMillis() - before) + "ms");
				before = System.currentTimeMillis();
				ResultSet wordtypeCountRs = database
						.executeQuery("SELECT * FROM POS_TYPE "
								+ "ORDER BY LOW, HIGH desc");
				JSONArray all = new JSONArray();
				ArrayList<Integer> parents = new ArrayList<Integer>();
				parents.add(-1);
				addChildren(wordtypeCountRs, all, parents);
				writer.write("\"WORDTYPE_WORDCOUNTS\":" + all.toString() + "}");
				logger.info("wordtypes: " + (System.currentTimeMillis() - before) + "ms");
			} else if (command.equals("getWordlist")) {
				int pos = Integer.parseInt(request.getParameter("pos"));
				ResultSet wordsRs = database.executeQuery("SELECT TERM, COUNT "
						+ "FROM ALL_TERMS WHERE POS=" + pos + " ORDER BY COUNT DESC");
				writer.write("{\"TERM\":[");
				if(wordsRs.next()) {
					writer.write("{\"TERM\":\"" + wordsRs.getString("TERM").replace("\"", "\\\"") + "\",\"COUNT\":" + wordsRs.getInt("COUNT") + "}");
					while(wordsRs.next()) {
						writer.write(",{\"TERM\":\"" + wordsRs.getString("TERM").replace("\"", "\\\"") + "\",\"COUNT\":" + wordsRs.getInt("COUNT") + "}");
					}
				}
				writer.write("]}");
			}

		} catch (Exception e) {
			System.err.println("Error " + e);
			System.exit(0);
		}

	}


	public void addChildren(ResultSet rs, JSONArray in,
			ArrayList<Integer> parents) throws SQLException {
		while (rs.next()) {
			int parentPos = rs.getInt("PARENT_POS");
			int pos = rs.getInt("POS");
			if (parents.get(parents.size() - 1) == parentPos) {
				JSONObject object = new JSONObject();
				object.put("POS", pos);
				object.put("LABEL", rs.getString("DESCRIPTION"));
				object.put("PARENT", parentPos);
				object.put("COUNT", rs.getInt("TOKEN_COUNT"));
				object.put("DOCUMENT_COUNT", rs.getInt("DOCUMENT_COUNT"));
				object.put("TERM_COUNT", rs.getInt("TERM_COUNT"));
				object.put("MIN_TOKEN_LENGTH", rs.getInt("MIN_TOKEN_LENGTH"));
				object.put("MAX_TOKEN_LENGTH", rs.getInt("MAX_TOKEN_LENGTH"));
				object.put("AVG_TOKEN_LENGTH", rs.getFloat("AVG_TOKEN_LENGTH"));
				object.put("LOW", rs.getInt("LOW"));
				object.put("HIGH", rs.getInt("HIGH"));
				parents.add(pos);
				JSONArray children = new JSONArray();
				addChildren(rs, children, parents);
				object.put("CHILDREN", children);
				in.add(object);
			} else if (parents.contains(parentPos)) {
				rs.previous();
				parents.remove(parents.size() - 1);
				return;
			}
		}
	}
}
