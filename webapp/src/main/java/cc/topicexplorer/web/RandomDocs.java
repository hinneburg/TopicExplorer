package cc.topicexplorer.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cc.topicexplorer.chain.ChainManagement;
import cc.topicexplorer.chain.CommunicationContext;
/**
 * Servlet implementation class TestServlet
 */
public class RandomDocs extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getRootLogger();
    /**
     * @throws Exception 
     * @see HttpServlet#HttpServlet()
     */
//    public TestServlet() throws Exception {
//        super();
//    }
//
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("UTF8");
		PrintWriter writer = response.getWriter();
		CommunicationContext communicationContext = new CommunicationContext();
        
		communicationContext.put("SERVLET_WRITER", writer);
		
		ChainManagement chainManager = new ChainManagement(communicationContext);
        chainManager.init();

		String catalogLocation = "/catalog.xml";

		try {
			chainManager.setCatalog(catalogLocation);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		logger.info("1");
		Set<String> orderedCommands = chainManager.getOrderedCommands();
				

		logger.info("ordered commands: " + orderedCommands);

		chainManager.executeOrderedCommands(orderedCommands);
        // TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

}
