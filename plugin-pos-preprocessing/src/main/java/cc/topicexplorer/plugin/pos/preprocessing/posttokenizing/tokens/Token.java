package cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.tokens;

import cc.topicexplorer.plugin.pos.preprocessing.implementation.postagger.JPOSToken;

public class Token 
{
	public String token, term, wordType, continuationPos;
	public int documentId, textPosition, continuation;
	
	public Token(String token,String term, String pos, int documentId){
		this.token = token;
		this.wordType=pos;
		this.term=term; 
		this.documentId=documentId;
	}
	
	public Token(JPOSToken token)
	{
		
	}
	
	public String csvString()
	{
		return "\"" + this.documentId + "\";\"" + this.textPosition + "\";\""
				+ this.term + "\";\"" + this.token + "\";\"" + this.wordType + "\";\"" + this.continuation 
				+ this.continuationPos +"\"";
	}
	
}
