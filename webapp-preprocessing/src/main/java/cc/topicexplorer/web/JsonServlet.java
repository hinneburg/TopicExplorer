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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cc.commandmanager.core.Context;
import cc.topicexplorer.database.Database;

/**
 * Servlet implementation class TestServlet
 */
public class JsonServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String command = request.getParameter("Command");

		response.setCharacterEncoding("UTF8");

		PrintWriter writer = response.getWriter();

		Context context = new Context(WebChainManagement.getContext());
		context.bind("SERVLET_WRITER", writer);
		// Properties properties = (Properties) context.get("properties");
		Database database = (Database) context.get("database");

		JSONArray all = new JSONArray();
		ArrayList<Integer> parents = new ArrayList<Integer>();

		try {
			if (command.equals("init")) {
				ResultSet docCountRs = database
						.executeQuery("SELECT COUNT(distinct DOCUMENT_ID) AS docCount, "
								+ "COUNT(*) AS wordCount, COUNT(DISTINCT WORDTYPE_CLASS) AS wordtypeCount FROM DOCUMENT_TERM");
				if (docCountRs.next()) {
					writer.write("{\"DOCUMENT_COUNT\": "
							+ docCountRs.getInt("docCount")
							+ ", \"WORD_COUNT\": "
							+ docCountRs.getInt("wordCount")
							+ ", \"WORDTYPE_COUNT\": "
							+ docCountRs.getInt("wordtypeCount") + ",");
				}
				ResultSet maxWordCountRs = database
						.executeQuery("SELECT COUNT(*) AS maxWordCount "
								+ "FROM DOCUMENT_TERM GROUP BY DOCUMENT_ID ORDER BY maxWordCount DESC LIMIT 1");
				if (maxWordCountRs.next()) {
					writer.write("\"MAX_WORD_COUNT\": "
							+ maxWordCountRs.getInt("maxWordCount") + ",");
				}
				ResultSet minWordCountRs = database
						.executeQuery("SELECT COUNT(*) AS minWordCount "
								+ "FROM DOCUMENT_TERM GROUP BY DOCUMENT_ID ORDER BY minWordCount LIMIT 1");
				if (minWordCountRs.next()) {
					writer.write("\"MIN_WORD_COUNT\": "
							+ minWordCountRs.getInt("minWordCount") + ",");
				}
				ResultSet wordLengthRs = database
						.executeQuery("SELECT MIN(CHAR_LENGTH(TOKEN)) AS minWordLength, "
								+ "MAX(CHAR_LENGTH(TOKEN)) AS maxWordLength, AVG(CHAR_LENGTH(TOKEN)) AS avgWordLength "
								+ "FROM DOCUMENT_TERM WHERE CHAR_LENGTH(TOKEN) > 0");
				if (wordLengthRs.next()) {
					writer.write("\"MIN_WORD_LENGTH\": "
							+ wordLengthRs.getInt("minWordLength") + ",");
					writer.write("\"MAX_WORD_LENGTH\": "
							+ wordLengthRs.getInt("maxWordLength") + ",");
					writer.write("\"AVG_WORD_LENGTH\": "
							+ wordLengthRs.getFloat("avgWordLength") + ",");
				}
				ResultSet wordtypeCountRs = database
						.executeQuery("SELECT * FROM POS_TYPE "
								+ "ORDER BY LOW, HIGH desc");
				parents.add(-1);
				addChildren(wordtypeCountRs, all, parents);
				writer.write("\"WORDTYPE_WORDCOUNTS\":" + all.toString() + "}");
			} else if (command.equals("getWordlist")) {
				int low = Integer.parseInt(request.getParameter("low"));
				int high = Integer.parseInt(request.getParameter("high"));
				ResultSet wordsRs = database.executeQuery("SELECT DOCUMENT_TERM.TOKEN, COUNT(DOCUMENT_TERM.TOKEN) AS COUNT "
						+ "FROM DOCUMENT_TERM, POS_TYPE WHERE "
						+ "POS_TYPE.POS=DOCUMENT_TERM.WORDTYPE_CLASS AND POS_TYPE.LOW>=" + low
						+ " AND POS_TYPE.HIGH<=" + high + " GROUP BY DOCUMENT_TERM.TOKEN ORDER BY COUNT DESC");
				writer.write("{\"TOKEN\":[");
				if(wordsRs.next()) {
					writer.write("{\"TOKEN\":\"" + wordsRs.getString("TOKEN") + "\",\"COUNT\":" + wordsRs.getInt("COUNT") + "}");
					while(wordsRs.next()) {
						writer.write(",{\"TOKEN\":\"" + wordsRs.getString("TOKEN") + "\",\"COUNT\":" + wordsRs.getInt("COUNT") + "}");
					}
				}
				writer.write("]}");
			}

		} catch (Exception e) {
			System.err.println("Error " + e);
			System.exit(0);
		}

	}

	// Full texts POS LOW HIGH DESCRIPTION PARENT_POS TOKEN_COUNT DOCUMENT_COUNT
	// TERM_COUNT MIN_TOKEN_LENGTH MAX_TOKEN_LENGTH AVG_TOKEN_LENGTH

	// SELECT p.POS, p.DESCRIPTION, SUM(x.wordCount) AS wordCount, p.PARENT_POS
	// FROM (SELECT WORDTYPE_CLASS, COUNT(*) AS wordCount FROM DOCUMENT_TERM
	// GROUP BY WORDTYPE_CLASS) x, POS_TYPE subtype, POS_TYPE p WHERE
	// subtype.POS = x.WORDTYPE_CLASS AND p.LOW <= subtype.LOW and
	// subtype.HIGH<=p.HIGH GROUP BY p.POS ORDER BY p.LOW, p.HIGH desc
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
