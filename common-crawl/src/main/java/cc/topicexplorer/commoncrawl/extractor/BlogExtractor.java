package cc.topicexplorer.commoncrawl.extractor;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import org.archive.io.ArchiveReader;

import cc.topicexplorer.commoncrawl.HelperUtils;
import cc.topicexplorer.commoncrawl.RFC822DateFormatter;
import cc.topicexplorer.commoncrawl.RecordWrapper;
import cc.topicexplorer.commoncrawl.StringFilter;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

/**
 * A class for extracting Japanese blogs from ArchiveRecords retrieved from the
 * common crawl web archive.
 * 
 * @author Florian Luecke
 */
public class BlogExtractor extends DataExtractor {
    private static final Logger     LOG                        = Logger.getLogger(BlogExtractor.class);

    public BlogExtractor() {
    }

    /**
     * Extracts extracts blog posts from a RecordWrapper.
     * 
     * @param wrapper
     *            a RecordWrapper in which to search for blog posts.
     * @param context
     *            the context of the current mapper.
     * @throws IOException
     *             if writing to the context fails.
     * @throws InterruptedException
     *             if writing to the context fails.
     * TODO rueckgabe true falls wert extrahiert
     */
    @Override
    public void extract(RecordWrapper wrapper,
                        Mapper<Text, ArchiveReader, Text, Text>.Context context)
        throws IOException, InterruptedException {

        String url = wrapper.getHeader().getUrl();
        String host = HelperUtils.getTopPrivateDomain(url);
        StringReader reader = new StringReader(wrapper.getHTTPBody());
        SyndFeedInput in = new SyndFeedInput();
        try {
            SyndFeed feed = in.build(reader);
            for (SyndEntry entry : feed.getEntries()) {

                String title = entry.getTitle();

                if (StringFilter.containsJapaneseCharacters(title)) {
                    String entryUrl = entry.getLink();
                    String mainAuthor = entry.getAuthor();
                    String dateString = getPublishedDateRFC(entry);
                    String contentString = getContents(entry);
                    String[] values = new String[] { entryUrl, mainAuthor,
                            dateString, contentString };



                    if (contentString.length() != 0) {
                        StringBuilder builder = new StringBuilder();
                        CSVFormat format = CSVFormat.MYSQL.withHeader("entryUrl",
                                                                      "mainAuthor",
                                                                      "dateString",
                                                                      "contentString");
                        CSVPrinter printer = new CSVPrinter(builder, format);
                        printer.printRecord((Object[]) values);

                        context.write(new Text(host),
                                      new Text(builder.toString()));

                        printer.close();
                        return;
                    } else {
                        LOG.debug("Empty post: " + entryUrl);
                    }
                } else {
                    LOG.debug("Invalid title: " + title);
                }
            }
        } catch (IllegalArgumentException e) {
            LOG.error("No valid feed type at " + url);
        } catch (FeedException e) {
            LOG.error("Feed could not be parsed: " + url);
        }

        this.next.callNext(wrapper, context);
    }

    /**
     * Get all contents of a SyndEntry as String.
     * 
     * A SyndEntry may contain more than one SyndContent. This returns the
     * values of all SyndContents joined by newlines (\n)
     * 
     * @param entry
     *            the SyndEntry whose contents should be returned.
     * @return a string containing all contents of entry joined by
     *         newlines.
     * @see SyndContent#getValue()
     */
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

    /**
     * Return the date an entry was published formatted according to <a
     * href="http://www.w3.org/Protocols/rfc822/">RFC822</a>.
     * 
     * @param entry
     *            the SyndEntry whose date to return.
     * @return the requested date.
     * 
     */
    public static String getPublishedDateRFC(SyndEntry entry) {
        Date publishedDate = entry.getPublishedDate();
        return RFC822DateFormatter.getRFCDate(publishedDate);
    }
}
