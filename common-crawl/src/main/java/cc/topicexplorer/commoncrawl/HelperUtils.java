package cc.topicexplorer.commoncrawl;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import com.google.common.net.InternetDomainName;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HelperUtils {
    private static final Logger LOG = Logger.getLogger(HelperUtils.class);

    /**
     * Load the contents of a file as List of strings.
     * 
     * Defaults to UTF-8 encoding and an empty configuration.
     * 
     * @param pathString
     *            the path to the file
     * @return a list of all the lines in the file
     * @throws IOException
     *             if an error occured while reading the file
     * @see HelperUtils#loadFileAsArray(String, Configuration)
     * @see HelperUtils#loadFileAsArray(String, Configuration, String)
     */
    public static List<String> loadFileAsArray(String pathString)
        throws IOException {
        return loadFileAsArray(pathString, new Configuration(), "UTF-8");
    }

    /**
     * Load the contents of a file as List of strings.
     * 
     * Defaults to UTF-8 encoding.
     * 
     * @param pathString
     *            the path to the file
     * @param config
     *            a Configuration object to use for accessing the file system
     * @return a list of all the lines in the file
     * @throws IOException
     *             if an error occured while reading the file
     * @see HelperUtils#loadFileAsArray(String)
     * @see HelperUtils#loadFileAsArray(String, Configuration, String)
     */
    public static List<String> loadFileAsArray(String pathString,
                                               Configuration config)
        throws IOException {
        return loadFileAsArray(pathString, config, "UTF-8");
    }

    /**
     * Load the contents of a file as List of strings.
     * 
     * @param pathString
     *            the path to the file
     * @param config
     *            a Configuration object to use for accessing the file system
     * @param charset
     *            the name of the charset to use for decoding the file
     * @return a list of all the lines in the file
     * @throws IOException
     *             if an error occured while reading the file
     * @see HelperUtils#loadFileAsArray(String)
     * @see HelperUtils#loadFileAsArray(String, Configuration)
     */
    public static List<String> loadFileAsArray(String pathString,
                                               Configuration config,
                                               String charset)
        throws IOException {
        LOG.info("Trying to read " + pathString);
        Path path = new Path(pathString);
        FileSystem fs = path.getFileSystem(config);
        FSDataInputStream stream = fs.open(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream,
                                                                         charset));
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

    /**
     * Get the top private domain from a url.
     * 
     * @param urlString
     *            the url of which to return the top private domain
     * @return the requested top private domain
     */
    public static String getTopPrivateDomain(String urlString) {
        String host;
        try {
            host = new URL(urlString).getHost();
        } catch (MalformedURLException e) {
            return "";
        }

        // some domains are not recognized as valid
        // use host as top private domain for them
        String topPrivateDomain;
        try {
            topPrivateDomain = InternetDomainName.from(host).topPrivateDomain().toString();
        } catch (IllegalStateException e) {
            return host;
        }

        return topPrivateDomain;
    }
}
