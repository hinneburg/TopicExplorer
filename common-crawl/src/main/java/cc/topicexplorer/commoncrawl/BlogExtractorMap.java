package cc.topicexplorer.commoncrawl;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import org.archive.io.ArchiveReader;

public class BlogExtractorMap {
    private static final Logger LOG = Logger.getLogger(BlogExtractorMap.class);

    /*
     * Implements the map function for MapReduce.
     */
    public static class BlogExtractorMapper extends
        Mapper<Text, ArchiveReader, Text, Text> {
        // implement the main "map" function
        @Override
        public void map(Text key, ArchiveReader value, Context context)
            throws IOException, InterruptedException {
        }
    }
}
