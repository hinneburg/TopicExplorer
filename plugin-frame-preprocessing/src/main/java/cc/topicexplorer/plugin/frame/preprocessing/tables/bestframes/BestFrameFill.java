package cc.topicexplorer.plugin.frame.preprocessing.tables.bestframes;

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
public final class BestFrameFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(BestFrameFill.class);
	
	@Override
	public void setTableName() {
		this.tableName = "FRAME$BEST_FRAMES";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("Frame_BestFrameCreate", "Frame_FrameFill");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet("HierarchicalTopic_TopicFill");
	}

	@Override
	public void fillTable() {
		int bestFrameCount = Integer.parseInt((String) properties.get("TopicBestItemLimit"));
		String frameType;
		List<Integer> topicIds = new ArrayList<Integer>();
		try {
			ResultSet topipcIdsRs = database.executeQuery("SELECT TOPIC_ID FROM TOPIC");
			while(topipcIdsRs.next()){
				topicIds.add(topipcIdsRs.getInt("TOPIC_ID"));
			}
			ResultSet frameTypeRs = database.executeQuery("SELECT DISTINCT FRAME_TYPE FROM FRAME$FRAMES");
			while(frameTypeRs.next()) {
				frameType =  frameTypeRs.getString("FRAME_TYPE");
				for (int i = 0; i < topicIds.size(); i++) {
					database.executeUpdateQueryForUpdate("INSERT INTO " + this.tableName + " SELECT FRAME, TOPIC_ID, COUNT(DISTINCT DOCUMENT_ID) AS FRAME_COUNT, FRAME_TYPE FROM " 
							+ "FRAME$FRAMES WHERE FRAME_TYPE='" + frameType + "' AND TOPIC_ID="	+ i + " AND ACTIVE=1 GROUP BY FRAME ORDER BY FRAME_COUNT DESC LIMIT "
							+ bestFrameCount);
				}
			}
		} catch (SQLException e) {
			logger.error("Exception while creating bestFrames table.");
			throw new RuntimeException(e);
		}

	}

}
