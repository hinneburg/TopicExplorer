package cc.topicexplorer.plugin.frame.preprocessing.tables.frames;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

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
public final class FrameFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(FrameFill.class);
	private List<String> frameTypes = new ArrayList<String>();

	@Override
	public void setTableName() {
		this.tableName = "FRAME$FRAMES";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("TermFill", "TermTopicFill", "DocumentTermTopicFill", "FrameCreate");
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
	public void fillTable() {
		String[] startWordTypes =  ((String) properties.get("Frame_firstWordType")).split(",");
		String[] endWordTypes =  ((String) properties.get("Frame_lastWordType")).split(",");
		String[] startWordTypeLimits = ((String) properties.get("Frame_firstWordTypeLimit")).split(",");
		String[] endWordTypeLimits = ((String) properties.get("Frame_firstWordTypeLimit")).split(",");
		String[] maxFrameSizes = ((String) properties.get("Frame_maxFrameSize")).split(",");
		if( startWordTypes.length == endWordTypes.length && 
			startWordTypes.length == startWordTypeLimits.length && 
			startWordTypes.length == endWordTypeLimits.length && 
			startWordTypes.length == maxFrameSizes.length) {
		
			fillWordtypeColumnOfTableTerm();
			
			try {
				database.executeUpdateQuery("DROP TABLE IF EXISTS FRAME$BEST_FRAMES");
				database.executeUpdateQuery("CREATE TABLE FRAME$BEST_FRAMES (FRAME VARCHAR(255), TOPIC_ID INT,  FRAME_COUNT INT, FRAME_TYPE VARCHAR(255))");
			} catch (SQLException e) {
				logger.error("Exception while creating bestFrames table.");
				throw new RuntimeException(e);
			}
			
			
			for(int i = 0; i < startWordTypes.length; i++) {
				createAndFillTableTopTerms(startWordTypes[i], startWordTypeLimits[i], endWordTypes[i], endWordTypeLimits[i]);
				createAndFillTableTopTermsDocSameTopic();
				try {
					String frameType = startWordTypes[i] + "_" + startWordTypeLimits[i] + "_" + endWordTypes[i] + "_" 
							+ endWordTypeLimits[i] + "_" + maxFrameSizes[i];
					fillTableFrames(Integer.parseInt(maxFrameSizes[i]), startWordTypes[i], frameType);
					frameTypes.add(frameType);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dropTemporaryTables();
				
			}
			try {
				database.executeUpdateQuery("ALTER TABLE " + this.tableName + " ADD KEY IDX0 (DOCUMENT_ID,TOPIC_ID,START_POSITION,END_POSITION) ");
			} catch (SQLException e) {
				logger.error("Exception while creating frames indezes.");
				throw new RuntimeException(e);
			}
			createAndFillTableFrameDelimiterPos();
			bestFrames();
			
			logger.info(String.format("Table %s is filled.", this.tableName));
		} else {
			logger.error("Sizes of frame property fields do not match");
			throw new RuntimeException();
		}
	}

	private void fillWordtypeColumnOfTableTerm() {
		try {
			database.executeUpdateQueryForUpdate("alter table TERM add column WORDTYPE VARCHAR(255) COLLATE utf8_bin");
			database.executeUpdateQueryForUpdate("update TERM, DOCUMENT_TERM_TOPIC "
					+ "SET TERM.WORDTYPE=DOCUMENT_TERM_TOPIC.WORDTYPE$WORDTYPE WHERE DOCUMENT_TERM_TOPIC.TERM=TERM.TERM_NAME");
		} catch (SQLException e) {
			logger.error("Table TERM could not be altered.");
			throw new RuntimeException(e);
		}
	}
	
	private void createAndFillTableFrameDelimiterPos() {
		if( Boolean.parseBoolean(properties.getProperty("Frame_frameDelimiter")) == true) {
			if(properties.getProperty("Frame_frameDelimiterFile").length() < 1) {
				logger.error("frameDelimiterFile not set - there will be no inactive frames");
				throw new RuntimeException();
			} else if(this.getClass().getResource(properties.getProperty("Frame_frameDelimiterPosCSV")) != null){
				logger.warn("frameDelimiterCSV not found - there will be no inactive frames");
				throw new RuntimeException();
			} else {
				try {
					database.executeUpdateQuery("drop table if exists FRAME_DELIMITER_POS");
					database.executeUpdateQuery("create table FRAME_DELIMITER_POS (DOCUMENT_ID INT, POSITION INT) ENGINE = MEMORY");  
					database.executeUpdateQuery("LOAD DATA LOCAL INFILE '" + properties.getProperty("Frame_frameDelimiterPosCSV") 
							+ "' INTO TABLE FRAME_DELIMITER_POS CHARACTER SET utf8 FIELDS TERMINATED BY ';' ENCLOSED BY '\"' IGNORE 1 LINES");
					database.executeUpdateQuery("ALTER TABLE FRAME_DELIMITER_POS ADD KEY IDX0 (DOCUMENT_ID, POSITION) ");
					database.executeUpdateQuery("UPDATE " + this.tableName + ",FRAME_DELIMITER_POS SET ACTIVE=0 WHERE " 
							+ "FRAME_DELIMITER_POS.DOCUMENT_ID=FRAME$FRAMES.DOCUMENT_ID AND START_POSITION<POSITION AND END_POSITION>POSITION");
				} catch (SQLException e) {
					logger.error("Exception while deactivating frames.");
					throw new RuntimeException(e);
				} 

			}
		}
	}

	private void createAndFillTableTopTerms(String startWordType, String startWordTypeLimit, String endWordType, String endWordTypeLimit) {
		try {
			database.executeUpdateQuery("create table TopTerms ENGINE = MEMORY DEFAULT CHARSET=utf8 COLLATE=utf8_bin "
					+ "select TERM_NAME, TOPIC_ID, PR_TERM_GIVEN_TOPIC from TERM_TOPIC join TERM using (TERM_ID) "
					+ "where TOPIC_ID=0 AND WORDTYPE='" + startWordType
					+ "' order by PR_TERM_GIVEN_TOPIC desc limit "
					+ startWordTypeLimit + ";");
			database.executeUpdateQuery("alter table TopTerms add index (TOPIC_ID,TERM_NAME)");
			database.executeUpdateQuery("alter table TopTerms add index (TERM_NAME)");
			int numTopics = Integer.parseInt((String) properties.get("malletNumTopics"));

			// best 20 nouns of best 20 topics
			for (int i = 1; i < numTopics; i++) {
				database.executeUpdateQueryForUpdate("insert into TopTerms "
					+ "select TERM_NAME, TOPIC_ID, PR_TERM_GIVEN_TOPIC from TERM_TOPIC join TERM "
					+ "using (TERM_ID) where TOPIC_ID=" + i
					+ " AND WORDTYPE='" + startWordType
					+ "' order by PR_TERM_GIVEN_TOPIC desc limit "
					+ startWordTypeLimit + ";");
			}

			// best 20 verbs of best 20 topics
			for (int i = 0; i < numTopics; i++) {
				database.executeUpdateQueryForUpdate("insert into TopTerms "
					+ "select TERM_NAME, TOPIC_ID, PR_TERM_GIVEN_TOPIC from TERM_TOPIC join TERM "
					+ "using (TERM_ID) where TOPIC_ID=" + i
					+ " AND WORDTYPE='" + endWordType
					+ "' order by PR_TERM_GIVEN_TOPIC desc limit "
					+ endWordTypeLimit + ";");
			}
		} catch (SQLException e) {
			logger.error("Exception while handling temporary table TopTerms.");
			throw new RuntimeException(e);
		}
	}

	private void createAndFillTableTopTermsDocSameTopic() {
		try {
			database.executeUpdateQuery("create table TOP_TERMS_DOC_SAME_TOPIC ENGINE = MEMORY DEFAULT CHARSET=utf8 COLLATE=utf8_bin "
					+ "select DOCUMENT_TERM_TOPIC.DOCUMENT_ID, TopTerms.TOPIC_ID, "
					+ "DOCUMENT_TERM_TOPIC.POSITION_OF_TOKEN_IN_DOCUMENT, DOCUMENT_TERM_TOPIC.TERM, DOCUMENT_TERM_TOPIC.WORDTYPE$WORDTYPE "
					+ "from DOCUMENT_TERM_TOPIC join TopTerms on (DOCUMENT_TERM_TOPIC.TERM=TopTerms.TERM_NAME and DOCUMENT_TERM_TOPIC.TOPIC_ID=TopTerms.TOPIC_ID) "
					+ "order by DOCUMENT_TERM_TOPIC.DOCUMENT_ID asc, TopTerms.TOPIC_ID asc,	DOCUMENT_TERM_TOPIC.POSITION_OF_TOKEN_IN_DOCUMENT asc");
		} catch (SQLException e) {
			logger.error("Exception while handling temporary table TOP_TERMS_DOC_SAME_TOPIC.");
			throw new RuntimeException(e);
		}
	}

	private void fillTableFrames(int maxFrameSize, String startWordType, String frameType) throws IOException {
		try {
			File fileTemp = new File("temp/frames.sql.csv");
	        if (fileTemp.exists()) {
	        	fileTemp.delete();
	        }  
			BufferedWriter frameCSVWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("temp/frames.sql.csv", true), "UTF-8"));
			ResultSet topicRs = database.executeQuery("SELECT * FROM TOPIC");
			while (topicRs.next()) {
				
			}
			ResultSet topTerms = database
					.executeQuery("SELECT * FROM TOP_TERMS_DOC_SAME_TOPIC order by DOCUMENT_ID asc, TOPIC_ID asc, POSITION_OF_TOKEN_IN_DOCUMENT asc");

			int documentId = 0;
			int position = 0;
			int topicId = 0;
			String term = null, wordType = null;
			int endPos;
			
			while (topTerms.next()) {
				if (documentId != topTerms.getInt("DOCUMENT_ID")// || topicId != topTerms.getInt("TOPIC_ID")
						|| topTerms.getInt("POSITION_OF_TOKEN_IN_DOCUMENT") - position > maxFrameSize
						|| topTerms.getString("WORDTYPE$WORDTYPE").equals(startWordType)) {
					documentId = topTerms.getInt("DOCUMENT_ID");
					topicId = topTerms.getInt("TOPIC_ID");
					position = topTerms.getInt("POSITION_OF_TOKEN_IN_DOCUMENT");
					term = topTerms.getString("TERM");
					wordType = topTerms.getString("WORDTYPE$WORDTYPE");
				} else {
					if (wordType.equals(startWordType)) {
						
						endPos = topTerms.getInt("POSITION_OF_TOKEN_IN_DOCUMENT") + topTerms.getString("TERM").length();
						frameCSVWriter.write("\"" + documentId + "\",\"" + topicId + "\",\"" + term + "," 
						+ topTerms.getString("TERM") + "\",\"" + position + "\",\""  + endPos + "\",\""  + frameType + "\"\n");
					}
					position = 0 - maxFrameSize;
				}
			}
			frameCSVWriter.flush();
			frameCSVWriter.close();

			database.executeUpdateQuery("LOAD DATA LOCAL INFILE 'temp/frames.sql.csv' IGNORE INTO TABLE "
					+ tableName + " CHARACTER SET utf8 FIELDS TERMINATED BY ',' ENCLOSED BY '\"' (DOCUMENT_ID, TOPIC_ID, FRAME, START_POSITION, END_POSITION, FRAME_TYPE);");
		} catch (SQLException e) {
			logger.error("Table could not be filled properly.");
			throw new RuntimeException(e);
		} 
	}

	private void bestFrames() {
		int numTopics = Integer.parseInt((String) properties.get("malletNumTopics"));
		int bestFrameCount = Integer.parseInt((String) properties.get("TopicBestItemLimit"));
		try {
			for(String frameType: frameTypes) {
				for (int i = 0; i < numTopics; i++) {
					database.executeUpdateQueryForUpdate("INSERT INTO FRAME$BEST_FRAMES SELECT FRAME, TOPIC_ID, COUNT(DISTINCT DOCUMENT_ID) AS FRAME_COUNT, FRAME_TYPE FROM " 
							+ this.tableName + " WHERE FRAME_TYPE='" + frameType + "' AND TOPIC_ID="	+ i + " AND ACTIVE=1 GROUP BY FRAME ORDER BY FRAME_COUNT DESC LIMIT "
							+ bestFrameCount);
				}
			}
		} catch (SQLException e) {
			logger.error("Exception while creating bestFrames table.");
			throw new RuntimeException(e);
		}
	}

	private void dropTemporaryTables() {
		try {
			database.dropTable("TopTerms");
			database.dropTable("TOP_TERMS_DOC_SAME_TOPIC");
		} catch (SQLException e) {
			logger.warn("At least one temporarely created table or column could not be dropped.", e);
		}
	}

}
