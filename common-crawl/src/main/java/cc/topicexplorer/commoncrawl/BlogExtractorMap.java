package cc.topicexplorer.commoncrawl;

import static cc.topicexplorer.commoncrawl.HelperUtils.loadFileAsArray;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveRecord;

import cc.topicexplorer.commoncrawl.extractor.BlogExtractor;

public class BlogExtractorMap {
    public static final String VALID_URL_FILE_CONFIG_NAME = "validurlfile";
    public static final String WARC_TYPE_HEADER_NAME      = "WARC-Type";

    // Implements the map function for MapReduce.
    public static class BlogExtractorMapper extends
        Mapper<Text, ArchiveReader, Text, Text> {
        private static final Logger LOG = Logger.getLogger(BlogExtractorMapper.class);

        // implement the main "map" function
        @Override
        public void map(Text key, ArchiveReader value, Context context)
            throws IOException, InterruptedException {
            List<String> validULs = getValidURLs(context);
            BlogExtractor extractor = new BlogExtractor();
            URLFilter filter = new URLFilter(validULs);

            for (ArchiveRecord record : value) {
                // wrap the record so it's easier to use
                RecordWrapper wrapper = new RecordWrapper(record);

                if ("response".equals(wrapper.getHeader().getHeaderValue(WARC_TYPE_HEADER_NAME))) {
                    // filter out requests and crawler metadata
                    // (crawler metadata != page metadata)
                    String url = wrapper.getHeader().getUrl();
                    if (filter.filter(url)) {
                        extractor.extract(wrapper, context);
                    }
                }
            }
        }

        protected static List<String> getValidURLs(Context context)
            throws IOException {
            String validURLFile = context.getConfiguration().get(VALID_URL_FILE_CONFIG_NAME);
            if (validURLFile != null) {
                LOG.debug("Found blog provider list in: " + validURLFile);
            } else {
                LOG.warn("No blog provider list found!");
                throw new IOException("No blog provider list found!");
            }

            List<String> urls = loadFileAsArray(validURLFile,
                                                context.getConfiguration());

            return urls;
        }
    }
}
