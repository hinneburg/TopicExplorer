package cc.topicexplorer.commoncrawl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.conf.Configuration;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.fest.assertions.Assertions.assertThat;

public class JapaneseBlogTest {
    String metadaString;
    JapaneseBlog blog;
    
    @SuppressWarnings("rawtypes")
    @Before
    public void initialize() throws FileNotFoundException, InvalidBlogMetadataException {
        String testFeedPath = this.getClass().getResource("/testFeed.json").getPath();
        
        // read metadataString from testFeed.json
        metadaString = new Scanner(new File(testFeedPath)).useDelimiter("\\Z").next();
        String path = this.getClass().getResource("/blogproviders.txt").getPath();
        
        Configuration mockedConfig = mock(Configuration.class);
        when(mockedConfig.get(BlogIdentifier.domainFileKey)).thenReturn(path);
        
        Context mockedContext = mock(Context.class);
        when(mockedContext.getConfiguration()).thenReturn(mockedConfig);
        
        blog = new JapaneseBlog("http://imikowa.hatenablog.com/", metadaString, mockedContext);
    }
    
    @Test
    public void testGetTitle() {
        assertThat(blog.getTitle()).isEqualTo("title1");
    }
    
    @Test
    public void testGetNumberOfPosts() {
        assertThat(blog.getNumberOfPosts()).isEqualTo(2);
    }
    
    @Test
    public void testGetLink() {
        assertThat(blog.getLink()).isEqualTo("http://testdomain.com");
    }
}
