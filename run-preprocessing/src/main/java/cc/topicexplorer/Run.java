package cc.topicexplorer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cc.topicexplorer.chain.ChainManagement;

public class Run {
	private static Logger logger = Logger.getRootLogger();

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
		Document doc = this
				.getMergedXML(
						builder.parse(this
								.getClass()
								.getResourceAsStream(
										"/cc/topicexplorer/core-preprocessing/catalog/preJooqConfig.xml")),
						builder.parse(this
								.getClass()
								.getResourceAsStream(
										"/cc/topicexplorer/core-preprocessing/catalog/postJooqConfig.xml")));

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
														+ "-preprocessing/catalog/preJooqConfig.xml")));
			} catch (Exception e) {
				logger.warn("/cc/topicexplorer/plugin-" + plugin
						+ "-preprocessing/catalog/preJooqConfig.xml not found");
			}
			try {
				doc = this
						.getMergedXML(
								doc,
								builder.parse(this
										.getClass()
										.getResourceAsStream(
												"/cc/topicexplorer/plugin-"
														+ plugin
														+ "-preprocessing/catalog/postJooqConfig.xml")));
			} catch (Exception e) {
				logger.warn("/cc/topicexplorer/plugin-" + plugin
						+ "-preprocessing/catalog/postJooqConfig.xml not found");
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

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Run run = new Run();
		ChainManagement chainManager = new ChainManagement();
		RunCommandLineParser commandLineParser = new RunCommandLineParser(args);
		Properties properties = new Properties();

		// create directories
		File temp = new File("temp");
		temp.mkdir();

		chainManager.init();

		try {
			properties.load(run.getClass().getResourceAsStream(
					"/config.global.properties"));
		} catch (Exception e) {
			logger.fatal("config.global.properties not found");
			System.exit(0);
		}

		try {
			properties.load(run.getClass().getResourceAsStream(
					"/config.local.properties"));
		} catch (Exception e) {
			logger.warn("config.local.properties not found");
		}
		logger.info("Activated plugins: " + properties.getProperty("plugins"));

		run.makeCatalog(properties.getProperty("plugins"));

		chainManager.setCatalog("/catalog.xml");

		List<String> orderedCommands = chainManager.getOrderedCommands(
				commandLineParser.getStartCommands(),
				commandLineParser.getEndCommands());

		logger.info("ordered commands: " + orderedCommands);

		if (!commandLineParser.getDrawGraph()) {
			chainManager.executeOrderedCommands(orderedCommands);
			System.out.println("Preprocessing successfully executed!");
		}	

		FileUtils.deleteDirectory(temp);
	}

	/**
	 * Retrieves arguments from the commandline and makes them accessable via a
	 * getter method
	 * 
	 * @author Sebastian Baer
	 * 
	 */
	private static class RunCommandLineParser {
		private Options options;

		private CommandLineParser commandLineParser;
		private CommandLine commandLine;
		private HelpFormatter helpFormatter;

		private boolean onlyDrawGraph = false;
		private String catalogLocation;
		private List<String> startCommand = new ArrayList<String>();
		private List<String> endCommand = new ArrayList<String>();

		private String[] args;

		/**
		 * Adds the possible arguments. Sets global args and executes the
		 * parsing of the given arguments.
		 * 
		 * @param args
		 */
		public RunCommandLineParser(String[] args) {
			options = new Options();
			options.addOption("h", "help", false,
					"prints information about passing arguments.");
			options.addOption("c", "catalog", true,
					"determines location of catalog file");
			options.getOption("c").setArgName("string");
			options.addOption("g", "graph", false, "only the graph is drawed");
			options.addOption("s", "start", true,
					"set commands to start with, separated by only comma");
			options.getOption("s").setArgName("string");
			options.addOption("e", "end", true,
					"set commands to end with, separated only by comma");
			options.getOption("e").setArgName("string");

			commandLineParser = new BasicParser();
			commandLine = null;
			helpFormatter = new HelpFormatter();

			this.args = args;

			parseArguments();
		}

		/**
		 * Checks if any of the mentioned options is contained in the arguments
		 * and then sets it in the class. If the usage of arguments is wrong
		 * help is printed.
		 */
		public void parseArguments() {
			// if there is something wrong with the input, print help
			try {
				commandLine = commandLineParser.parse(options, args);
			} catch (Exception e) {
				printHelp();
				System.out.println("Usage of arguments wrong.");
				System.exit(1);
			}

			if (commandLine.hasOption("h")) {
				printHelp();
			}

			if (commandLine.hasOption("g")) {
				onlyDrawGraph = true;
			}

			if (commandLine.hasOption("c")) {
				catalogLocation = commandLine.getOptionValue("c");
			} else {
				System.out.println("No catalog location given, taking standard value.");
			}

			if (commandLine.hasOption("s")) {
				startCommand = Arrays.asList(commandLine.getOptionValue("s")
						.split(","));
			}

			if (commandLine.hasOption("e")) {
				endCommand = Arrays.asList(commandLine.getOptionValue("s")
						.split(","));
			}
		}

		public String getCatalogLocation() {
			return catalogLocation;
		}

		public boolean getDrawGraph() {
			return onlyDrawGraph;
		}

		public List<String> getStartCommands() {
			return startCommand;
		}

		public List<String> getEndCommands() {
			return endCommand;
		}

		public void printHelp() {
			helpFormatter.printHelp("Run <command> [<arg>]", options);
		}
	}

}
