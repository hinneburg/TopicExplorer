package cc.topicexplorer.web;

import java.io.IOException;
import java.io.PrintWriter;
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

import cc.commandmanager.core.Context;

/**
 * Servlet implementation class TestServlet
 */
public class JsonServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(JsonServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String command = request.getParameter("Command");

		response.setCharacterEncoding("UTF8");

		PrintWriter writer = response.getWriter();

		Context context = WebChainManagement.getContext();
		context.bind("SERVLET_WRITER", writer);

		int offset = (request.getParameter("offset") != null) ? Integer.parseInt(request.getParameter("offset")) : 0;

		Set<String> startCommands = new HashSet<String>();

		if (command != null) {
			if (command.contains("getDoc")) {
				context.bind("SHOW_DOC_ID", request.getParameter("DocId"));

				startCommands.add("ShowDocCoreCreate");
			} else if (command.contains("bestDocs")) {
				context.bind("TOPIC_ID", request.getParameter("TopicId"));
				context.bind("OFFSET", offset);
				@SuppressWarnings("unchecked")
				Enumeration<String> parameterNames = request.getParameterNames();
				while (parameterNames.hasMoreElements()) {
					String paramName = parameterNames.nextElement();
					context.bind(paramName, request.getParameter(paramName));
				}
				startCommands.add("BestDocsCoreCreate");
			} else if (command.contains("allTerms")) {
				startCommands.add("AllTermsCoreCreate");
			} else if (command.contains("autocomplete")) {
				context.bind("SEARCH_WORD", request.getParameter("SearchWord"));

				startCommands.add("AutocompleteCoreCreate");
			} else if (command.contains("search")) {
				context.bind("SEARCH_WORD", request.getParameter("SearchWord"));
				context.bind("OFFSET", offset);

				startCommands.add("SearchCoreCreate");
			} else if (command.contains("getBestFrames")) {
				startCommands.add("BestFrameCreate");
			} else if (command.contains("getFrames")) {
				context.bind("TOPIC_ID", request.getParameter("topicId"));
				context.bind("OFFSET", offset);

				startCommands.add("FrameCreate");
			} else if (command.contains("getDates")) {
				startCommands.add("GetDatesTimeCreate");
			}
			WebChainManagement.executeCommands(WebChainManagement.getOrderedCommands(startCommands), context);
		} else {
			startCommands.add("InitCoreCreate");
			writer.print("{\"FRONTEND_VIEWS\":" + this.getFrontendViews((Properties) context.get("properties"))
					+ ",\"JSON\":");

			WebChainManagement.executeCommands(WebChainManagement.getOrderedCommands(startCommands), context);

			Properties properties = (Properties) context.get("properties");
			String plugins = properties.getProperty("plugins");
			String[] pluginArray = plugins.split(",");
			List<String> pluginList = new ArrayList<String>();

			for (String element : pluginArray) {
				pluginList.add("\"" + element + "\"");
			}
			writer.print(", \"PLUGINS\":" + pluginList.toString());
			writer.print(", \"LIMIT\":" + Integer.parseInt(properties.getProperty("DocBrowserLimit")));
			writer.print("}");
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

	private String getFrontendViews(Properties properties) {
		String plugins = properties.getProperty("plugins");
		String pluginArray[] = plugins.split(",");
		List<String> frontendViews = new ArrayList<String>();

		// init
		String frontendViewArray[] = properties.get("FrontendViews").toString().split(",");
		for (int j = 0; j < frontendViewArray.length; j++) {
			if (!frontendViews.contains("\"" + frontendViewArray[j] + "\"")) {
				frontendViews.add("\"" + frontendViewArray[j] + "\"");
			}
		}
		for (String element : pluginArray) {
			try {
				frontendViewArray = properties
						.get(element.substring(0, 1).toUpperCase() + element.substring(1) + "_FrontendViews")
						.toString().split(",");
				for (int k = 0; k < frontendViewArray.length; k++) {
					if (!frontendViews.contains("\"" + frontendViewArray[k] + "\"")) {
						frontendViews.add("\"" + frontendViewArray[k] + "\"");
					}
				}

			} catch (Exception e) { // TODO Specify exception type!
				logger.info("Property " + element.substring(0, 1).toUpperCase() + element.substring(1)
						+ "_FrontendViews not found");
			}
		}
		logger.info(frontendViews.toString());
		return frontendViews.toString();

	}
}
