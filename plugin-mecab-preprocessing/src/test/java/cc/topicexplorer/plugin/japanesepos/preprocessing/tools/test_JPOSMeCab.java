package cc.topicexplorer.plugin.japanesepos.preprocessing.tools;

import org.junit.Test;

import cc.topicexplorer.plugin.japanesepos.preprocessing.implementation.postagger.JPOSMeCab;

public class test_JPOSMeCab {

	@Test
	public void testDuplicates() throws Exception
	{
		String inFilePath = "";
		
		int i = 1;
		
		if (i==1) inFilePath = this.getClass().getResource("/wiki_a-profile.txt").getFile();
		if (i==2) inFilePath = this.getClass().getResource("/wiki_autobahnpolizei.txt").getFile();
		if (i==3) inFilePath = this.getClass().getResource("/wiki_kawagoe.txt").getFile();
		if (i==4) inFilePath = this.getClass().getResource("/wiki_nintendo.txt").getFile();
		if (i==5) inFilePath = this.getClass().getResource("/wiki_schule.txt").getFile();
		if (i==6) inFilePath = this.getClass().getResource("/wiki_shibata.txt").getFile();
		if (i==7) inFilePath = this.getClass().getResource("/wiki_vancouver.txt").getFile();
		
		
		JPOSMeCab jpos = new JPOSMeCab();

//		jpos.setUseWordClass("MISC", false);
//		jpos.setUseWordClass("SYM", false);
//		jpos.setUseWordClass("ADJ", true);
//		jpos.setUseWordClass("PART", false);
//		jpos.setUseWordClass("AUXV", false);
//		jpos.setUseWordClass("CONJ", false);
//		jpos.setUseWordClass("PREF", false);
//		jpos.setUseWordClass("VERB", true);
//		jpos.setUseWordClass("ADV", true);
//		jpos.setUseWordClass("NOUN", true);

		jpos.parse(inFilePath, "");
		
		
		System.out.println("the end");
	}
}
