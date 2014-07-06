package cc.topicexplorer.commoncrawl;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;

import org.apache.hadoop.mapred.JobConf;
import org.junit.Test;

import cc.topicexlorer.commoncrawl.BlogIdentifier;
import cc.topicexlorer.commoncrawl.MetadataScanner.MetadataScannerMapper;

public class MetadataScannerMapperTest {

    // TODO make filePath dynamic
    @Test
    public void testConfigure() {
        JobConf conf = new JobConf();
        conf.addResource("testconf.xml");
        String filePath = "/Users/florianluecke/Eclipse/workspace/TopicExplorer/common-crawl/src/test/resources/blogproviders.txt";
        assertThat(conf.get(BlogIdentifier.fileKey)).isEqualTo(filePath);
        
        MetadataScannerMapper m = new MetadataScannerMapper();
        m.configure(conf);
        
        System.out.println("Testing file path");
        assertThat(m.identifier.domainFile).isEqualTo(filePath);
        
        try {
            m.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
