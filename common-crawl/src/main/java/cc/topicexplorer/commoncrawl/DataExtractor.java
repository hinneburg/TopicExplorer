package cc.topicexplorer.commoncrawl;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.archive.io.ArchiveReader;

public abstract class DataExtractor {
    protected DataExtractor next;

    public DataExtractor() {
        super();
    }

    public DataExtractor(DataExtractor next) {
        this.next = next;
    }

    public void setNext(DataExtractor next) {
        this.next = next;
    }

    public DataExtractor getNext() {
        return this.next;
    }

    protected void callNext(RecordWrapper wrapper, Mapper<Text, ArchiveReader, Text, Text>.Context context) throws IOException, InterruptedException {
        if (this.next != null) {
            this.next.extract(wrapper, context);
        }
    }

    public abstract void extract(RecordWrapper wrapper, Mapper<Text, ArchiveReader, Text, Text>.Context context)
        throws IOException, InterruptedException;

}