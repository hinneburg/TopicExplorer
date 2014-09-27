package cc.topicexplorer.commoncrawl.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

import cc.topicexplorer.commoncrawl.HelperUtils;
import cc.topicexplorer.commoncrawl.StringFilter;

public class StringFilterTest {
    private static final String JAPANESE_STRING_FILE_NAME = "/JapaneseStrings.txt";
    private static final String JAPANESE_STRING_FILE_PATH = StringFilterTest.class.getResource(JAPANESE_STRING_FILE_NAME).getPath();
    @Test
    public void testContainsJapaneseCharacters() throws IOException {
        List<String> testStrings = HelperUtils.loadFileAsArray(JAPANESE_STRING_FILE_PATH, new Configuration());
        PrintStream stdout = new PrintStream(System.out, true, "UTF-8");
        System.setOut(stdout);
        for (String testString : testStrings) {
            assertTrue("Assert " + testString + " contains japanese characters",
                       StringFilter.containsJapaneseCharacters(testString));
        }
    }
}
