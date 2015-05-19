package cc.topicexplorer.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import cc.commandmanager.core.Context;
import cc.topicexplorer.database.Database;

/**
 * Servlet implementation class TestServlet
 */
public class JsonServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String command = request.getParameter("Command");

		response.setCharacterEncoding("UTF8");

		PrintWriter writer = response.getWriter();

		Context context = new Context(WebChainManagement.getContext());
		context.bind("SERVLET_WRITER", writer);
		
		Properties properties = (Properties) context.get("properties");
		
		int offset = (request.getParameter("offset") != null) ? Integer.parseInt(request.getParameter("offset")) : 0;

		Set<String> startCommands = new HashSet<String>();
		if ("getDocBrowserLimit".equals(command)) {
			writer.print("{\"BrowserLimit\": " + properties.getProperty("DocBrowserLimit") + "}");
		} else if ("getWordtypeNames".equals(command)) {
			if(properties.getProperty("plugins").contains("mecab")) {
		
				Database database = (Database) context.get("database");
				JSONObject wordtypes = new JSONObject();
	
				try {
					ResultSet wordtypeNamesRs = database.executeQuery("SELECT POS, DESCRIPTION FROM POS_TYPE "
							+ "WHERE POS IN (" + properties.getProperty("Wordtype_wordtypes") + ")");
					while(wordtypeNamesRs.next()) {
						wordtypes.put(wordtypeNamesRs.getString("POS"), wordtypeNamesRs.getString("DESCRIPTION"));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				writer.print("{\"WordtypeNames\": " + wordtypes.toString() + "}");
			} else {
				writer.print("{\"WordtypeNames\": {}}");
			}
		} else if ("getActivePlugins".equals(command)) {
			String plugins = properties.getProperty("plugins");
			String[] pluginArray = plugins.split(",");
			List<String> pluginList = new ArrayList<String>();

			for (String element : pluginArray) {
				pluginList.add("\"" + element + "\"");
			}
			writer.print("{\"PLUGINS\":" + pluginList.toString() + "}");
		} else {
			if ("getDoc".equals(command)) {
				context.bind("SHOW_DOC_ID", Integer.parseInt(request.getParameter("DocId")));

				startCommands.add("ShowDocCoreCreate");
			} else if ("bestDocs".equals(command)) {
				context.bind("TOPIC_ID", Integer.parseInt(request.getParameter("TopicId")));
				context.bind("OFFSET", offset);
				@SuppressWarnings("unchecked")
				Enumeration<String> parameterNames = request.getParameterNames();
				while (parameterNames.hasMoreElements()) {
					String paramName = parameterNames.nextElement();
					context.bind(paramName, request.getParameter(paramName));
				}
				startCommands.add("BestDocsCoreCreate");
			} else if ("getTerms".equals(command)) {
				context.bind("TOPIC_ID", Integer.parseInt(request.getParameter("TopicId")));
				context.bind("OFFSET", offset);
				@SuppressWarnings("unchecked")
				Enumeration<String> parameterNames = request.getParameterNames();
				while (parameterNames.hasMoreElements()) {
					String paramName = parameterNames.nextElement();
					context.bind(paramName, request.getParameter(paramName));
					System.out.println(paramName + ": " + request.getParameter(paramName));
				}
				startCommands.add("GetTermsCoreCreate");
			} else if ("autocomplete".equals(command)) {
				String searchWord = request.getParameter("SearchWord");
				
				if(searchWord.length() < 1) {
					return;
				} 
				context.bind("SEARCH_WORD", searchWord);

				startCommands.add("AutocompleteCoreCreate");
			} else if ("search".equals(command)) {
				context.bind("SEARCH_WORD", request.getParameter("SearchWord"));
				context.bind("SEARCH_STRICT", request.getParameter("SearchStrict"));
				context.bind("OFFSET", offset);
				@SuppressWarnings("unchecked")
				Enumeration<String> parameterNames = request.getParameterNames();
				while (parameterNames.hasMoreElements()) {
					String paramName = parameterNames.nextElement();
					context.bind(paramName, request.getParameter(paramName));
				}
				startCommands.add("SearchCoreCreate");
			} else if ("getBestFrames".equals(command)) {
				startCommands.add("BestFrameCreate");
			} else if ("getBestTerms".equals(command)) {
				startCommands.add("BestTermsCreate");
			} else if ("getTopics".equals(command)) {
				
				startCommands.add("GetTopicsCoreCreate");
			} else if ("getFrameInfo".equals(command)) {
				startCommands.add("FrameInfoCreate");
			} else if ("getFrames".equals(command)) {
				context.bind("TOPIC_ID", Integer.parseInt(request.getParameter("topicId")));
				context.bind("OFFSET", offset);
				context.bind("FRAME_TYPE", request.getParameter("frameType"));
				
				startCommands.add("FrameCreate");
			} else if ("getDates".equals(command)) {
				startCommands.add("GetDatesTimeCreate");
			} else if ("getDateRange".equals(command)) {
				startCommands.add("GetDateRange");
			}
			WebChainManagement.executeCommands(WebChainManagement.getOrderedCommands(startCommands), context);
		} 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		// TODO Auto-generated method stub

	}
}
