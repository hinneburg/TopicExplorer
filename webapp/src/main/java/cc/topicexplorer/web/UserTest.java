package cc.topicexplorer.web;

import java.io.IOException;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.commandmanager.core.CommunicationContext;

public class UserTest extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		CommunicationContext communicationContext = WebChainManagement
				.getCommunicationContext();

		communicationContext.put("SERVLET_WRITER", response.getWriter());

		WebChainManagement.executeCommands(WebChainManagement
				.getOrderedCommands(new HashSet<String>(),
						new HashSet<String>()), communicationContext);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

}
