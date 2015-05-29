package cc.topicexplorer.plugin.frame.preprocessing.tables.frames;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

public class FrameFillTest {

	@Test
	public void testFillTable() throws IOException {
		// TODO cover test cases of public FrameFill methods

		Properties p = new Properties();

		p.load(new ByteArrayInputStream("test=".getBytes()));

		String[] startWordTypes = p.getProperty("test").split(",");
		assertEquals(true, startWordTypes[0].isEmpty());
	}
}
