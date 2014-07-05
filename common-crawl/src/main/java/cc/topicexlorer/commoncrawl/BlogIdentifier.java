package cc.topicexlorer.commoncrawl;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.*;

public class BlogIdentifier {
    public static String fileKey = "validdomainfile";

    public HashSet<String> validDomains = new HashSet<String>();

    private final String _patternString = "[\\p{InHiragana}\\p{InKatakana}\u3000-\u303F\uFF5F-\uFF9F]+";
    private final Pattern _pattern = Pattern.compile(_patternString);

    public BlogIdentifier(String domainFile) {
        try {
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
    
    public boolean isBlog(Map<String, Object> metadata) {
        // TODO implement
        return false;
    }
}