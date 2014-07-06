package cc.topicexplorer.commoncrawl;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.*;

import cc.topicexlorer.commoncrawl.BlogIdentifier;

public class BlogIdentifierTest {
    // TODO make path dynamic
    private final String path = "/Users/florianluecke/Eclipse/workspace/TopicExplorer/common-crawl/src/test/resources/blogproviders.txt";
    private BlogIdentifier id = new BlogIdentifier(path);

    @Test
    public void testIsFeed() {
        System.out.println("Testing " + "{\"content\":{\"type\":\"atom-feed\"}}");
        boolean isFeed = id.isFeed("{\"content\":{\"type\":\"atom-feed\"}}");
        assertThat(isFeed).isEqualTo(true);
        
        System.out.println("Testing " + "{\"content\":{\"type\":\"rss-feed\"}}");
        isFeed = id.isFeed("{\"content\":{\"type\":\"rss-feed\"}}");
        assertThat(isFeed).isEqualTo(true);
        
        System.out.println("Testing " + "{\"content\":{\"type\":\"html-doc\"}}");
        isFeed = id.isFeed("{\"content\":{\"type\":\"html-doc\"}}");
        assertThat(isFeed).isEqualTo(false);
        
        System.out.println("Testing " + "{}");
        isFeed = id.isFeed("{}");
        assertThat(isFeed).isEqualTo(false);
    }
    
    @Test
    public void testIsValidURL() {
        System.out.println();
        System.out.println("Testing isValidURL");
        String blogURL = "http://minkara.carview.co.jp/userid/601087/blog/";
        System.out.println("Testing " + blogURL);
        assertThat(id.isValidURL(blogURL)).isEqualTo(true);
        
        blogURL = "http://imikowa.hatenablog.com/";
        System.out.println("Testing " + blogURL);
        assertThat(id.isValidURL(blogURL)).isEqualTo(true);
        
        blogURL = "http://nazlife.hatenablog.com/entry/2014/07/05/";
        System.out.println("Testing " + blogURL);
        assertThat(id.isValidURL(blogURL)).isEqualTo(true);
        
        blogURL = "http://hatenablog.com";
        System.out.println("Testing " + blogURL);
        assertThat(id.isValidURL(blogURL)).isEqualTo(false);
    }
}
