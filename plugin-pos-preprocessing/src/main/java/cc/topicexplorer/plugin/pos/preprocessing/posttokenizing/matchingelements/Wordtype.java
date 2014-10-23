package cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.matchingelements;

import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.tokens.Token;


public class Wordtype extends MatchingElement
{
	public Wordtype(String[] content) {
		super(content);
	}

	@Override
	public boolean tokenMatchSuccessfull(Token tokenFromContext)
	{
		if(this.content.length!=1)
			return false;
		
		if(this.content[0] == tokenFromContext.wordType)
			return true;
		return false;
	}

}
