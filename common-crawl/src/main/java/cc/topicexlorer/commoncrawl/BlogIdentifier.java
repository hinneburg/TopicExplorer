package cc.topicexlorer.commoncrawl;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.hadoop.io.Text;
import org.apache.commons.io.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// TODO move language detection to optional class member
public class BlogIdentifier {
    public static String fileKey = "validdomainfile";

    public HashSet<String> validDomains = new HashSet<String>();

    private final String _patternString = "[\\p{InHiragana}\\p{InKatakana}\u3000-\u303F\uFF5F-\uFF9F]+";
    private final Pattern _pattern = Pattern.compile(_patternString);

    public BlogIdentifier(String domainFile) {
        try {
            // suppress warning:
            // Type safety: The expression of type List needs unchecked conversion to conform to List<String>
            // this is necessary since readLines declares the wrong return type
            @SuppressWarnings("unchecked")
            List<String> lines = FileUtils.readLines(new File(domainFile));
            
            for (String line : lines) {
                if (line.isEmpty() == false) {
                    validDomains.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isValidBlog(String url, String metadataString) {
        boolean isValidURL = isValidURL(url);
        
        boolean isFeed = isFeed(metadataString);
        
        return isValidURL && isFeed;
    }
    
    public boolean isValidURL(String url) {
        // TODO validata url
        return false;
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