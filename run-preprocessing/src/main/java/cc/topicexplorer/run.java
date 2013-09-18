package cc.topicexplorer;

import java.util.List;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
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

public class run {
	private static Logger logger;

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
		run run = new run();
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setIgnoringComments(true);
		DocumentBuilder builder = null;

		builder = domFactory.newDocumentBuilder();

		// init
		Document doc = this.getMergedXML(
				builder.parse(this.getClass().getResourceAsStream("/cc/topicexplorer/core-preprocessing/catalog/preJooqConfig.xml")),
				builder.parse(this.getClass().getResourceAsStream("/cc/topicexplorer/core-preprocessing/catalog/postJooqConfig.xml")));

		// process plugin catalogs
		for (String plugin : plugins.split(",")) {
			plugin = plugin.trim().toLowerCase();
			try {
				doc = this.getMergedXML(
						doc,
						builder.parse(this.getClass().getResourceAsStream("/cc/topicexplorer/plugin-"
								+ plugin + "-preprocessing/catalog/preJooqConfig.xml")));
			} catch (Exception e) {
				//logger.warn
				System.out.println("cc/topicexplorer/plugin/"
						+ plugin + "/catalog/preJooqConfig.xml not found");
			}
			try {
				doc = this.getMergedXML(
						doc,
						builder.parse(this.getClass().getResourceAsStream("/cc/topicexplorer/plugin-"
								+ plugin + "-preprocessing/catalog/postJooqConfig.xml")));
			} catch (Exception e) {
				//logger.warn
				System.out.println("cc/topicexplorer/plugin/"
						+ plugin + "/catalog/postJooqConfig.xml not found");
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
		run run = new run();
		logger = Logger.getRootLogger();
		Properties properties = new Properties();
		properties.load(run.getClass().getResourceAsStream(
				"/config.global.properties"));
		try {
			properties.load(run.getClass().getResourceAsStream(
					"/config.local.properties"));
		} catch (Exception e) {
			//logger.warn
			System.out.println("config.local.properties not found");
		}
		System.out.println(properties.getProperty("plugins"));

		run.makeCatalog(properties.getProperty("plugins"));

		
		 ChainManagement chainManager = new ChainManagement();
		 chainManager.getCatalog("catalog.xml");
		 
		 List<String> orderedCommands = chainManager.getOrderedCommands();
		 
		 logger.info("ordered commands: " + orderedCommands);
		 
		 chainManager.executeOrderedCommands(orderedCommands);
		 
	}

}