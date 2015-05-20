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

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import cc.commandmanager.core.Context;
import cc.topicexplorer.database.Database;

/**
 * Servlet implementation class TestServlet
 */
public class JsonServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(JsonServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		synchronized (this) {
		
		String command = request.getParameter("Command");
		
		logger.info("Recieved request with command: " + command);

		response.setCharacterEncoding("UTF8");

		PrintWriter writer = response.getWriter();

		Context context = new Context(WebChainManagement.getContext());
		context.bind("SERVLET_WRITER", writer);
		
		Properties properties = (Properties) context.get("properties");
		
		int offset = (request.getParameter("offset") != null) ? Integer.parseInt(request.getParameter("offset")) : 0;

		Set<String> startCommands = new HashSet<String>();
		if ("getDocBrowserLimit".equals(command)) {
			logger.info("Detected command getDocBrowserLimit.");

			writer.print("{\"BrowserLimit\": " + properties.getProperty("DocBrowserLimit") + "}");
		} else if ("getWordtypeNames".equals(command)) {

			logger.info("Detected command getWordTypeNames.");
			if(properties.getProperty("plugins").contains("mecab")) {

				logger.info("Plugin mecab is active.");

				Database database = (Database) context.get("database");
				JSONObject wordtypes = new JSONObject();
	
				logger.info("Retrieving word type names for POS IDs:" + properties.getProperty("Wordtype_wordtypes"));
				try {
					ResultSet wordtypeNamesRs = database.executeQuery("SELECT POS, DESCRIPTION FROM POS_TYPE "
							+ "WHERE POS IN (" + properties.getProperty("Wordtype_wordtypes") + ")");
					while(wordtypeNamesRs.next()) {
						wordtypes.put(wordtypeNamesRs.getString("POS"), wordtypeNamesRs.getString("DESCRIPTION"));
					}
					wordtypeNamesRs.close();
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
			logger.info("Detected command getActivePlugins.");

			String plugins = properties.getProperty("plugins");
			String[] pluginArray = plugins.split(",");
			List<String> pluginList = new ArrayList<String>();

			for (String element : pluginArray) {
				pluginList.add("\"" + element + "\"");
			}
			writer.print("{\"PLUGINS\":" + pluginList.toString() + "}");
		} else {
			if ("getDoc".equals(command)) {
				logger.info("Detected command getDoc.");

				context.bind("SHOW_DOC_ID", Integer.parseInt(request.getParameter("DocId")));

				startCommands.add("ShowDocCoreCreate");
			} else if ("bestDocs".equals(command)) {
				logger.info("Detected command bestDocs.");

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
				logger.info("Detected command getTerms.");

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
				logger.info("Detected command autocomplete.");

				String searchWord = request.getParameter("SearchWord");
				
				if(searchWord.length() < 1) {
					return;
				} 
				context.bind("SEARCH_WORD", searchWord);

				startCommands.add("AutocompleteCoreCreate");
			} else if ("search".equals(command)) {
				logger.info("Detected command search.");

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
				logger.info("Detected command getBestFrames.");

				startCommands.add("BestFrameCreate");
			} else if ("getBestTerms".equals(command)) {
				logger.info("Detected command getBestTerms.");

				startCommands.add("BestTermsCreate");
			} else if ("getTopics".equals(command)) {
				logger.info("Detected command getTopics.");

				startCommands.add("GetTopicsCoreCreate");
			} else if ("getFrameInfo".equals(command)) {
				logger.info("Detected command getFrameInfo.");

				startCommands.add("FrameInfoCreate");
			} else if ("getFrames".equals(command)) {
				logger.info("Detected command getFrames.");

				context.bind("TOPIC_ID", Integer.parseInt(request.getParameter("topicId")));
				context.bind("OFFSET", offset);
				context.bind("FRAME_TYPE", request.getParameter("frameType"));
				
				startCommands.add("FrameCreate");
			} else if ("getDates".equals(command)) {
				logger.info("Detected command getDates.");

				startCommands.add("GetDatesTimeCreate");
			} else if ("getDateRange".equals(command)) {
				logger.info("Detected command getDateRange.");

				startCommands.add("GetDateRange");
			}
			
			if (!startCommands.isEmpty()) {
				WebChainManagement.executeCommands(WebChainManagement.getOrderedCommands(startCommands), context);
			}
			else {
				logger.warn("Detected request with non-matching command. No commands executed.");
			}
		}
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
