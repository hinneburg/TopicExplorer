package cc.topicexplorer.commoncrawl;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HelperUtils {
    private static final Logger LOG = Logger.getLogger(HelperUtils.class);

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
    public static List<String> loadFileAsArray(String pathString,
                                               Configuration config)
        throws IOException {
        LOG.info("Trying to read " + pathString);
        Path path = new Path(pathString);
        FileSystem fs = path.getFileSystem(config);
        FSDataInputStream stream = fs.open(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        return lines;
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
