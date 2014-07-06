package cc.topicexplorer.commoncrawl;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import cc.topicexlorer.commoncrawl.BlogIdentifier;

public class BlogIdentifierTest {
    private final String path = this.getClass().getResource("/blogproviders.txt").getPath();
    private BlogIdentifier id = new BlogIdentifier(path);

    @Test
    public void testIsFeed_atomFeed() {
        String jsonString = "{\"content\":{\"type\":\"atom-feed\"}}";
        assertThat(id.isFeed(jsonString)).isEqualTo(true);
    }
    
    @Test
    public void testIsFeed_rssFeed() {
        String jsonString = "{\"content\":{\"type\":\"rss-feed\"}}";
        assertThat(id.isFeed(jsonString)).isEqualTo(true);
    }
    
    @Test
    public void testIsFeed_htmlDoc() {
        String jsonString = "{\"content\":{\"type\":\"html-doc\"}}";
        assertThat(id.isFeed(jsonString)).isEqualTo(false);
    }
    
    @Test
    public void testIsFeed_invalidObject() {
        String jsonString = "{}";
        assertThat(id.isFeed(jsonString)).isEqualTo(false);
    }
    
    @Test
    public void testIsValidURL_minkara() {
        String blogURL = "http://minkara.carview.co.jp/userid/601087/blog/";
        assertThat(id.isValidURL(blogURL)).isEqualTo(true);
    }
    
    @Test
    public void testIsValidURL_hatenablog() {
        String blogURL = "http://imikowa.hatenablog.com/";
        assertThat(id.isValidURL(blogURL)).isEqualTo(true);
    }
    
    @Test
    public void testIsValidURL_hatenaExtended() {
        String blogURL = "http://nazlife.hatenablog.com/entry/2014/07/05/";
        assertThat(id.isValidURL(blogURL)).isEqualTo(true);
    }

    @Test
    public void testIsValidURL_invalidURL() {
        String blogURL = "http://hatenablog.com";
        assertThat(id.isValidURL(blogURL)).isEqualTo(false);
    }
}
