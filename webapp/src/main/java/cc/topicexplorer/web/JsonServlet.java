package cc.topicexplorer.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.log4j.Logger;


import cc.topicexplorer.chain.CommunicationContext;

/**
 * Servlet implementation class TestServlet
 */
public class JsonServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getRootLogger();

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String command = request.getParameter("Command");
		
		response.setCharacterEncoding("UTF8");
		
		PrintWriter writer = response.getWriter();
		
		CommunicationContext communicationContext = WebChainManagement
				.getCommunicationContext();
		communicationContext.put("SERVLET_WRITER", writer);

		Set<String> startCommands = new HashSet<String>();
		Set<String> endCommands = new HashSet<String>();

		if(command != null) {
			communicationContext.put("SHOW_DOC_ID", request.getParameter("DocId"));
			
			startCommands.add("ShowDocCoreCreate");
			WebChainManagement.executeCommands(WebChainManagement
					.getOrderedCommands(startCommands,
							endCommands), communicationContext);	
		} else {
			startCommands.add("InitCoreCreate");
	
			writer.print("{\"FRONTEND_VIEWS\":" + this.getFrontendViews((Properties)communicationContext.get("properties")) + ",\"JSON\":");

			WebChainManagement.executeCommands(WebChainManagement
					.getOrderedCommands(startCommands,
							endCommands), communicationContext);
			
			writer.print("}");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}
	
	private String getFrontendViews(Properties properties) {
		String plugins = properties.getProperty("plugins");
		String pluginArray[] = plugins.split(",");
		List<String> frontendViews = new ArrayList<String>();
		
		// init
		String frontendViewArray[] = properties.get("FrontendViews").toString().split(",");
		for (int j = 0; j < frontendViewArray.length; j++) {
			if(!frontendViews.contains("\"" + frontendViewArray[j] + "\"")) {
				frontendViews.add("\"" + frontendViewArray[j] + "\"");
			}
		}
		for (int i = 0; i < pluginArray.length; i++) {
			try {
				frontendViewArray = properties.get(pluginArray[i].substring(0, 1).toUpperCase() + pluginArray[i].substring(1) + "_FrontendViews").toString().split(",");
				for (int k = 0; k < frontendViewArray.length; k++) {
					if(!frontendViews.contains("\"" + frontendViewArray[k] + "\"")) {
						frontendViews.add("\"" + frontendViewArray[k] + "\"");
					}
				}
			}catch(Exception e) {
				logger.info("Property " + pluginArray[i].substring(0, 1).toUpperCase() + pluginArray[i].substring(1) + "_FrontendViews not found");
			}
		}
		logger.info(frontendViews.toString());
		return frontendViews.toString();
		
	}
}
