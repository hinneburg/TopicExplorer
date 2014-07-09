package cc.topicexplorer.commoncrawl;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;

import org.apache.hadoop.mapred.JobConf;
import org.junit.Test;

import cc.topicexplorer.commoncrawl.MetadataScanner.MetadataScannerMapper;

public class MetadataScannerMapperTest {

    @Test
    public void testConfigure() {
        String confFile = this.getClass().getResource("/testconf.xml").getPath();
        JobConf conf = new JobConf();
        conf.addResource(confFile);

        MetadataScannerMapper m = new MetadataScannerMapper();
        m.configure(conf);
        
        String filePath = conf.get(BlogIdentifier.fileKey);
        assertThat(m.identifier.domainFilePath).isEqualTo(filePath);
        
        try {
            m.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
