package cc.topicexplorer.commoncrawl;

// Java classes
import java.io.IOException;
import java.net.URI;

// Apache Project classes
import org.apache.log4j.Logger;

// Hadoop classes
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.IdentityReducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


/**
 * A metadata scanner for the CommonCrawl archive. Based on Chris Stephens' <chris@commoncrawl.org> ExampleMetadataDomainPageCount.java
 * @author Florian Luecke
 */
public class MetadataScanner extends Configured implements Tool {

    private static final Logger LOG = Logger.getLogger(MetadataScanner.class);

    /**
     * Implements the map function for MapReduce.
     */ 
    public static class MetadataScannerMapper extends MapReduceBase implements Mapper<Text, Text, Text, Text> {
        public BlogIdentifier identifier = null;

        // implement the main "map" function
        public void map(Text key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
            if (identifier.isValidBlog(key.toString(), value.toString())){
                output.collect(key, value);
            }
        }
        
        @Override
        public void configure(JobConf job) {
            this.identifier = new BlogIdentifier(job.get(BlogIdentifier.fileKey));
        }
    }


    /**
     * Implmentation of Tool.run() method, which builds and runs the Hadoop job.
     *
     * @param  args command line parameters, less common Hadoop job parameters stripped
     *              out and interpreted by the Tool class.  
     * @return      0 if the Hadoop job completes successfully, 1 if not. 
     */
    @Override
    public int run(String[] args) throws Exception {

        String outputPath = null;
        String configFile = null;

        // Read the command line arguments.
        if (args.length <  1) {
            throw new IllegalArgumentException("Example JAR must be passed an output path.");
        }

        outputPath = args[0];

        if (args.length >= 2) {
            configFile = args[1];
        }

        // Read in any additional config parameters.
        if (configFile != null) {
            LOG.info("adding config parameters from '"+ configFile + "'");
            this.getConf().addResource(configFile);
        }


        // Creates a new job configuration for this Hadoop job.
        JobConf job = new JobConf(this.getConf());

        String inputPath = "s3n://aws-publicdatasets/common-crawl/parse-output/segment/1341690166822/metadata-*";
        inputPath = job.get("inputpath", inputPath);

        job.setJarByClass(MetadataScanner.class);

        // Scan the provided input path for ARC files.
        LOG.info("setting input path to '"+ inputPath + "'");
        FileInputFormat.addInputPath(job, new Path(inputPath));

        // Delete the output path directory if it already exists.
        LOG.info("clearing the output path at '" + outputPath + "'");

        FileSystem fs = FileSystem.get(new URI(outputPath), job);

        if (fs.exists(new Path(outputPath))) {
            fs.delete(new Path(outputPath), true);
        }

        // Set the path where final output 'part' files will be saved.
        LOG.info("setting output path to '" + outputPath + "'");
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        FileOutputFormat.setCompressOutput(job, false);

        // Set which InputFormat class to use. 
        job.setInputFormat(SequenceFileInputFormat.class);

        // Set which OutputFormat class to use.
        job.setOutputFormat(TextOutputFormat.class);

        // Set the output data types.
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // Set which Mapper and Reducer classes to use.
        job.setMapperClass(MetadataScanner.MetadataScannerMapper.class);
        job.setReducerClass(IdentityReducer.class);

        if (JobClient.runJob(job).isSuccessful()) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Main entry point that uses the {@link ToolRunner} class to run the example
     * Hadoop job.
     */
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new MetadataScanner(), args);
        System.exit(res);
    }
}

