package cc.topicexplorer.plugin.japanesepos.preprocessing.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;


import cc.topicexplorer.commands.TableFillCommand;
import cc.topicexplorer.plugin.japanesepos.preprocessing.implementation.postagger.JPOSMeCab;

import com.google.common.collect.Sets;

public class DocumentTermFill extends TableFillCommand {

	private JPOSMeCab jpos;
			
	@Override
	public void fillTable() {
		String fileName = "temp/docTerm.sql.csv";
		File fileTemp = new File(fileName);
        if (fileTemp.exists()) {
        	fileTemp.delete();
        }  
		try {
			BufferedWriter docTermCSVWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true), "UTF-8"));
		
			ResultSet textRs = database.executeQuery("SELECT " + properties.getProperty("OrgTableId") + 
					", " + properties.getProperty("OrgTableTxt") + " FROM " + properties.getProperty("OrgTableName"));
			while(textRs.next()) {
				jpos = new JPOSMeCab(); // fixme: is it nessecary?
				
				List<String> csvList = jpos.parseString(textRs.getInt(properties.getProperty("OrgTableId")), textRs.getString(properties.getProperty("OrgTableTxt")));
				
				for (String csvEntry : csvList) {
					docTermCSVWriter.write(csvEntry + "\n");
				}
			}
			docTermCSVWriter.flush();
			docTermCSVWriter.close();

			database.executeUpdateQuery("LOAD DATA LOCAL INFILE '" + fileName + "' IGNORE INTO TABLE "
					+ tableName + " CHARACTER SET utf8 FIELDS TERMINATED BY ',' ENCLOSED BY '\"' (`DOCUMENT_ID`, "
					+ "`POSITION_OF_TOKEN_IN_DOCUMENT`, `TERM`, `TOKEN`, `WORDTYPE_CLASS`, `CONTINUATION`);");
		      
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (UnsupportedEncodingException e1) {
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
