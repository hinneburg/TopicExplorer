package cc.topicexplorer.plugin.pos.preprocessing.tools;

import cc.topicexplorer.plugin.pos.preprocessing.implementation.postagger.JPOSToken;

public class Token 
{
	public String token = "";
	public String term ="";
	
	
	public String[] content = null;
	
	public Token(String[] content){
		this.content = content;
	}
	
	public Token(JPOSToken token){
		this.content = new String[]{token.documentID, token.posOfTokenInDocument, 
				token.term, token.token, token.posID};
	}
	
	public String getCsvString(){
		if(content.length==0) return "";
		String output = "\"";
		for(int i=0; i<content.length-1; i++){
			output+=content[i]+"\";\"";
		}
		output+=content[content.length-1]+"\"";
		return output;
	}
}
