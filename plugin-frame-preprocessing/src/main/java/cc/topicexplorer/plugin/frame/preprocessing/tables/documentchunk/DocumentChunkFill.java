package cc.topicexplorer.plugin.frame.preprocessing.tables.documentchunk;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

import cc.topicexplorer.commands.TableFillCommand;
import cc.topicexplorer.plugin.frame.preprocessing.implementation.DocumentChunk;
import cc.topicexplorer.plugin.frame.preprocessing.implementation.FrameCommon;

public class DocumentChunkFill extends TableFillCommand {
	private static final Logger logger = Logger
			.getLogger(DocumentChunkFill.class);
	private static final String documentChunksFile ="temp/documentchunks.csv"; 


	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("Frame_DocumentChunkCreate", "Text_DocumentFill");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}

	private final void checkingFrameDelimiterFileConfiguration() {
		if( (Boolean.parseBoolean(properties.getProperty("Frame_frameDelimiter")) == false) ||
				(properties.getProperty("Frame_frameDelimiterFile").length() < 1) 
			  ) {
				logger.error("frameDelimiterFile not set");
				throw new RuntimeException();			
			}
			if (this.getClass().getResource(
					properties.getProperty("Frame_frameDelimiterFile")) != null
			   ) {
				 logger.error("frameDelimiterFile " + properties.getProperty("Frame_frameDelimiterFile") +"not found");
				 throw new RuntimeException();
			}		
	}

	private final static String getDelimiterListLogString(final List<String> delimiterList) {
		String delimiterListLogString = new String("[format (Delimiter,Length,UniCode)]: ");
		
		for (String s:delimiterList) {
			delimiterListLogString += "(" + s + ", " + new Integer(s.length()).toString();
			for (Integer i=0;i<s.length();i++) {
				delimiterListLogString += ", " + s.codePointAt(i);
			}
			delimiterListLogString+=") ";
		}
		return delimiterListLogString;
	}
	
	private final static List<String> getDelimiterListFromXmlConfigFile(final String XmlConfigFileName) throws ConfigurationException {
		final XMLConfiguration delimiterConfig = new XMLConfiguration();
		delimiterConfig.setDelimiterParsingDisabled(true);
		delimiterConfig.load(XmlConfigFileName); 
		return Arrays.asList(delimiterConfig.getStringArray("delimiter"));
	}
	
	@Override
	public void fillTable() {
		checkingFrameDelimiterFileConfiguration();
		try {
			List<String> delimiterList=getDelimiterListFromXmlConfigFile(properties.getProperty("Frame_frameDelimiterFile"));
			logger.info("DelimiterList " + getDelimiterListLogString(delimiterList));
			
			final BufferedWriter chunkWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					documentChunksFile, false), "UTF-8"));
			final ResultSet documentRS = database.executeQuery( DocumentChunk.getSelectDocumentTextStatement() );
			logger.info("Document texts selected.");		
			DocumentChunk.writeDocumentChunks(delimiterList, chunkWriter, documentRS );			
			chunkWriter.close();
			documentRS.close();
			logger.info("Document chunks written into file " + documentChunksFile);
			database.executeUpdateQuery( DocumentChunk.getLoadChunksStatement(documentChunksFile) );
			logger.info("Document chunks from "+documentChunksFile+" loaded into database table.");
			database.executeUpdateQuery( DocumentChunk.getCreateIndexStatement() );
			logger.info("Index on document chunk table created.");

		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			logger.error("Exception while opening delimiter configuration file. Wrong Encoding.");
			throw new RuntimeException(e1);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			logger.error("Exception cannot find delimiter configuration file.");
			throw new RuntimeException(e1);
		} catch (ConfigurationException e1) {
			e1.printStackTrace();
			logger.error("Exception while reading delimiter configuration file.");
			throw new RuntimeException(e1);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("SQL Exception while filling FrameDelimiter table.");
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("IO Exception while filling FrameDelimiter table.");
			throw new RuntimeException(e);
		}

	}
	
	@Override
	public void setTableName() {
		this.tableName = FrameCommon.pluginPrefix + FrameCommon.delimiter
				+ DocumentChunk.tableName;
	}
}
