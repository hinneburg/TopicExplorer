package cc.topicexplorer.commoncrawl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.fs.GlobPattern;

public class URLFilter {
    protected Pattern urlPattern;

    public URLFilter(List<String> urlList) {
        String pattern = "{http,https}://{"
                         + HelperUtils.join(urlList, ",") + "}";
        this.urlPattern = GlobPattern.compile(pattern);
    }

    public boolean filter(String url) {
        Matcher m = urlPattern.matcher(url);
        return m.matches();
    }
}
