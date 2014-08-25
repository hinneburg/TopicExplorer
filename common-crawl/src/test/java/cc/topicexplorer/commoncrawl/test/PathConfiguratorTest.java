package cc.topicexplorer.commoncrawl.test;

import static cc.topicexplorer.commoncrawl.PathConfigurator.configureInputPaths;
import static cc.topicexplorer.commoncrawl.PathConfigurator.readPathsFromConfigFile;
import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.junit.Before;
import org.junit.Test;

public class PathConfiguratorTest {
    private static final String PATHFILE_CONFIG_FILE_NAME = "/config-pathfile.xml";
    private static final String PATHFILE_CONFIG_FILE_PATH = PathConfiguratorTest.class.getResource(PATHFILE_CONFIG_FILE_NAME).getPath();
    private static final String INPUTPATH_CONFIG_FILE_NAME = "/config-inputpath.xml";
    private static final String INPUTPATH_CONFIG_FILE_PATH = PathConfiguratorTest.class.getResource(INPUTPATH_CONFIG_FILE_NAME).getPath();
    
    private Configuration config;
    private Job job;

    @Before
    public void setup() throws IOException {
        job = new Job();
        config = new Configuration();
    }

    @Test
    public void testConfigureInputPaths_pathfile() throws IOException {
        config.addResource(new Path(PATHFILE_CONFIG_FILE_PATH));

        configureInputPaths(job, config);

        Path[] expected = readPathsFromConfigFile(config);
        Path[] actual = FileInputFormat.getInputPaths(job);

        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void testReadPathsFromConfigFile() {
        config.addResource(new Path(PATHFILE_CONFIG_FILE_PATH));

        Path[] paths = new Path[]{new Path("file:/aws-publicdatasets/CC-MAIN-20140707234000-00000-ip-10-180-212-248.ec2.internal.warc.gz"),
                                  new Path("file:/aws-publicdatasets/CC-MAIN-20140707234000-00001-ip-10-180-212-248.ec2.internal.warc.gz")};

        assertArrayEquals(paths, readPathsFromConfigFile(config));
    }
    
    @Test
    public void testConfigureInputPaths_inputpath() throws IOException {
        config.addResource(new Path(INPUTPATH_CONFIG_FILE_PATH));
        
        configureInputPaths(job, config);

        Path[] expected = readPathsFromConfigFile(config);
        Path[] actual = FileInputFormat.getInputPaths(job);

        assertArrayEquals(expected, actual);
    }
}
