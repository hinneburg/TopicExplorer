package cc.topicexplorer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cc.commandmanager.core.ChainManagement;
import cc.commandmanager.core.CommunicationContext;
import cc.commandmanager.core.DependencyCommand;
import cc.topicexplorer.commands.DbConnectionCommand;
import cc.topicexplorer.commands.PropertiesCommand;
import cc.topicexplorer.utils.CommandLineParser;

public class Run {
	private static Logger logger = Logger.getRootLogger();

	public static void main(String[] args) throws Exception {
		Run run = new Run();

		run.initializeLogger("logs/Preprocessing.log");

		CommandLineParser commandLineParser = null;
		try {
			commandLineParser = new CommandLineParser(args);
		} catch (RuntimeException e) {
			logger.error("Problems encountered while parsing the command line tokens.");
			throw e;
		}

		File temp = new File("temp");
		temp.mkdir();

		ChainManagement chainManager = new ChainManagement();
		// chainManager.init();
		CommunicationContext context = new CommunicationContext();
		run.executeInitialCommands(context);

		Properties properties = new Properties();
		try {
			properties.load(run.getClass().getResourceAsStream("/config.global.properties"));
		} catch (IOException e) {
			logger.error("config.global.properties not found");
			throw e;
		}
		try {
			properties.load(run.getClass().getResourceAsStream("/config.local.properties"));
		} catch (IOException e) {
			logger.warn("config.local.properties not found", e);
		}
		logger.info("Activated plugins: " + properties.getProperty("plugins"));

		run.makeCatalog(properties.getProperty("plugins"));
		chainManager.setCatalog("/catalog.xml");

		List<String> orderedCommands = chainManager.getOrderedCommands(commandLineParser.getStartCommands(),
				commandLineParser.getEndCommands());
		logger.info("ordered commands: " + orderedCommands);

		if (!commandLineParser.getOnlyDrawGraph()) {
			chainManager.executeCommands(orderedCommands, context);
			logger.info("Preprocessing successfully executed!");
		}

		FileUtils.deleteDirectory(temp);
	}

	private void executeInitialCommands(CommunicationContext context) {
		try {
			DependencyCommand propertiesCommand = new PropertiesCommand();
			propertiesCommand.execute(context);

			DependencyCommand dbConnectionCommand = new DbConnectionCommand();
			dbConnectionCommand.execute(context);
		} catch (RuntimeException rntmEx) {
			logger.error("Initialization abborted, due to a critical exception");
			throw rntmEx;
		}
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

	private void makeCatalog(String plugins) throws ParserConfigurationException, TransformerException, IOException,
			SAXException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setIgnoringComments(true);
		DocumentBuilder builder = null;

		builder = domFactory.newDocumentBuilder();

		// init
		Document doc = getMergedXML(
				builder.parse(this.getClass().getResourceAsStream(
						"/cc/topicexplorer/core-preprocessing/catalog/preJooqConfig.xml")),
				builder.parse(this.getClass().getResourceAsStream(
						"/cc/topicexplorer/core-preprocessing/catalog/postJooqConfig.xml")));

		// process plugin catalogs
		for (String plugin : plugins.split(",")) {
			plugin = plugin.trim().toLowerCase();

			try {
				doc = getMergedXML(
						doc,
						builder.parse(this.getClass().getResourceAsStream(
								"/cc/topicexplorer/plugin-" + plugin + "-preprocessing/catalog/preJooqConfig.xml")));
			} catch (SAXException e) {
				logger.warn(
						"/cc/topicexplorer/plugin-" + plugin + "-preprocessing/catalog/preJooqConfig.xml not found", e);
			} catch (IOException ioEx) {
				logger.warn(
						"/cc/topicexplorer/plugin-" + plugin + "-preprocessing/catalog/preJooqConfig.xml not found",
						ioEx);
			}

			try {
				doc = getMergedXML(
						doc,
						builder.parse(this.getClass().getResourceAsStream(
								"/cc/topicexplorer/plugin-" + plugin + "-preprocessing/catalog/postJooqConfig.xml")));
			} catch (SAXException e) {
				logger.warn("/cc/topicexplorer/plugin-" + plugin
						+ "-preprocessing/catalog/postJooqConfig.xml not found", e);
			} catch (IOException ioEx) {
				logger.warn("/cc/topicexplorer/plugin-" + plugin
						+ "-preprocessing/catalog/postJooqConfig.xml not found", ioEx);
			}
		}

		// write out
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new StringWriter());
		transformer.transform(source, result);

		Writer output = new BufferedWriter(new FileWriter("catalog.xml"));
		String xmlOutput = result.getWriter().toString();
		output.write(xmlOutput);
		output.close();

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
