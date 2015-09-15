package cc.topicexplorer.web;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.commandmanager.core.Context;

public class UserTest extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Context context = WebChainManagement.getContext();
		context.bind("SERVLET_WRITER", response.getWriter());
		WebChainManagement.executeCommands((List<String>) new HashSet<String>(), context);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	IOException {

	}

}
