package cc.topicexplorer.plugin.frame.preprocessing.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

public final class Frametype {
	public static final String tableName = "FRAMETYPE";

	public static final String getCreateTableStatement() {
		// @formatter:off
		return 
		"CREATE TABLE " + 
		   FrameCommon.pluginPrefix + FrameCommon.delimiter + tableName + " " +
		   "(" + 
		     "POS_START INTEGER UNSIGNED," + 
		     "POS_END INTEGER UNSIGNED" + 
		   ") ENGINE INNODB";
	   // @formatter:on
	}

    public static final List<String>  getFrameTypesRows(final String[] firstWordtype,final String[] lastWordtype) {
    	Preconditions.checkArgument(firstWordtype.length==lastWordtype.length,"Lists firstWordtype and lastWordtype have not same length.");
    	Preconditions.checkArgument(firstWordtype.length>0,"Lists firstWordtype and lastWordtype have zero length.");
    	
    	final List<String> frameTypeRows = new ArrayList<String>();

    	for(Integer i=0;i<firstWordtype.length;i++)
    		frameTypeRows.add("("+firstWordtype[i]+","+lastWordtype[i]+")");
    	return frameTypeRows;
    }

    public  static final String getInsertFrameTypes(final Properties prop) {
    	Preconditions.checkArgument(
    			prop.containsKey("Frame_firstWordType") &&
    			prop.containsKey("Frame_lastWordType"),
    			"Property firstWordType or lastWordType not specified.");
    	// @formatter:off
    	return 
    	"INSERT INTO "+
    	FrameCommon.pluginPrefix + FrameCommon.delimiter + tableName + " " +
    	   "VALUES " + Joiner.on(", ").join(
    			   getFrameTypesRows(
    						prop.getProperty(FrameCommon.pluginPropertyPrefix+"firstWordType").split(","),
    						prop.getProperty(FrameCommon.pluginPropertyPrefix+"lastWordType").split(",") )
    			   )
    	;
 	   // @formatter:on
    }
}
