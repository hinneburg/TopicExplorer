package cc.topicexplorer.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

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

import com.google.common.base.Joiner;
import com.mysql.jdbc.AbandonedConnectionCleanupThread;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.DbConnectionCommand;
import cc.topicexplorer.commands.PropertiesCommand;
import cc.topicexplorer.utils.LoggerUtil;

public class OnStartup implements ServletContextListener {

	private static final Logger logger = Logger.getLogger(OnStartup.class);
	private static boolean hasBeenInitialized = false;

	@Override
	public void contextDestroyed(final ServletContextEvent arg0) {
		// This manually deregisters JDBC driver, which prevents Tomcat 7 from
		// complaining about memory leaks wrto this class
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
				logger.log(Level.INFO,
						String.format("deregistering jdbc driver: %s", driver));
			} catch (SQLException e) {
				logger.log(Level.ERROR,
						String.format("Error deregistering driver %s", driver),
						e);
			}
		}
		try {
			AbandonedConnectionCleanupThread.shutdown();
		} catch (InterruptedException e) {
			logger.warn("SEVERE problem cleaning up: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void contextInitialized(final ServletContextEvent arg0) {
		if (!hasBeenInitialized) {
			Context context = new Context();
			executeInitialCommands(context);
			LoggerUtil.initializeLogger();

			logger.info("Do on startup.");

			String[] plugins = getActivedPluginsFromProperties(context.get(
					"properties", Properties.class));
			logger.info("Activated plugins: " + Joiner.on(',').join(plugins));

			try {
				Document mergedCatalog = generatedMergedPluginCatalog(plugins);
				logger.info("Merged Catalog\n" + catalog2String(mergedCatalog));
				WebChainManagement.init(context, mergedCatalog);
			} catch (ParserConfigurationException e) {
				doOnMergeCatalogException(e);
			} catch (SAXException e) {
				doOnMergeCatalogException(e);
			} catch (TransformerException e) {
				doOnMergeCatalogException(e);
			} catch (IOException e) {
				doOnMergeCatalogException(e);
			}
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
			logger.error(
					"Initialization abborted, due to a critical exception during initial Commands (Properties, DbConnection)",
					rntmEx);
			throw rntmEx;
		}
	}

	private static String[] getActivedPluginsFromProperties(
			Properties properties) {
		String pluginsString = properties.getProperty("plugins");
		String[] plugins = pluginsString.split(",");
		for (String plugin : plugins) {
			plugin.trim().toLowerCase();
		}
		return plugins;
	}

	private static Document generatedMergedPluginCatalog(String[] plugins)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setIgnoringComments(true);
		DocumentBuilder builder = null;

		builder = domFactory.newDocumentBuilder();

		// init
		Document doc = builder
				.parse(OnStartup.class
						.getResourceAsStream("/cc/topicexplorer/core-webinterface/catalog/catalog.xml"));

		// process plugin catalogs
		for (String plugin : plugins) {
			InputStream inPluginCatalog = OnStartup.class
					.getResourceAsStream("/cc/topicexplorer/plugin-" + plugin
							+ "-webinterface/catalog/catalog.xml");
			if (inPluginCatalog == null) {
				logger.warn("/cc/topicexplorer/plugin-" + plugin
						+ "-webinterface/catalog/catalog.xml not found");
				continue;
			}

			try {
				doc = getMergedXML(doc, builder.parse(inPluginCatalog));
			} catch (SAXException saxEx) {
				logger.warn("/cc/topicexplorer/plugin-" + plugin
						+ "-webinterface/catalog/catalog.xml not found", saxEx);
			} catch (IOException ioEx) {
				logger.warn("/cc/topicexplorer/plugin-" + plugin
						+ "-webinterface/catalog/catalog.xml not found", ioEx);
			} catch (IllegalStateException e) {
				logger.warn(e);
			}
		}

		return doc;
	}

	private static String catalog2String(Document doc)
			throws TransformerException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new StringWriter());
		transformer.transform(source, result);

		String xmlOutput = result.getWriter().toString();
		return xmlOutput;
	}

	private static Document getMergedXML(Document xmlFile1, Document xmlFile2) {
		NodeList nodes = xmlFile2.getElementsByTagName("catalog").item(0)
				.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node importNode = xmlFile1.importNode(nodes.item(i), true);
			xmlFile1.getElementsByTagName("catalog").item(0)
					.appendChild(importNode);
		}
		return xmlFile1;
	}

	private void doOnMergeCatalogException(Throwable t) {
		logger.warn("Problems occured while merging the catalog", t);
	}

}
