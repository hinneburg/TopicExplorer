package cc.topicexplorer.dataimport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import cc.topicexplorer.utils.PropertiesUtil;
import cc.topicexplorer.utils.LoggerUtil;

public class ImportFolder {

	private static final Logger logger = Logger.getLogger(ImportFolder.class);

	private static String readFileFromClasspath(String fileName) throws IOException {
		InputStream fis = ImportFolder.class.getResourceAsStream("/" + fileName);
		return IOUtils.toString(fis, "UTF-8");
	}

	public static void main(String[] args) throws Exception {
        LoggerUtil.initializeLogger();


		Option folderOption = new Option("f", "folder", true, "folder with files that are imported");
		folderOption.setRequired(true);
		
		Option teiXmlOption = new Option("t", "tei", false, "files to be imported have TEI-XML format");
		teiXmlOption.setRequired(false);

		Options commands = new Options();
		commands.addOption(folderOption);
		commands.addOption(teiXmlOption);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(commands, args, true);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			formatter.printHelp("import-folder", commands);
			System.exit(1);
			return;
		}

		String pathToImportFolder = cmd.getOptionValue("folder");
		
		
		File importFolder = new File(pathToImportFolder);
		
		if (! importFolder.isDirectory()) {
			logger.error("specified folder "+ pathToImportFolder +" is not a folder.");
			System.exit(1);
			return;
			
		}
		
		String corpusName = importFolder.getName().toUpperCase();  // this returns just the last name in the path sequence 
		
		
		Properties dbProps = PropertiesUtil.updateOptionalProperties(new Properties(), "cmdb", "");

		Connection con = DriverManager.getConnection("jdbc:mysql://" + dbProps.getProperty("DbLocation")
				+ "?useUnicode=true&characterEncoding=UTF-8&useCursorFetch=true", dbProps.getProperty("DbUser"),
				dbProps.getProperty("DbPassword"));

		String sqlStatementFiles[] = {"te-create-corpus-text-table.sql"
				, "te-create-corpus-meta-table.sql"
				, "insert-search-string.sql" // important: execute insert-search-string.sql before insert-crawl.sq
				, "insert-crawl.sql"   // search_id is determined by max()
				};
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("corpus", corpusName);
		
		for (final String sqlStatementFile : sqlStatementFiles ) {
			String sqlTemplate = readFileFromClasspath(sqlStatementFile);
			String sqlStatement = StringSubstitutor.replace(sqlTemplate, parameters);
			
			Statement stmt = con.createStatement();
			int createTableRS = stmt.executeUpdate(sqlStatement);
		}
		
		File [] textFiles = importFolder.listFiles();

		logger.info("list of files created");
		
		if (cmd.hasOption("tei")) {
//			insertTeiXmlDocumentsByCharacterStream(null, textFiles, corpusName);
			insertTeiXmlDocumentsByCharacterStream(con, textFiles, corpusName);
		} else {
//			insertDocumentsByCharacterStream(null, textFiles, corpusName);
			insertDocumentsByCharacterStream(con, textFiles, corpusName);
		}
		
