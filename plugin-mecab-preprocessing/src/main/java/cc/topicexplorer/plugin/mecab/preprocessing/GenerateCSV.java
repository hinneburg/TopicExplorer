package cc.topicexplorer.plugin.mecab.preprocessing;

import java.io.BufferedReader;
import java.io.FileOutputStream;

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.csvreader.CsvWriter;
import com.google.common.collect.Sets;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableCommand;

public class GenerateCSV  extends TableCommand{
	private static final Logger logger = Logger.getLogger(GenerateCSV.class);

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("Mallet");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public void setTableName() {
		this.tableName = "DOCUMENT_TERM";
	}

	@Override
	public void tableExecute(Context context) {
		String outputFile = properties.getProperty("InCSVFile");
		
		JSONParser parser = new JSONParser();
		 
		
		Object obj;
		try {
			obj = parser.parse(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/wordlist.json"))));
		
		
			JSONArray wordList = (JSONArray) obj;
			JSONObject word = (JSONObject) wordList.get(0);
			
	
			String condition = "((ALL_TERMS.COUNT > " + word.get("lowerBorder") + " AND "
					+ "ALL_TERMS.COUNT < " + word.get("upperBorder") + " AND "
					+ "ALL_TERMS.POS=" + word.get("id");
			JSONArray stopWordList = (JSONArray) word.get("stopWords");
			if(stopWordList.size() > 0) {
				condition += " AND ALL_TERMS.TERM NOT IN ('" + stopWordList.get(0) + "'";
				for(int j = 1 ; j < stopWordList.size(); j++) {
					condition += ",'" + stopWordList.get(j) + "'";
				}
				condition += ")";
			}
			condition += ")";
			for(int i = 1 ; i < wordList.size(); i++) {
				word = (JSONObject) wordList.get(i);
				condition += " OR (ALL_TERMS.COUNT > " + word.get("lowerBorder") + " AND "
						+ "ALL_TERMS.COUNT < " + word.get("upperBorder") + " AND "
						+ "ALL_TERMS.POS=" + word.get("id");
				
				stopWordList = (JSONArray) word.get("stopWords");
				if(stopWordList.size() > 0) {
					condition += " AND ALL_TERMS.TERM NOT IN ('" + stopWordList.get(0) + "'";
					for(int j = 1 ; j < stopWordList.size(); j++) {
						condition += ",'" + stopWordList.get(j) + "'";
					}
					condition += ")";
				}
				condition += ") ";
			}
			condition += ") ";
			
			CsvWriter csvOutput = new CsvWriter(new FileOutputStream(outputFile), ';', Charset.forName("UTF-8"));
			csvOutput.setTextQualifier('"');
			csvOutput.setForceQualifier(true);
			//write header
			csvOutput.write("DOCUMENT_ID");
			csvOutput.write("POSITION_OF_TOKEN_IN_DOCUMENT");
			csvOutput.write("TERM");
			csvOutput.write("TOKEN");
			csvOutput.write("WORDTYPE$WORDTYPE");
			csvOutput.endRecord();
				
			ResultSet wordSelectRS = database.executeQuery("SELECT " + this.tableName + ".DOCUMENT_ID, POSITION_OF_TOKEN_IN_DOCUMENT, " 
					+ this.tableName + ".TERM," + this.tableName + ".TOKEN, POS_TYPE.DESCRIPTION AS WORDTYPE$WORDTYPE "
					+ "FROM " + this.tableName + ", ALL_TERMS, POS_TYPE "
					+ "WHERE ALL_TERMS.POS=POS_TYPE.POS AND " + this.tableName + ".TERM=ALL_TERMS.TERM AND " + this.tableName + ".WORDTYPE_CLASS=ALL_TERMS.POS AND "
					+ condition
					+ "ORDER BY DOCUMENT_ID, POSITION_OF_TOKEN_IN_DOCUMENT");
			while(wordSelectRS.next()) {
				csvOutput.write(wordSelectRS.getString("DOCUMENT_ID"));
				csvOutput.write(wordSelectRS.getString("POSITION_OF_TOKEN_IN_DOCUMENT"));
				csvOutput.write(wordSelectRS.getString("TERM"));
				csvOutput.write(wordSelectRS.getString("TOKEN"));
				csvOutput.write(wordSelectRS.getString("WORDTYPE$WORDTYPE"));
				csvOutput.endRecord();
			}
			
			csvOutput.close();
		} catch(Exception e) {
			logger.error("Mallets input CSV could not be filled properly.");
			throw new RuntimeException(e);
		}
	}

}
