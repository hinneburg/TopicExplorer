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
        JsonObject content = blogMetadada.getAsJsonObject("content");
        JsonArray items = content.getAsJsonArray("items");
        
        return items.size();
    }
    public String getFeedType() {
        return this.contentGetAsString("type");
    }

    public String getTitle() {
        return this.contentGetAsString("title");
    }
    
    public String getLink() {
        return this.contentGetAsString("link");
    }
    
    private String contentGetAsString(String member) {
        JsonObject content = blogMetadada.getAsJsonObject("content");
        return content.get(member).getAsString();
    }
    
    @SuppressWarnings("unused")
    private String getAsString(String member) {
        return blogMetadada.get(member).getAsString();
    }
}
