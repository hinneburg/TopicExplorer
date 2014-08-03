package cc.topicexplorer.commoncrawl;

import cc.topicexplorer.commoncrawl.JapaneseBlogIdentifier;
import cc.topicexplorer.commoncrawl.InvalidBlogMetadataException;

public class JapaneseBlog extends Blog {
    public JapaneseBlog(String url, String metadataString, String domainFilePath)
        throws InvalidBlogMetadataException {

        this.id = new JapaneseBlogIdentifier(domainFilePath);
        
        this.init(url, metadataString);
    }
}
