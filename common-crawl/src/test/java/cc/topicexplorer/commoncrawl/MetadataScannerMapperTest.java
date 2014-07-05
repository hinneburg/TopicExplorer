package cc.topicexplorer.commoncrawl;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;

import org.junit.*;
import org.apache.hadoop.mapred.JobConf;

import cc.topicexlorer.commoncrawl.BlogIdentifier;
import cc.topicexlorer.commoncrawl.MetadataScanner.MetadataScannerMapper;

public class MetadataScannerMapperTest {

    /**
     * Tests the configure method of {@link MetadataScannerMapper}.
     * 
     * TODO make filePath dynamic
     */
    @Test
    public void testConfigure() {
        JobConf conf = new JobConf();
        conf.addResource("testconf.xml");
        String filePath = "/Users/florianluecke/Eclipse/workspace/TopicExplorer/common-crawl/src/test/resources/blogproviders.txt";
        assertThat(conf.get(BlogIdentifier.fileKey)).isEqualTo(filePath);
        
        MetadataScannerMapper m = new MetadataScannerMapper();
        m.configure(conf);
        
        System.out.println("Testing valid domain count");
        assertThat(m.identifier.validDomains.size()).isEqualTo(32);
        
        try {
            m.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
