package cc.topicexplorer.commoncrawl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class JapaneseBlogTest {
    String metadaString;
    JapaneseBlog blog;
    
    @Before
    public void initialize() throws FileNotFoundException, InvalidBlogMetadataException {
        String testFeedPath = this.getClass().getResource("/testFeed.json").getPath();
        metadaString = new Scanner(new File(testFeedPath)).useDelimiter("\\Z").next();
        String path = this.getClass().getResource("/blogproviders.txt").getPath();
        blog = new JapaneseBlog("http://imikowa.hatenablog.com/", metadaString, path);
    }
    
    @Test
    public void testGetTitle() {
        assertThat(blog.getTitle()).isEqualTo("title1");
    }
    
    @Test
    public void testGetNumberOfPosts() {
        assertThat(blog.getNumberOfPosts()).isEqualTo(2);
    }
    
}
