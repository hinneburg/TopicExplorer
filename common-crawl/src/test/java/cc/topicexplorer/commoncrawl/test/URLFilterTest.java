package cc.topicexplorer.commoncrawl.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import cc.topicexplorer.commoncrawl.HelperUtils;
import cc.topicexplorer.commoncrawl.URLFilter;

public class URLFilterTest {
    private static final String VALID_URL_FILE_NAME = "/blogproviders.txt";
    private static final String VALID_URL_FILE_PATH = URLFilterTest.class.getResource(VALID_URL_FILE_NAME).getPath();
    private static final String SAMPLE_URL_FILE_NAME = "/SampleURLs.txt";
    private static final String SAMPLE_URL_FILE_PATH = URLFilterTest.class.getResource(SAMPLE_URL_FILE_NAME).getPath();

    @Test
    public void testFilter() throws IOException {
        List<String> validURLs = HelperUtils.loadFileAsArray(VALID_URL_FILE_PATH);
        List<String> sampleURLs = HelperUtils.loadFileAsArray(SAMPLE_URL_FILE_PATH);
        
        URLFilter filter = new URLFilter(validURLs);
        for (String url : sampleURLs) {
            assertTrue(url + " should be valid", filter.filter(url));
        }
    }
}
