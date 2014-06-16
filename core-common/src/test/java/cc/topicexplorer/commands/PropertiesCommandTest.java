package cc.topicexplorer.commands;

import static org.fest.assertions.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Test;

import cc.commandmanager.core.Context;

public class PropertiesCommandTest {

	PropertiesCommand commandUnderTest = new PropertiesCommand();

	@Test
	public void testExecute() throws IOException {
		byte[] targetPropertiesRaw = "plugin_hierarchicaltopic=true\nOrgTableId=id\nBitDepth=32\ndatabase.NumberOfRetries=3\nplugin_wordtype=true\nDocBrowserLimit=20\nmalletNumTopics=10\nOrgTableTxt=txt\ndatabase.DbUser=root\nplugins=text,hierarchicaltopic,colortopic,wordtype,fulltext\ndatabase.DbLocation=localhost:3306\ndatabase.DbPassword=TopicExplorer\nplugin_text=true\nFrontendViews=slider,topic,text\nRAM=1g\nFulltext_OrgTableFulltext=txt\nplugin_fulltext=true\nplugin_colortopic=true\ndatabase.DB=japantest\nInCSVFile=/home/user/japantest/db.csv\nOrgTableName=db"
				.getBytes();
		InputStream propertiesStream = new ByteArrayInputStream(targetPropertiesRaw);
		Properties targetProperties = new Properties();
		targetProperties.load(propertiesStream);

		Context communicationContext = new Context();
		commandUnderTest.execute(communicationContext);

		Properties actualProperties = (Properties) communicationContext.get(PropertiesCommand.PROPERTIES_KEY);
		assertThat(actualProperties).isEqualTo(targetProperties);
	}
}
