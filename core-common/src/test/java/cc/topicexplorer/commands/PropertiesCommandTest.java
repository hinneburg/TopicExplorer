package cc.topicexplorer.commands;

import static org.fest.assertions.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import cc.commandmanager.core.CommunicationContext;

public class PropertiesCommandTest {

	/*
	 * mock context
	 */
	/*
	 * lass ein Mal das Command ausführen und speichere die dann enthaltenen
	 * properties in "Rohform" im Code dieses Tests
	 */
	/*
	 * Alle Änderungen müssen dann wieder diese Properties erstellen
	 */

	PropertiesCommand objectUnderTest = new PropertiesCommand();

	@Test
	public void testExecute() throws IOException {
		Properties targetProperties = new Properties();
		targetProperties
				.load(new ByteArrayInputStream(
						"plugin_hierarchicaltopic=true\nOrgTableId=id\nBitDepth=32\ndatabase.NumberOfRetries=3\nplugin_wordtype=true\nDocBrowserLimit=20\nmalletNumTopics=10\nOrgTableTxt=txt\ndatabase.DbUser=root\nplugins=text,hierarchicaltopic,colortopic,wordtype,fulltext\ndatabase.DbLocation=localhost:3306\ndatabase.DbPassword=TopicExplorer\nplugin_text=true\nFrontendViews=slider,topic,text\nRAM=1g\nFulltext_OrgTableFulltext=txt\nplugin_fulltext=true\nplugin_colortopic=true\ndatabase.DB=japantest\nInCSVFile=/home/user/japantest/db.csv\nOrgTableName=db"
								.getBytes()));

		CommunicationContext communicationContext = new CommunicationContext();
		objectUnderTest.execute(communicationContext);

		Properties actualProperties = (Properties) communicationContext.get(PropertiesCommand.PROPERTIES_CONTEXT_KEY);
		assertThat(actualProperties).isEqualTo(targetProperties);
	}
}
