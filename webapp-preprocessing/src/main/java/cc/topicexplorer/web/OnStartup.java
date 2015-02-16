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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.DbConnectionCommand;
import cc.topicexplorer.commands.PropertiesCommand;
import cc.topicexplorer.utils.LoggerUtil;

public class OnStartup implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(OnStartup.class);
	private static boolean hasBeenInitialized = false;

	private ServletContext servletContext;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// This manually deregisters JDBC driver, which prevents Tomcat 7 from
		// complaining about memory leaks wrto this class
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
		if (!hasBeenInitialized) {

			System.out.println("Do on startup.");
			servletContext = arg0.getServletContext();

			Context context = new Context();
			executeInitialCommands(context);
			LoggerUtil.initializeLogger();

			if (this.getClass().getResource("/catalog.xml") != null) {
				String path = servletContext.getRealPath("/");
				File file = new File(path + "WEB-INF" + File.separator + "classes" + File.separator + "catalog.xml");
				if (file.delete()) {
					logger.info(file.getName() + " is deleted!");
				} else {
					logger.warn("Delete operation is failed.");
				}
			}

			String path = servletContext.getRealPath("/");
			String catalogLocation = path + "WEB-INF" + File.separator + "classes" + File.separator + "catalog.xml";
			try {
				PrintWriter pw = new PrintWriter(new FileWriter(catalogLocation));
				makeCatalogFromProperties(context.get("properties", Properties.class), pw);
			} catch (ParserConfigurationException e) {
				doOnCatalogException(e, catalogLocation);
			} catch (SAXException e) {
				doOnCatalogException(e, catalogLocation);
			} catch (IOException e) {
				doOnCatalogException(e, catalogLocation);
			} catch (TransformerException e) {
				doOnCatalogException(e, catalogLocation);
			}

			WebChainManagement.init(context, catalogLocation);
			hasBeenInitialized = true;
		}
	}

	private static void executeInitialCommands(Context context) {
		try {
			Command propertiesCommand = new PropertiesCommand();
			propertiesCommand.execute(context);

			Command dbConnectionCommand = new DbConnectionCommand();
			dbConnectionCommand.execute(context);
		} catch (RuntimeException rntmEx) {
			logger.error("Initialization abborted, due to a critical exception", rntmEx);
			throw rntmEx;
		}
	}

	private static void makeCatalogFromProperties(Properties properties, PrintWriter pw)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		// to be filled with makeCatalog() of RandomDocs.java
		String plugins = properties.getProperty("plugins");
		logger.info("Activated plugins: " + plugins);

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setIgnoringComments(true);
		DocumentBuilder builder = null;

		builder = domFactory.newDocumentBuilder();

		// init
		Document doc = builder.parse(OnStartup.class
				.getResourceAsStream("/cc/topicexplorer/core-webinterface/catalog/catalog.xml"));

		// process plugin catalogs
		for (String plugin : plugins.split(",")) {
			plugin = plugin.trim().toLowerCase();
			try {
				doc = getMergedXML(
						doc,
						builder.parse(OnStartup.class.getResourceAsStream("/cc/topicexplorer/plugin-" + plugin
								+ "-webinterface/catalog/catalog.xml")));
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

		String xmlOutput = result.getWriter().toString();

		pw.println(xmlOutput);
		pw.flush();
		pw.close();

	}

	private static Document getMergedXML(Document xmlFile1, Document xmlFile2) {
		NodeList nodes = xmlFile2.getElementsByTagName("catalog").item(0).getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node importNode = xmlFile1.importNode(nodes.item(i), true);
			xmlFile1.getElementsByTagName("catalog").item(0).appendChild(importNode);
		}
		return xmlFile1;
	}

	private void doOnCatalogException(Throwable t, String catalogLocation) {
		logger.warn("Problems occured while creating and filling the catalog at this location: " + catalogLocation, t);
	}

}
