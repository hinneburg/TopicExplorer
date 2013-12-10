package cc.topicexplorer.plugin.duplicates.preprocessing.tools;

import java.util.Properties;

import org.junit.Test;


import cc.topicexplorer.database.Database;
//import cc.topicexplorer.database.Database;
import cc.topicexplorer.plugin.duplicates.preprocessing.implementation.Duplicates;

public class test_findDuplicates {

	@Test
	public void testDuplicates() throws Exception
	{
//		String inFilePath = this.getClass().getResource("/genpats_part.csv").getFile();
		String inFilePath = this.getClass().getResource("/duplicates_test4.csv").getFile();

		System.out.println("opened csv");
		
		Duplicates duplicates = new Duplicates();
		
		System.out.println("created duplicates object");

		Properties properties = new Properties();
		properties.setProperty("database.DbLocation", "localhost");
		properties.setProperty("database.DbUser", "root");
		properties.setProperty("database.DbPassword", "TopicExplorer");
		properties.setProperty("database.DB", "genpats_part");
		properties.setProperty("malletNumTopics", "1");
		
		Database db = new Database(properties);
		
		duplicates.setCsvFilePath(inFilePath);
		duplicates.setDB(db);
		
		duplicates.setFrameSize(4);
		
		duplicates.findDuplicates();
		
//		duplicates.writeDuplicatesToDB();
		
		System.out.println("the end");
	}
}
