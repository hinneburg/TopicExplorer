package cc.topicexplorer.commoncrawl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.fs.GlobPattern;

/**
 * A class to filter urls using a list of valid URL schemes
 *
 * @author Florian Luecke
 */
public class URLFilter {
    protected Pattern urlPattern;

    /**
     * Construct a new URLFilter
     *
     * @param urlList
     *            a list of valid URL schemes
     */
    public URLFilter(List<String> urlList) {
        String pattern = "{http,https}://{"
                         + HelperUtils.join(prepareURLs(urlList), ",") + "}";
        this.urlPattern = GlobPattern.compile(pattern);
    }

    /**
     * Test if a URL is valid
     *
     * @param url
     *            the url to test
     * @return true if url is in the list of valid URLs
     */
    public boolean filter(String url) {
        Matcher m = urlPattern.matcher(url);
        return m.matches();
    }

    /**
     * Replace leading * with [^/] to make sure urls only match at the beginning
     * of the line
     *
     * @param urls
     *            a list of urls to prepare
     * @return list of prepared URLs
     */
    protected List<String> prepareURLs(List<String> urls) {
        List<String> preparedURLs = new ArrayList<String>();
        for (String string : urls) {
            if (string.startsWith("*")) {
                preparedURLs.add(string.replaceFirst("\\*", "[^/]"));
            } else {
                preparedURLs.add(string);
            }
        }
        return preparedURLs;
    }
}
