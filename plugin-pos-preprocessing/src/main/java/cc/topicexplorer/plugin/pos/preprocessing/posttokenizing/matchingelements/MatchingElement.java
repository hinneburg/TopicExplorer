package cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.matchingelements;

import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.tokens.Token;


public class MatchingElement 
{
	public MatchingElement(String[] content){
		this.content = content;
	}
	public String[] content;
	
	public boolean tokenMatchSuccessfull(Token tokenFromContext){
		return false;
	}
}
