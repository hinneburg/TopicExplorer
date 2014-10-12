package cc.topicexplorer.commoncrawl;

import static cc.topicexplorer.commoncrawl.HelperUtils.loadFileAsArray;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for configuring input paths of a hadoop job.
 * 
 * @author Florian Luecke
 * 
 */
public class PathConfigurator {
    private static final Logger LOG                   = Logger.getLogger(PathConfigurator.class);
    public static final String  PATHFILE_CONFIG_NAME  = "pathfile";
    public static final String  INPUTPATH_CONFIG_NAME = "inputpath";

    /**
     * Configure the input paths of a mapreduce job.
     * If the job is passed a configuration file on the command line
     * input path settings will be read from it.
     * 
     * @param job
     *            the job to configure
     * @param config
     *            the {@link Configuration} object to read the settings from
     * @throws IOException
     *             if config does not contain input path settings
     * @see Job
     * @see PathConfigurator#PATHFILE_CONFIG_NAME
     * @see PathConfigurator#INPUTPATH_CONFIG_NAME
     */
    public static void configureInputPaths(Job job) throws IOException {
        List<Path> paths = readPathsFromConfigFile(job.getConfiguration());
        if (paths.size() == 0) {
            throw new IOException("Configuration must contain input path settings.");
        }

        for (Path path : paths) {
            try {
                FileInputFormat.addInputPath(job, path);
                LOG.info("Added input path: " + path);
            } catch (IOException e) {
                LOG.error("Input path was not added: " + path, e);
            }
        }
    }

    /**
     * Read the input settings from the file stored in configuration.
     * 
     * @param configuration
     *            a configuration that contains input settings
     * @return a list of input paths
     */
    public static List<Path> readPathsFromConfigFile(Configuration configuration) {
        // get the input path
        String pathString = configuration.get(INPUTPATH_CONFIG_NAME);
        List<Path> paths = new ArrayList<Path>();
        if (pathString != null) {
            // there is an input path
            // use only the input path, ignore contents of path file if it exists
            Path inputPath = new Path(pathString);
            paths.add(inputPath);
            return paths;
        }

        String pathFile = configuration.get(PATHFILE_CONFIG_NAME);
        if (pathFile != null) {
            try {
                // read configuration file and turn lines into paths
                List<String> pathStrings = loadFileAsArray(pathFile, configuration);
                for (String string : pathStrings) {
                    if (!string.equals("")) {
                        paths.add(new Path(string));
                    }
                }
            } catch (IOException e) {
                return paths; // return null;
            }
        }

        return paths;
    }
}
