package cc.topicexplorer.commoncrawl;

import com.google.gson.JsonObject;

import org.apache.hadoop.mapreduce.Mapper.Context;

public abstract class BlogIdentifier {

    public static final String domainFileKey = "validdomainfile";

    public abstract boolean isFeed(JsonObject metadata);

    public abstract boolean isValidURL(String url);

    // TODO: change metadataString to JsonObject
    @SuppressWarnings("rawtypes")
    public abstract boolean isValidBlog(String url, String metadataString, Context context);

    public BlogIdentifier() {
        super();
    }

}