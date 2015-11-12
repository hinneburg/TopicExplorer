package cc.topicexplorer.plugin.frame.procecssing.implementation;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import com.google.common.base.Joiner;

import cc.topicexplorer.plugin.frame.preprocessing.implementation.FrameCommon;
import cc.topicexplorer.plugin.frame.preprocessing.implementation.Frametype;


public class FrametypeTest {
	@Test
	public void test_getFrameTypesRows() {
		String [] firstWordType = {"1","3","5"};
		String [] lastWordType  = {"2","4","6"};
		List<String> actualFrameTypeRows=Frametype.getFrameTypesRows(firstWordType, lastWordType);
		List<String> expectedFrameTypeRows =  Arrays.asList("(1,2)","(3,4)","(5,6)");
		assertEquals(expectedFrameTypeRows, actualFrameTypeRows);
	}
	@Test
	public void test_getInsertFrameTypes() {
		String [] firstWordType = {"1","3","5"};
		String [] lastWordType  = {"2","4","6"};
		Properties prop = new Properties();
		prop.setProperty(FrameCommon.pluginPropertyPrefix+"firstWordType", Joiner.on(',').join(firstWordType));
		prop.setProperty(FrameCommon.pluginPropertyPrefix+"lastWordType", Joiner.on(',').join(lastWordType));
		
		String actualInsertFrameTypes=Frametype.getInsertFrameTypes(prop);
		String expectedInsertFrameTypes =  "INSERT INTO FRAME$FRAMETYPE VALUES (1,2), (3,4), (5,6)";
		assertEquals(expectedInsertFrameTypes, actualInsertFrameTypes);
	}
}
