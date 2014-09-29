package cc.topicexplorer.commoncrawl;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.archive.io.ArchiveReader;

public abstract class DataExtractor {

    public DataExtractor() {
        super();
    }

    public abstract void extract(RecordWrapper wrapper, Mapper<Text, ArchiveReader, Text, Text>.Context context)
        throws IOException, InterruptedException;

}