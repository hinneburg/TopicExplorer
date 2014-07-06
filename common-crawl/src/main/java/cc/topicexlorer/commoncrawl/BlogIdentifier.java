package cc.topicexlorer.commoncrawl;

import java.io.File;
import org.apache.hadoop.fs.GlobPattern;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.io.*;
import org.apache.commons.lang.StringUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// TODO move language detection to optional class member
public class BlogIdentifier {
    public static String fileKey = "validdomainfile";
    
    public String domainFile = null;
    private GlobPattern _globPattern = null;

    private final String _patternString = "[\\p{InHiragana}\\p{InKatakana}\u3000-\u303F\uFF5F-\uFF9F]+";
    private final Pattern _pattern = Pattern.compile(_patternString);

    public BlogIdentifier(String domainFile) {
        this.domainFile = domainFile;
    }
    
    public boolean isValidBlog(String url, String metadataString) {
        boolean isValidURL = isValidURL(url);
        
        boolean isFeed = isFeed(metadataString);
        
        return isValidURL && isFeed;
    }
    
    /**
     * Tests if a URL is valid by matching it against urls in domainFile.
     * @param url The url that should be testet.
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
