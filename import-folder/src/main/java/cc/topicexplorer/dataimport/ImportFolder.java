package cc.topicexplorer.dataimport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.log4j.Logger;


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

		OptionGroup commandOptions = new OptionGroup();
		commandOptions.setRequired(false);

		Option folderOption = new Option("f", "folder", true, "folder with text files that are imported");
		folderOption.setRequired(true);
		commandOptions.addOption(folderOption);
		
		Options commands = new Options();
		commands.addOptionGroup(commandOptions);

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

		logger.info("list of text files created");
		
		insertDocumentsByCharacterStream(con, textFiles, corpusName);
		
		logger.info("all text files imported");

		
	}

	private static void insertDocumentsByCharacterStream(Connection con, File [] textFiles, String corpusName) throws IOException, SQLException {
		Integer documentId = 1;
		
		String sqlInsertMetaTemplate = readFileFromClasspath("insert-corpus-meta-table.sql");
		String sqlInsertTextTemplate = readFileFromClasspath("insert-corpus-text-table.sql");

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("corpus", corpusName);

		String sqlInsertMeta = StringSubstitutor.replace(sqlInsertMetaTemplate, parameters);
		String sqlInsertText = StringSubstitutor.replace(sqlInsertTextTemplate, parameters);
		
		PreparedStatement insertMeta = con.prepareStatement(sqlInsertMeta);
		PreparedStatement insertText = con.prepareStatement(sqlInsertText);
		
		
		for (final File fileEntry : textFiles ) {
			String fileName = fileEntry.getName();
			
			insertMeta.setInt(1, documentId);
			insertMeta.setString(2, fileName); // Title			
			insertMeta.setString(3, fileName ); // URL
			
			insertMeta.execute();
			
			insertText.setInt(1,documentId);
			BufferedReader readerTextFile = new BufferedReader(new FileReader(fileEntry));
			insertText.setCharacterStream(2, readerTextFile);
			
			insertText.execute();
			
			documentId ++;
		}

	}

}
