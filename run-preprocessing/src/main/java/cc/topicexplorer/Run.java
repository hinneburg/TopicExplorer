package cc.topicexplorer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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
import org.apache.log4j.Logger;
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
import cc.topicexplorer.utils.LoggerUtil;

public class Run {

	private static final String CATALOG_FILENAME = "catalog.xml";
	private static final Logger logger = Logger.getLogger(Run.class);

	public static void main(String[] args) throws Exception {
		LoggerUtil.initializeLogger();

		Run run = new Run();
		run.logWelcomeMessage();

		File temp = new File("temp");
		temp.mkdir();

		try {
			CommandLineParser commandLineParser = initializeCommandLineParser(args);
			runPreprocessing(commandLineParser.getStartCommands(), commandLineParser.getEndCommands(),
					!commandLineParser.getOnlyDrawGraph());
		} catch (Exception exception) {
			logger.error("Preprocessing could not be completed.", exception);
		} finally {
			FileUtils.deleteDirectory(temp);
		}
	}

	private static CommandLineParser initializeCommandLineParser(String[] args) {
		CommandLineParser commandLineParser = null;
		try {
			commandLineParser = new CommandLineParser(args);
		} catch (RuntimeException exception) {
			logger.error("Problems encountered while parsing the command line tokens.");
			throw exception;
		}
		return commandLineParser;
	}

	/**
	 * Execute initial commands, collect plugin names from local and global config properties, create catalog, let
	 * chainManager order all commands, execute ordered commands.Users can specify from which command to start the
	 * execution and which command should be the last one to execute. Specification can be made via start- and end
	 * command parameter respectively.
	 * 
	 * @throws RuntimeException
	 *             commands can throw multiple RuntimeExceptions. This would signalize a corrupt preprocessing result.
	 */
	private static void runPreprocessing(Set<String> startCommands, Set<String> endCommands,
			boolean commandsShouldGetExecuted) throws ParserConfigurationException, TransformerException, IOException,
			SAXException {
		Date start = new Date();
		CommunicationContext context = new CommunicationContext();
		executeInitialCommands(context);

		Properties properties = (Properties) context.get("properties");
		String plugins = properties.getProperty("plugins");
		logger.info("Activated plugins: " + plugins);

		makeCatalog(plugins);
		ChainManagement chainManager = new ChainManagement();
		chainManager.setCatalog("/" + CATALOG_FILENAME);

		List<String> orderedCommands = chainManager.getOrderedCommands(startCommands, endCommands);
		logger.info("ordered commands: " + orderedCommands);

		if (commandsShouldGetExecuted) {
			chainManager.executeCommands(orderedCommands, context);
			logger.info("Preprocessing successfully executed!");
		}
		Date end = new Date();
		logger.info("Execution time: " + (end.getTime() - start.getTime()) / 1000 + " seconds.");
	}

	private void logWelcomeMessage() {
		logger.info("#####################################");
		logger.info("# R U N   P R E P R O C E S S I N G #");
		logger.info("#####################################");
	}

	private static void executeInitialCommands(CommunicationContext context) {
		try {
			DependencyCommand propertiesCommand = new PropertiesCommand();
			propertiesCommand.execute(context);

			DependencyCommand dbConnectionCommand = new DbConnectionCommand();
			dbConnectionCommand.execute(context);
		} catch (RuntimeException exception) {
			logger.error("Initialization abborted, due to a critical exception");
			throw exception;
		}
	}

	private static void makeCatalog(String plugins) throws ParserConfigurationException, TransformerException,
			IOException, SAXException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setIgnoringComments(true);
		DocumentBuilder builder = null;

		builder = domFactory.newDocumentBuilder();

		// init
		Document doc = getMergedXML(builder.parse(Run.class
				.getResourceAsStream("/cc/topicexplorer/core-preprocessing/catalog/preJooqConfig.xml")),
				builder.parse(Run.class
						.getResourceAsStream("/cc/topicexplorer/core-preprocessing/catalog/postJooqConfig.xml")));

		// process plugin catalogs
		for (String plugin : plugins.split(",")) {
			plugin = plugin.trim().toLowerCase();

			try {
				doc = getMergedXML(
						doc,
						builder.parse(Run.class.getResourceAsStream("/cc/topicexplorer/plugin-" + plugin
								+ "-preprocessing/catalog/preJooqConfig.xml")));
			} catch (SAXException saxException) {
				logger.warn(
						"/cc/topicexplorer/plugin-" + plugin + "-preprocessing/catalog/preJooqConfig.xml not found",
						saxException);
			} catch (IOException ioException) {
				logger.warn(
						"/cc/topicexplorer/plugin-" + plugin + "-preprocessing/catalog/preJooqConfig.xml not found",
						ioException);
			}

			try {
				doc = getMergedXML(
						doc,
						builder.parse(Run.class.getResourceAsStream("/cc/topicexplorer/plugin-" + plugin
								+ "-preprocessing/catalog/postJooqConfig.xml")));
			} catch (SAXException saxException) {
				logger.warn("/cc/topicexplorer/plugin-" + plugin
						+ "-preprocessing/catalog/postJooqConfig.xml not found", saxException);
			} catch (IOException ioException) {
				logger.warn("/cc/topicexplorer/plugin-" + plugin
						+ "-preprocessing/catalog/postJooqConfig.xml not found", ioException);
			}
		}

		// write out
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new StringWriter());
		transformer.transform(source, result);

		Writer output = new BufferedWriter(new FileWriter(CATALOG_FILENAME));
		String xmlOutput = result.getWriter().toString();
		output.write(xmlOutput);
		output.close();

	}

	private static Document getMergedXML(Document xmlFile1, Document xmlFile2) {
		NodeList nodes = xmlFile2.getElementsByTagName("catalog").item(0).getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node importNode = xmlFile1.importNode(nodes.item(i), true);
			xmlFile1.getElementsByTagName("catalog").item(0).appendChild(importNode);
		}
		return xmlFile1;
	}
}
