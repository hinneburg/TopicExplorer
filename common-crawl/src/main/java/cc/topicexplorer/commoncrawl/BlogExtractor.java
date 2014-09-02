package cc.topicexplorer.commoncrawl;

import static cc.topicexplorer.commoncrawl.HelperUtils.loadFileAsArray;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.hadoop.fs.GlobPattern;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import org.archive.io.ArchiveReader;

import cc.topicexplorer.commoncrawl.statistics.StatisticsBuilder;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

public class BlogExtractor {
    private static final Logger     LOG                             = Logger.getLogger(BlogExtractor.class);
    private static final String     RFC_822_DATE_FORMAT_STRING          = "E', 'd' 'MMM' 'yy' 'HH':'mm':'ss' 'Z";
    private static final String     VALID_URL_FILE_CONFIG_NAME      = "validurlfile";

    private List<StatisticsBuilder> statisticsBuilders              = new ArrayList<StatisticsBuilder>();

    private Pattern                 urlPattern;
    private Pattern                 titlePattern                    = Pattern.compile("[\\p{InHiragana}]+");

    public BlogExtractor(Mapper<Text, ArchiveReader, Text, ArrayWritable>.Context context) throws IOException {
        // load valid URLs from config
        String validURLFile = context.getConfiguration().get(VALID_URL_FILE_CONFIG_NAME);
        List<String> lines = loadFileAsArray(validURLFile);

        // create a globbing pattern from the configuration
        urlPattern = GlobPattern.compile("http?://{"
                                         + HelperUtils.join(lines, ",") + "}");
    }

    public void extract(RecordWrapper wrapper,
                        Mapper<Text, ArchiveReader, Text, ArrayWritable>.Context context)
        throws IOException, InterruptedException {
        String url = wrapper.getHeader().getUrl();
        if (this.urlPattern.matcher(url).matches()) {
            // the page url is valid
            StringReader reader = new StringReader(wrapper.getHTTPBody());
            SyndFeedInput in = new SyndFeedInput();
            try {
                SyndFeed feed = in.build(reader);
                for (SyndEntry entry : feed.getEntries()) {
                    // get all interesting data about the post
                    String title = entry.getTitle();
                    if (this.titlePattern.matcher(title).matches()) {
                        String entryUrl = entry.getLink();
                        String mainAuthor = entry.getAuthor();
                        String dateString = getPublishedDateRFC(entry);
                        String contentString = getContents(entry);
                        ArrayWritable values = new ArrayWritable(new String[] {
                                entryUrl, mainAuthor, dateString, contentString });

                        if (contentString.length() != 0) {
                            context.write(new Text(url), values);
                        }
                    }
                }
                buildStatistics(feed, context);
            } catch (IllegalArgumentException e) {
                LOG.info("No valid feed: " + url);
            } catch (FeedException e) {
                LOG.error("Feed could not be parsed: " + url);
            }
        }
    }

    public static String getContents(SyndEntry entry) {
        // get ATOM content
        List<SyndContent> contents = entry.getContents();
        List<String> contentValues = getValues(contents);
        return HelperUtils.join(contentValues, "\n");
    }

    protected static List<String> getValues(List<SyndContent> contents) {
        List<String> valueList = new ArrayList<String>();
        for (SyndContent syndContent : contents) {
            valueList.add(syndContent.getValue());
        }
        return valueList;
    }

    public static String getPublishedDateRFC(SyndEntry entry) {
        Date publishedDate = entry.getPublishedDate();
        return getRFCDate(publishedDate);
    }

    protected static String getRFCDate(Date date) {
        // format the date according to RFC 3339
        String dateString = new SimpleDateFormat(RFC_822_DATE_FORMAT_STRING, Locale.ENGLISH).format(date);
        return dateString;
    }

    public void buildStatistics(SyndFeed feed,
                                Mapper<Text, ArchiveReader, Text, ArrayWritable>.Context context) {
        for (StatisticsBuilder statisticsBuilder : this.statisticsBuilders) {
            statisticsBuilder.buildStatistics(feed, context);
        }
    }

    public void addStatisticsBuilder(StatisticsBuilder sb) {
        statisticsBuilders.add(sb);
    }

    public void removeStatisticsBuilder(StatisticsBuilder sb) {
        statisticsBuilders.remove(sb);
    }
}
