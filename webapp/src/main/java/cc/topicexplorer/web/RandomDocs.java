package cc.topicexplorer.web;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
	// public TestServlet() throws Exception {
	// super();
	// }
	//
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("UTF8");
		PrintWriter writer = response.getWriter();
		CommunicationContext communicationContext = new CommunicationContext();

		communicationContext.put("SERVLET_WRITER", writer);

		ChainManagement chainManager = new ChainManagement(communicationContext);
		chainManager.init();
		
		if (this.getClass().getResource("/catalog.xml") == null) {
			Properties properties = (Properties) communicationContext
					.get("properties");

			String plugins = properties.getProperty("plugins");
			
			logger.info("Activated plugins: " + plugins);
			try {
				makeCatalog(plugins);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			chainManager.setCatalog("/catalog.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Set<String> startCommands = new HashSet<String>();
		Set<String> endCommands = new HashSet<String>();

		startCommands.add("InitCoreCreate");

		List<String> orderedCommands = chainManager.getOrderedCommands(
				startCommands, endCommands);

		logger.info("ordered commands: " + orderedCommands);

		chainManager.executeOrderedCommands(orderedCommands);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

	private Document getMergedXML(Document xmlFile1, Document xmlFile2) {
		NodeList nodes = xmlFile2.getElementsByTagName("catalog").item(0)
				.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node importNode = xmlFile1.importNode(nodes.item(i), true);
			xmlFile1.getElementsByTagName("catalog").item(0)
					.appendChild(importNode);
		}
		return xmlFile1;
	}

	private void makeCatalog(String plugins)
			throws ParserConfigurationException, TransformerException,
			IOException, SAXException {

		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setIgnoringComments(true);
		DocumentBuilder builder = null;

		builder = domFactory.newDocumentBuilder();

		// init
		Document doc = builder.parse(this.getClass().getResourceAsStream(
				"/cc/topicexplorer/core-webinterface/catalog/catalog.xml"));

		// process plugin catalogs
		for (String plugin : plugins.split(",")) {
			plugin = plugin.trim().toLowerCase();
			try {
				doc = this
						.getMergedXML(
								doc,
								builder.parse(this
										.getClass()
										.getResourceAsStream(
												"/cc/topicexplorer/plugin-"
														+ plugin
														+ "-webinterface/catalog/catalog.xml")));
			} catch (Exception e) {
				logger.warn("/cc/topicexplorer/plugin-" + plugin
						+ "-webinterface/catalog/catalog.xml not found");
			}
		}

		// write out
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new StringWriter());
		transformer.transform(source, result);

		ServletContext context = getServletContext();
		String path = context.getRealPath("/");
		PrintWriter pw = new PrintWriter(new FileWriter(path + "WEB-INF" + File.separator + "classes" + File.separator + "catalog.xml"));
		
		String xmlOutput = result.getWriter().toString();
		
		pw.println(xmlOutput);
		pw.flush();
		pw.close();
	}

}
