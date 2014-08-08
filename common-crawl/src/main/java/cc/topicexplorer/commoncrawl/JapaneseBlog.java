package cc.topicexplorer.commoncrawl;

import cc.topicexplorer.commoncrawl.JapaneseBlogIdentifier;
import cc.topicexplorer.commoncrawl.InvalidBlogMetadataException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class JapaneseBlog extends Blog {
    @SuppressWarnings("rawtypes")
    public JapaneseBlog(String url, String metadataString, Context context)
        throws InvalidBlogMetadataException {
        Configuration config = context.getConfiguration();
        String domainFilePath = config.get(BlogIdentifier.domainFileKey);

        this.id = new JapaneseBlogIdentifier(domainFilePath);
        
        this.init(url, metadataString, context);
    }
}