		logger.info("all text files imported");

		
	}

	private static void insertTeiXmlDocumentsByCharacterStream(Connection con, File[] teiXmlFiles, String corpusName)
			throws IOException, SQLException {
		Integer documentId = 1;

		String sqlInsertMetaTemplate = readFileFromClasspath("insert-corpus-meta-table.sql");
		String sqlInsertTextTemplate = readFileFromClasspath("insert-corpus-text-table.sql");

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("corpus", corpusName);

		String sqlInsertMeta = StringSubstitutor.replace(sqlInsertMetaTemplate, parameters);
		String sqlInsertText = StringSubstitutor.replace(sqlInsertTextTemplate, parameters);

		PreparedStatement insertMeta = con.prepareStatement(sqlInsertMeta);
		PreparedStatement insertText = con.prepareStatement(sqlInsertText);

		
		for (final File teiXmlFileEntry : teiXmlFiles) {
			Document doc = Jsoup.parse(new FileInputStream(teiXmlFileEntry), "UTF-8", "", Parser.xmlParser());
					
			String title = doc.selectFirst("TEI > teiHeader > fileDesc > titleStmt > title").text();
			if ("".equals(title)) {
				title = "file: " + teiXmlFileEntry.getName();
			}

			Element firstAuthorNode = doc.selectFirst("TEI > teiHeader > fileDesc > sourceDesc > biblStruct > analytic > author > persName > surname");
			String firstAuthor = firstAuthorNode!=null ? firstAuthorNode.text() :"";
			Element  yearNode = doc.selectFirst("TEI > teiHeader > fileDesc > publicationStmt > date[type='published']");
			String year = yearNode!=null ? yearNode.attr("when") :"";
			
			Element  doiNode = doc.selectFirst("TEI > teiHeader > fileDesc > sourceDesc > biblStruct > idno[type='DOI']");
			String doi = doiNode!=null ? doiNode.text() :"";
						
		    Element  abstractNode = doc.selectFirst("TEI > teiHeader > profileDesc > abstract");
			String abstractText = abstractNode!=null ? abstractNode.text() :"";
			
			String documentText = abstractText;
			Integer documentParts=0;
			for(Element textPartNode: doc.select("TEI > text > body > div") ) {
				for (Element refNode: textPartNode.select("ref")) {
					refNode.remove();
				}
				for (Element refNode: textPartNode.select("head")) {
					refNode.remove();
				}
				documentText += " " + textPartNode.text();
				documentParts++;
			}
			
			
//			System.out.println("Doc " + documentId + ", " + teiXmlFileEntry.getName());
//			System.out.println(title + ", " + firstAuthor + ", " + year + ", " + doi);
//			System.out.println("Document Parts: " + documentParts);
//			System.out.println("Text: " + documentText);
			
			String titleToInsert = (title + ", " + firstAuthor + ", " + year + ", " + doi).length() < 255
					? (title + ", " + firstAuthor + ", " + year + ", " + doi)
					: StringUtils.abbreviate(title, 255 - (", " + firstAuthor + ", " + year + ", " + doi).length());

			insertMeta.setInt(1, documentId);
			insertMeta.setString(2, titleToInsert); // Title
			insertMeta.setString(3, teiXmlFileEntry.getName()); // URL

			insertMeta.execute();
			
			insertText.setInt(1, documentId);
			StringReader readerDocumentText = new StringReader(documentText);
			insertText.setCharacterStream(2, readerDocumentText);

			insertText.execute();

			documentId++;

		}
		
	}

	
	
	private static void insertDocumentsByCharacterStream(Connection con, File[] textFiles, String corpusName)
			throws IOException, SQLException {
		Integer documentId = 1;

		String sqlInsertMetaTemplate = readFileFromClasspath("insert-corpus-meta-table.sql");
		String sqlInsertTextTemplate = readFileFromClasspath("insert-corpus-text-table.sql");

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("corpus", corpusName);

		String sqlInsertMeta = StringSubstitutor.replace(sqlInsertMetaTemplate, parameters);
		String sqlInsertText = StringSubstitutor.replace(sqlInsertTextTemplate, parameters);

		PreparedStatement insertMeta = con.prepareStatement(sqlInsertMeta);
		PreparedStatement insertText = con.prepareStatement(sqlInsertText);

		for (final File fileEntry : textFiles) {
			String fileName = fileEntry.getName();

			insertMeta.setInt(1, documentId);
			insertMeta.setString(2, fileName); // Title
			insertMeta.setString(3, fileName); // URL

			insertMeta.execute();

			insertText.setInt(1, documentId);
			BufferedReader readerTextFile = new BufferedReader(new FileReader(fileEntry));
			insertText.setCharacterStream(2, readerTextFile);

			insertText.execute();

			documentId++;
		}

	}

}
