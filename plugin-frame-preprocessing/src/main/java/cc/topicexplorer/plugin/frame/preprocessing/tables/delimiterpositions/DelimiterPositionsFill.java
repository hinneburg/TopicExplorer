package cc.topicexplorer.plugin.frame.preprocessing.tables.delimiterpositions;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import cc.topicexplorer.commands.TableFillCommand;

import com.google.common.collect.Sets;

/**
 * <b>Needed database tables</b>: {@code TERM}, {@code TERM_TOPIC}, {@code DOCUMENT_TERM_TOPIC}.
 * <p>
 * <b>{@link #fillTable()} method</b> will arrange the content of the table {@code TopTermsDocSameTopic} and search for
 * frames (noun-verb combinations) in that table. Every so found frame will be written to the table {@code FRAMES}. For
 * every topic only the 20 best fitting nouns and 20 best fitting verbs are used.
 * <p>
 * <b>Frames will be identified</b> if the distance between a noun and a succeeding verb is less or equal 150
 * characters. Further both, noun and verb, must be consistent with their {@code TOPIC_ID} and {@code DOCUMENT_ID}.
 * Accepted values in the column {@code $WORDTYPE} are {@code SUBS} and {@code VERB}.
 * <p>
 * Within any frame there will be <b>only one noun and one verb</b>. No second verb to a specific noun and no second
 * noun to a specific verb, unless one occurs a second time in the text corpus.
 */
public final class DelimiterPositionsFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(DelimiterPositionsFill.class);
	
	@Override
	public void setTableName() {
		this.tableName = "FRAME$DELIMITER_POSITIONS";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("Frame_DelimiterPositionsCreate", "Text_DocumentFill");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fillTable() {
		if( Boolean.parseBoolean(properties.getProperty("Frame_frameDelimiter")) == true) {
			if(properties.getProperty("Frame_frameDelimiterFile").length() < 1) {
				logger.error("frameDelimiterFile not set - there will be no inactive frames");
				throw new RuntimeException();
			} else if(this.getClass().getResource(properties.getProperty("Frame_frameDelimiterFile")) != null){
				logger.warn("frameDelimiterFile not found - there will be no inactive frames");
				throw new RuntimeException();
			} else {
				try {
					database.executeUpdateQuery("ALTER TABLE " + this.tableName + " ADD KEY IDX0 (DOCUMENT_ID, POSITION) ");
					
					BufferedWriter frameDelimiterCSVWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("temp/frameDelimiter.sql.csv", true), "UTF-8"));
				
					XMLConfiguration delimiterConfig = new XMLConfiguration(properties.getProperty("Frame_frameDelimiterFile"));
					List<String> delimiterList = delimiterConfig.getList("delimiter");
					ArrayList<String> cleanDelimiterList = new ArrayList<String>();
					for (String s : delimiterList) {
						if (!s.equals("")) {
							cleanDelimiterList.add(s);
						}
					}
					
					ResultSet documentRS = database.executeQuery("SELECT DOCUMENT_ID, TEXT$FULLTEXT FROM DOCUMENT");
					while(documentRS.next()) {
						int documentId = documentRS.getInt("DOCUMENT_ID");
						String text = documentRS.getString("TEXT$FULLTEXT");
						for(String delimiter : cleanDelimiterList) {
							int position = -1;
							while((position = text.indexOf(delimiter, position + 1)) > -1) {
								frameDelimiterCSVWriter.write("\"" + documentId + "\",\"" + position + "\"\n");
							}
						}
					}
					frameDelimiterCSVWriter.flush();
					frameDelimiterCSVWriter.close();

					database.executeUpdateQuery("LOAD DATA LOCAL INFILE 'temp/frameDelimiter.sql.csv' IGNORE INTO TABLE "
							+ this.tableName + " CHARACTER SET utf8 FIELDS TERMINATED BY ',' ENCLOSED BY '\"' (DOCUMENT_ID, POSITION);");
					
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e) {
					logger.error("Exception while creating FrameDelimiter table.");
					throw new RuntimeException(e);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		

	}

}
