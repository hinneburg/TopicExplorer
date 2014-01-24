package cc.topicexplorer.web;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
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

import cc.topicexplorer.chain.CommunicationContext;

public class OnStartup implements ServletContextListener {
	private static Logger logger = Logger.getRootLogger();
	private ServletContext servletContext;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("Do on startup.");
		servletContext = arg0.getServletContext();
		WebChainManagement.init();
		if (this.getClass().getResource("/catalog.xml") != null) {
			String path = servletContext.getRealPath("/");
			File file = new File(path + "WEB-INF" + File.separator + "classes" + File.separator + "catalog.xml");
			if (file.delete()) {
				logger.info(file.getName() + " is deleted!");
			} else {
				logger.warn("Delete operation is failed.");
			}
		}
		try {
			makeCatalog();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		WebChainManagement.setCatalog("/catalog.xml");
	}

	private Document getMergedXML(Document xmlFile1, Document xmlFile2) {
		NodeList nodes = xmlFile2.getElementsByTagName("catalog").item(0).getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node importNode = xmlFile1.importNode(nodes.item(i), true);
			xmlFile1.getElementsByTagName("catalog").item(0).appendChild(importNode);
		}
		return xmlFile1;
	}

	private void makeCatalog() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		// to be filled with makeCatalog() of RandomDocs.java
		CommunicationContext communicationContext = WebChainManagement.getCommunicationContext();
		Properties properties = (Properties) communicationContext.get("properties");

		String plugins = properties.getProperty("plugins");

		logger.info("Activated plugins: " + plugins);

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
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
				doc = this.getMergedXML(
						doc,
						builder.parse(this.getClass().getResourceAsStream(
								"/cc/topicexplorer/plugin-" + plugin + "-webinterface/catalog/catalog.xml")));
			} catch (SAXException saxEx) {
				logger.warn("/cc/topicexplorer/plugin-" + plugin + "-webinterface/catalog/catalog.xml not found", saxEx);
			} catch (IOException ioEx) {
				logger.warn("/cc/topicexplorer/plugin-" + plugin + "-webinterface/catalog/catalog.xml not found", ioEx);
			} 
			catch (Exception e) {
				logger.warn(e);
			}
		}

		// write out
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new StringWriter());
		transformer.transform(source, result);

		String path = servletContext.getRealPath("/");
		PrintWriter pw = new PrintWriter(new FileWriter(path + "WEB-INF" + File.separator + "classes" + File.separator
				+ "catalog.xml"));

		String xmlOutput = result.getWriter().toString();

		pw.println(xmlOutput);
		pw.flush();
		pw.close();

	}
}
