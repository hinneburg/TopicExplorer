package cc.topicexplorer.commoncrawl.statistics;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.archive.io.ArchiveReader;

import com.rometools.rome.feed.synd.SyndFeed;

public interface StatisticsBuilder {
    public void buildStatistics(SyndFeed feed,
                                Mapper<Text, ArchiveReader, Text, Text>.Context context);
}
