package cc.topicexplorer.plugin.prune.preprocessing.tools;

import java.io.IOException;

import org.junit.Test;

public class PruneAction_TwoPassMainMemoryVocabularyTest {

	@Test
	public void testPrune() {
		Prune_Ram_SortedCsv myPrune = new Prune_Ram_SortedCsv();

		// String inFilePath =
		// this.getClass().getResource("/test_input.csv").getFile();
		String inFilePath = this.getClass().getResource("/db.csv").getFile();
		myPrune.setInFilePath(inFilePath);
		myPrune.setLowerAndUpperBoundPercent(6, 94);
		try {
			myPrune.prune();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
