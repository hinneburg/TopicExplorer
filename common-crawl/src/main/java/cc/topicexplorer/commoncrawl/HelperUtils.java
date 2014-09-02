package cc.topicexplorer.commoncrawl;

import java.io.File;
import org.apache.commons.io.FileUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class HelperUtils {

    // TODO: write test cases
    public static List<String> loadFileAsArray(String path) throws IOException {
            String contents = FileUtils.readFileToString(new File(path));
            String[] lines = contents.split("\n");
            return Arrays.asList(lines);
    }

    public static String join(Iterable<String> i, String separator) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iter = i.iterator();
        if (iter.hasNext()) {
            builder.append(iter.next());
            while(iter.hasNext()) {
                builder.append(separator);
                builder.append(iter.next());
            }
        }

        return builder.toString();
    }
}

