package opennlpTests;

import java.io.IOException;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.annotations.VisibleForTesting;

import cc.commandmanager.core.Context;
import cc.topicexplorer.database.Database;
import cc.topicexplorer.plugin.pos.preprocessing.opennlp.OpenNlp;
import cc.topicexplorer.plugin.pos.preprocessing.tables.PosCreate;
import cc.topicexplorer.utils.PropertiesUtil;
import opennlp.tools.util.InvalidFormatException;

public class NLP_Tests 
{
	@VisibleForTesting
	static final String PROPERTIES_KEY = "properties";
	private static final String NO_PREFIX = "";
	private static final String DATABASE_PREFIX = "database.";
	/*
}
		@Test
		public void testSentences() {
			OpenNlp nlp = new OpenNlp();
			try {
				nlp.SentenceDetect();
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
*/

		@Test
		public void TestNlpSentences () throws InvalidFormatException, IOException
		{
			
			System.out.println("Testing Sentence Detector");
			OpenNlp nlp = new OpenNlp();
			
			nlp.setPath("/home/slayer/workspace/TopicExplorer/"
					+ "plugin-POS-preprocessing/src/main/resources/opennlpResources/");
			//System.out.println(nlp.getPath());
			nlp.getOutputCSV();
			//System.out.println();
			/*
			Properties properties = PropertiesUtil.loadMandatoryProperties("database", DATABASE_PREFIX);
			//properties = PropertiesUtil.updateMandatoryProperties(properties, "database", DATABASE_PREFIX);
			
			Database database = new Database(properties, false);
			Context context = new Context();
			context.bind("database", database);
			
			PosCreate posC= new PosCreate();
			
			posC.execute(context);*/
			
		}
		/*
		@Test
		public void TestNlpTags () throws InvalidFormatException, IOException
		{
			System.out.println("Testing Part Of Speech Tags");
			
			OpenNlp nlp = new OpenNlp();
			nlp.setPath("/home/slayer/workspace/TopicExplorer/"
			+ "plugin-POS-preprocessing/src/main/resources/opennlpResources/");
			nlp.PartOfSpeechTagging(new String[]{"Most", "large", "cities", "in", "the", "US", "had",
		                "morning", "and", "afternoon", "newspapers", "."});
			System.out.println();
		}

		@Test
		public void TestNlpTokens () throws InvalidFormatException, IOException
		{
			System.out.println("Testing Tokenizer");
			
			OpenNlp nlp = new OpenNlp();
			nlp.setPath("/home/slayer/workspace/TopicExplorer/"
			+ "plugin-POS-preprocessing/src/main/resources/opennlpResources/");
			nlp.TokenizeSentences("Most large cities in the US had morning and afternoon newspapers.");
			System.out.println();
		}
/*
			// String inFilePath =
			// this.getClass().getResource("/test_input.csv").getFile();
			String inFilePath = this.getClass().getResource("/db.csv").getFile();
			myPrune.setInFilePath(inFilePath);
			myPrune.setLowerAndUpperBoundPercent(6, 94);
			try {
				myPrune.prune();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();*/
			
		

}
