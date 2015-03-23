package cc.topicexplorer.plugin.mecab.initcorpus.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;
import cc.topicexplorer.plugin.mecab.initcorpus.implementation.postagger.JPOSMeCab;

import com.google.common.collect.Sets;


public class DocumentTermFill extends TableFillCommand {
	private static final Logger logger = Logger.getLogger(DocumentTermFill.class);

	@Override
	public void fillTable() {
		String fileName = "temp/docTerm.sql.csv";
		File fileTemp = new File(fileName);
        if (fileTemp.exists()) {
        	fileTemp.delete();
        }  
		try {
			if(!properties.containsKey("Mecab_LibraryPath")) {
				logger.error("Mecab library path not set. Did you enable mecab plugin in config.properties?");
				throw new RuntimeException("Mecab library path not set.");
			}
			JPOSMeCab jpos = new JPOSMeCab(properties.getProperty("Mecab_LibraryPath").trim(), logger);
			BufferedWriter docTermCSVWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true), "UTF-8"));
		
//			ResultSet textRs = database.executeQuery("SELECT " + properties.getProperty("OrgTableId") + 
//					", " + properties.getProperty("OrgTableTxt") + " FROM " + properties.getProperty("OrgTableName"));
			Statement stmt = database.getConnection().createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(Integer.MIN_VALUE);
			
			ResultSet textRs = stmt.executeQuery("SELECT DOCUMENT_ID, DOCUMENT_TEXT FROM orgTable_text");
			while(textRs.next()) {
				List<String> csvList = jpos.parseString(textRs.getInt("DOCUMENT_ID"), textRs.getString("DOCUMENT_TEXT"), logger);
				
				for (String csvEntry : csvList) {
					docTermCSVWriter.write(csvEntry + "\n");
				}
			}
			docTermCSVWriter.flush();
			docTermCSVWriter.close();

			database.executeUpdateQuery("LOAD DATA LOCAL INFILE '" + fileName + "' IGNORE INTO TABLE "
					+ tableName + " CHARACTER SET utf8 FIELDS TERMINATED BY ',' ENCLOSED BY '\"' (`DOCUMENT_ID`, "
					+ "`POSITION_OF_TOKEN_IN_DOCUMENT`, `TERM`, `TOKEN`, `WORDTYPE_CLASS`, `CONTINUATION`);");
			stmt.close();
		      
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setTableName() {
		this.tableName = "DOCUMENT_TERM";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentTermCreate");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}

}