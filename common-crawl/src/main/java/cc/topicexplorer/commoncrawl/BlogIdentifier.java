package cc.topicexplorer.commoncrawl;

import com.google.gson.JsonObject;

public abstract class BlogIdentifier {

    public static final String domainFileKey = "validdomainfile";

    public abstract boolean isFeed(JsonObject metadata);

    public abstract boolean isValidURL(String url);

    public abstract boolean isValidBlog(String url, String metadataString);

    public BlogIdentifier() {
        super();
    }

}