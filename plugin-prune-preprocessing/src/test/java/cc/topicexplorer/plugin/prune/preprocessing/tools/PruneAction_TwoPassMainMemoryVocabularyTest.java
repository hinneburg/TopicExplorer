package cc.topicexplorer.plugin.prune.preprocessing.tools;

import java.io.IOException;

import org.junit.*;

public class PruneAction_TwoPassMainMemoryVocabularyTest {

	@Test
	public void testPrune() {
		PruneAction_TwoPassMainMemoryVocabulary myPrune = new PruneAction_TwoPassMainMemoryVocabulary();
		
		String inFilePath = this.getClass().getResource("/test_input.csv").getFile();
		myPrune.setInFilePath(inFilePath);
		myPrune.setLowerAndUpperBoundPercent(20,80);
		try {
			myPrune.prune();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
