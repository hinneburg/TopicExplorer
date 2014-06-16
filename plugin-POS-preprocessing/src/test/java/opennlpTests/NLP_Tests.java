package opennlpTests;

import java.io.IOException;

import org.junit.Test;

import cc.topicexplorer.plugin.pos.preprocessing.opennlp.OpenNlp;
import opennlp.tools.util.InvalidFormatException;

public class NLP_Tests 
{
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
			System.out.println(nlp.getPath());
			nlp.SentenceDetect();
			System.out.println();
		}
		
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
