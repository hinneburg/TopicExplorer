package cc.topicexplorer.commoncrawl;

import java.io.File;
import org.apache.commons.io.FileUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class HelperUtils {

    /**
     * Load the contents of a file as List of strings.
     * 
     * @param path
     *            the path to the file
     * @return a list of all the lines in the file
     * @throws IOException
     *             if an error occured while reading the file
     *             TODO this could return an empty list on error
     */
    public static List<String> loadFileAsArray(String path) throws IOException {
        String contents = FileUtils.readFileToString(new File(path));
        String[] lines = contents.split("\n");
        return Arrays.asList(lines);
    }

    /**
     * Join a string iterable into a string.
     * 
     * @param i
     *            the iterable which to join
     * @param separator
     *            a string separating each string in i
     * @return all strings in i joined by separator
     */
    public static String join(Iterable<String> i, String separator) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iter = i.iterator();
        if (iter.hasNext()) {
            builder.append(iter.next());
            while (iter.hasNext()) {
                builder.append(separator);
                builder.append(iter.next());
            }
        }

        return builder.toString();
    }
}
