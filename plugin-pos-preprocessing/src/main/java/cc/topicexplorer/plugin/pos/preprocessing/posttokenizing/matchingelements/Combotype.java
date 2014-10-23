package cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.matchingelements;

import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.tokens.Token;


public class Combotype extends MatchingElement {

	public Combotype(String[] content) {
		super(content);
	}
	
	@Override
	public boolean tokenMatchSuccessfull(Token tokenFromContext)
	{
		if(this.content.length!=2)
			return false;
		
		if(this.content[0] == tokenFromContext.wordType &&
				this.content[1] == tokenFromContext.token)
			return true;
		return false;
	}
}
