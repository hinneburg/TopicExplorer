package cc.topicexlorer.commoncrawl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.GlobPattern;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// TODO move language detection to optional class member
public class BlogIdentifier {
    public static final String fileKey = "validdomainfile";
    
    public String domainFile = null;
    private GlobPattern _globPattern = null;

    private final String _patternString = "[\\p{InHiragana}\\p{InKatakana}\u3000-\u303F\uFF5F-\uFF9F]+";
    private final Pattern _pattern = Pattern.compile(_patternString);

    public BlogIdentifier(String domainFile) {
        this.domainFile = domainFile;
    }
    
    /**
     * Tests if the web document is a valid blog, i.e.
     * its URL matches and its metadata contains a feed.
     * @param url The URL of the web document.
     * @param metadataString A JSON string containing the document's metadata.
     * @return true, if the document is a valid blog, false otherwise
     *
     * @see BlogIdentifier#isValidURL
     * @see BlogIdentifier#isFeed
     */
    public boolean isValidBlog(String url, String metadataString) {
        boolean isValidURL = isValidURL(url);
        boolean isFeed = isFeed(metadataString);
        
        return isValidURL && isFeed;
    }

    /**
     * Tests if a URL is valid by matching it against urls in domainFile.
     * @param url The url that should be tested.
     * @return true, if url is valid, false otherwise.
     */
    public boolean isValidURL(String url) {
        if (this._globPattern == null) {
            this.initializeGlobPattern();
        }

        return _globPattern.matches(url);
    }

    private void initializeGlobPattern() {
        try {
            List<?> lines = FileUtils.readLines(new File(this.domainFile));
            String globPatternString = StringUtils.join(lines.toArray(), ",");

            _globPattern = new GlobPattern("{" + globPatternString + "}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests if a JSON object contains a feed.
     * @param metadataString The JSON object.
     * @return true, if a feed is found, false otherwise.
     */
    public boolean isFeed(String metadataString) {
        JsonParser parser = new JsonParser();
        JsonObject metadata = parser.parse(metadataString).getAsJsonObject();

        // catch malformed json
        try {
            JsonObject content = metadata.get("content").getAsJsonObject();
            String contentType = content.get("type").getAsString();
            return contentType.contains("feed");
        } catch (Exception e) {
            return false;
        }
    }
}
