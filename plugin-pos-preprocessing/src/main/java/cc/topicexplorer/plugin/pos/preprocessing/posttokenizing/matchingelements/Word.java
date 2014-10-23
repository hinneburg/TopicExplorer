package cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.matchingelements;

import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.tokens.Token;


public class Word extends MatchingElement
{
	public Word(String[] content) {
		super(content);
	}

	@Override
	public boolean tokenMatchSuccessfull(Token tokenFromContext)
	{
		if(this.content.length!=1)
			return false;
		
		if(this.content[0] == tokenFromContext.token)
			return true;
		return false;
	}
}
