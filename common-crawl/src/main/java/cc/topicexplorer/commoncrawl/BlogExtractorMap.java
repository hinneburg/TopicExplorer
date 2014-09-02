package cc.topicexplorer.commoncrawl;

import java.io.IOException;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveRecord;

public class BlogExtractorMap {
    private static final Logger LOG = Logger.getLogger(BlogExtractorMap.class);

    // Implements the map function for MapReduce.
    public static class BlogExtractorMapper extends
        Mapper<Text, ArchiveReader, Text, ArrayWritable> {
        // implement the main "map" function
        @Override
        public void map(Text key, ArchiveReader value, Context context)
            throws IOException, InterruptedException {
            BlogExtractor extractor = new BlogExtractor(context);

            for (ArchiveRecord record : value) {
                // wrap the record so it's easier to use
                RecordWrapper wrapper = new RecordWrapper(record);
                LOG.info("Page URL: " + wrapper.getHeader().getUrl());
                extractor.extract(wrapper, context);
            }
        }
    }
}
