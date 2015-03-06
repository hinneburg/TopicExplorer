package cc.topicexplorer.plugin.japanesepos.preprocessing.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import cc.topicexplorer.plugin.mecab.initcorpus.command.PosTypeFill;
import cc.topicexplorer.plugin.mecab.initcorpus.implementation.postagger.JPOSMeCab;

public class Test_JPOSMeCab {
	private static final Logger logger = Logger.getLogger(PosTypeFill.class);

	@Test @Ignore
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
		
		JPOSMeCab jpos = new JPOSMeCab("", logger);

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
		
		String content = "";
		   File file = new File(inFilePath); //for ex foo.txt
		   try {
			   BufferedReader reader = new BufferedReader(
					   new InputStreamReader(new FileInputStream(file), "UTF-8"));
			   String str;
			   
				while ((str = reader.readLine()) != null) {
				   content= content + str;
				}
		       reader.close();
		         System.out.println(content.length());
		       List<String> csvList = jpos.parseString(0, content, logger);
				
		       for (String csvEntry : csvList) {
		    	   System.out.println(csvEntry);
		       }
		   } catch (IOException e) {
		       e.printStackTrace();
		   }

		
		
		
		System.out.println("the end");
	}
}
