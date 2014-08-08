/**
 * 
 */
package cc.topicexplorer.commoncrawl;

import cc.topicexplorer.commoncrawl.BlogIdentifier;
import cc.topicexplorer.commoncrawl.InvalidBlogMetadataException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.hadoop.mapreduce.Mapper.Context;
/**
 * @author florianluecke
 *
 */
public abstract class Blog {
    BlogIdentifier id;
    JsonObject blogMetadada;
    JsonObject content;

    @SuppressWarnings("rawtypes")
    protected void init(String url, String metadataString, Context context) throws InvalidBlogMetadataException {

        if (! this.id.isValidBlog(url, metadataString, context)) {
            throw new InvalidBlogMetadataException();
        } else {
            JsonParser parser = new JsonParser();
            blogMetadada = parser.parse(metadataString).getAsJsonObject();
        }
    }
    
    public int getNumberOfPosts() {
        JsonObject content = this.getContent();
        JsonArray items = content.getAsJsonArray("items");

        return items.size();
    }
    public String getFeedType() {
        return this.getAsString("type");
    }

    public String getTitle() {
        return this.getAsString("title");
    }
    
    public String getLink() {
        JsonObject content = this.getContent();
        return this.getAsString(content.getAsJsonObject("link"), "href");
    }

    /**
     * Get a field by name.
     * @param member The Name of the field.
     * @return
     */
    public String getAsString(String member) {
        return this.getAsString(this.getContent(), member);
    }

    private String getAsString(JsonObject object, String member) {
        return object.get(member).getAsString();
    }

    private JsonObject getContent() {
        if (this.content == null) {
            this.content = this.blogMetadada.getAsJsonObject("content");
        }

        return this.content;
    }

    public abstract String getID();
}
