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

import cc.commandmanager.core.Context;

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

		int offset = (request.getParameter("offset") != null) ? Integer.parseInt(request.getParameter("offset")) : 0;

		Set<String> startCommands = new HashSet<String>();
		if (!command.contains("getActivePlugins")) {
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
			} else if (command.contains("getTerms")) {
				context.bind("TOPIC_ID", request.getParameter("TopicId"));
				context.bind("OFFSET", offset);
				@SuppressWarnings("unchecked")
				Enumeration<String> parameterNames = request.getParameterNames();
				while (parameterNames.hasMoreElements()) {
					String paramName = parameterNames.nextElement();
					context.bind(paramName, request.getParameter(paramName));
				}
				startCommands.add("GetTermsCoreCreate");
			} else if (command.contains("autocomplete")) {
				context.bind("SEARCH_WORD", request.getParameter("SearchWord"));

				startCommands.add("AutocompleteCoreCreate");
			} else if (command.contains("search")) {
				context.bind("SEARCH_WORD", request.getParameter("SearchWord"));
				context.bind("OFFSET", offset);

				startCommands.add("SearchCoreCreate");
			} else if (command.contains("getBestFrames")) {
				startCommands.add("BestFrameCreate");
			} else if (command.contains("getBestTerms")) {
				startCommands.add("BestTermsCreate");
			} else if (command.contains("getTopics")) {
				
				startCommands.add("GetTopicsCoreCreate");
			} else if (command.contains("getFrameInfo")) {
				startCommands.add("FrameInfoCreate");
			} else if (command.contains("getFrames")) {
				context.bind("TOPIC_ID", request.getParameter("topicId"));
				context.bind("OFFSET", offset);
				context.bind("FRAME_TYPE", request.getParameter("frameType"));
				
				startCommands.add("FrameCreate");
			} else if (command.contains("getDates")) {
				startCommands.add("GetDatesTimeCreate");
			}
			WebChainManagement.executeCommands(WebChainManagement.getOrderedCommands(startCommands), context);
		} else {
			Properties properties = (Properties) context.get("properties");
			String plugins = properties.getProperty("plugins");
			String[] pluginArray = plugins.split(",");
			List<String> pluginList = new ArrayList<String>();

			for (String element : pluginArray) {
				pluginList.add("\"" + element + "\"");
			}
			writer.print("{\"PLUGINS\":" + pluginList.toString() + "}");
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
