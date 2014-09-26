package cc.topicexplorer.commoncrawl.test;

import static cc.topicexplorer.commoncrawl.BlogExtractor.getContents;
import static cc.topicexplorer.commoncrawl.BlogExtractor.getPublishedDateRFC;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

public class BlogExtractorTest {
    private static final String RSS_ENCODED_FILE_NAME = "/rss_cenc.rss.xml";
    private static final String RSS_ENCODED_FILE_PATH = BlogExtractorTest.class.getResource(RSS_ENCODED_FILE_NAME).getPath();

    @Test
    public void testGetAllContents_rss() throws FileNotFoundException,
        IllegalArgumentException, IOException, FeedException {
        File file = new File(RSS_ENCODED_FILE_PATH);
        SyndFeedInput in = new SyndFeedInput();
        SyndFeed feed = in.build(file);
        SyndEntry entry = feed.getEntries().get(0);
        String contents = getContents(entry);
        assertEquals("This is a test.", contents);
    }

    @Test
    public void testGetPublishedDateRFC() throws FileNotFoundException,
        IllegalArgumentException, IOException, FeedException {
        File file = new File(RSS_ENCODED_FILE_PATH);
        SyndFeedInput in = new SyndFeedInput();
        SyndFeed feed = in.build(file);
        SyndEntry entry = feed.getEntries().get(0);

        String rfcDate = getPublishedDateRFC(entry);
        assertEquals("Sun, 15 May 05 18:02:08 +0000", rfcDate);
    }
}
