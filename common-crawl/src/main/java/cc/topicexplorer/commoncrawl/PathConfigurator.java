package cc.topicexplorer.commoncrawl;

import static cc.topicexplorer.commoncrawl.HelperUtils.loadFileAsArray;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.io.IOException;
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
    public static void configureInputPaths(Job job, Configuration config)
        throws IOException {
        Path[] paths = readPathsFromConfigFile(config);
        if (paths.length == 0) {
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

    public static Path[] readPathsFromConfigFile(Configuration config) {
        // get the input path
        String pathString = config.get(INPUTPATH_CONFIG_NAME);
        if (pathString != null) {
            // there is an input path
            // use only the input path, ignore contents of path file if there is
            // one
            Path inputPath = new Path(pathString);
            return new Path[] { inputPath };
        }

        String pathFile = config.get(PATHFILE_CONFIG_NAME);
        Path[] paths = null;
        if (pathFile != null) {
            try {
                // read config file and turn lines into paths
                List<String> pathStrings = loadFileAsArray(pathFile, config);
                paths = new Path[pathStrings.size()];
                for (int i = 0; i < paths.length; i++) {
                    paths[i] = new Path(pathStrings.get(i));
                }
            } catch (IOException e) {
                return new Path[0];
            }
        }

        return paths;
    }
}
