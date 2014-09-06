package cc.topicexplorer.commoncrawl.test;

import static cc.topicexplorer.commoncrawl.PathConfigurator.configureInputPaths;
import static cc.topicexplorer.commoncrawl.PathConfigurator.readPathsFromConfigFile;
import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.junit.Before;
import org.junit.Test;

public class PathConfiguratorTest {
    private static final String PATHFILE_CONFIG_FILE_NAME  = "/config-pathfile.xml";
    private static final String PATHFILE_CONFIG_FILE_PATH  = PathConfiguratorTest.class.getResource(PATHFILE_CONFIG_FILE_NAME).getPath();
    private static final String INPUTPATH_CONFIG_FILE_NAME = "/config-inputpath.xml";
    private static final String INPUTPATH_CONFIG_FILE_PATH = PathConfiguratorTest.class.getResource(INPUTPATH_CONFIG_FILE_NAME).getPath();

    private Job                 job;

    @Before
    public void setup() throws IOException {
        job = Job.getInstance();
    }

    @Test
    public void testConfigureInputPaths_pathfile() throws IOException {
        addResourceToConfiguration(PATHFILE_CONFIG_FILE_PATH,
                                   this.job.getConfiguration());
        configureInputPaths(this.job);

        Path[] expected = readPathsFromConfigFile(this.job.getConfiguration());
        Path[] actual = FileInputFormat.getInputPaths(this.job);

        assertArrayEquals(expected, actual);
    }

    private static void addResourceToConfiguration(String path,
                                                   Configuration config)
        throws IOException {
        Path configPath = new Path(path);
        FileSystem fs = configPath.getFileSystem(config);
        FSDataInputStream stream = fs.open(configPath);
        config.addResource(stream, configPath.getName());
    }

    @Test
    public void testInputPathOverridesInputPathFile() throws IOException {
        Configuration config = new Configuration();
        addResourceToConfiguration(INPUTPATH_CONFIG_FILE_PATH, config);
        addResourceToConfiguration(PATHFILE_CONFIG_FILE_PATH, config);

        Path[] paths = new Path[] { new Path("/Users/florianluecke/Eclipse/workspace/TopicExplorer/common-crawl/src/test/resources/warc.path") };

        assertArrayEquals(paths, readPathsFromConfigFile(config));
    }

    @Test
    public void testConfigureInputPaths_inputpath() throws IOException {
        addResourceToConfiguration(PATHFILE_CONFIG_FILE_PATH,
                                   this.job.getConfiguration());

        configureInputPaths(this.job);

        Path[] expected = readPathsFromConfigFile(this.job.getConfiguration());
        Path[] actual = FileInputFormat.getInputPaths(this.job);

        assertArrayEquals(expected, actual);
    }
}
