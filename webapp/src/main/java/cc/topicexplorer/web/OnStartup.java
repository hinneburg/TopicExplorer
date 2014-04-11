package cc.topicexplorer.web;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
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

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cc.commandmanager.core.CommunicationContext;

public class OnStartup implements ServletContextListener {
	private static Logger logger = Logger.getRootLogger();
	private ServletContext servletContext;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		 // This manually deregisters JDBC driver, which prevents Tomcat 7 from complaining about memory leaks wrto this class 
		Enumeration<Driver> drivers = DriverManager.getDrivers(); 
		while (drivers.hasMoreElements()) { 
			Driver driver = drivers.nextElement(); 
			try { 
				DriverManager.deregisterDriver(driver); 
				logger.log(Level.INFO, String.format("deregistering jdbc driver: %s", driver)); 
			} catch (SQLException e) { 
				logger.log(Level.ERROR, String.format("Error deregistering driver %s", driver), e); 
			} 
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("Do on startup.");
		servletContext = arg0.getServletContext();

		initializeLogger("logs/webapp.log");

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

	private void initializeLogger(String logfileName) {
		try {
			logger.addAppender(new FileAppender(new PatternLayout("%d-%p-%C-%M-%m%n"), logfileName, false));
			logger.setLevel(Level.INFO); // ALL | DEBUG | INFO | WARN | ERROR |
			// FATAL | OFF:
		} catch (IOException e) {
			logger.error("FileAppender with log file " + logfileName + " could not be constructed.");
			throw new RuntimeException(e);
		}
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
			} catch (IllegalStateException e) {
				logger.warn(e);
			} catch (IllegalArgumentException e) {
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

	private Document getMergedXML(Document xmlFile1, Document xmlFile2) {
		NodeList nodes = xmlFile2.getElementsByTagName("catalog").item(0).getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node importNode = xmlFile1.importNode(nodes.item(i), true);
			xmlFile1.getElementsByTagName("catalog").item(0).appendChild(importNode);
		}
		return xmlFile1;
	}

}
