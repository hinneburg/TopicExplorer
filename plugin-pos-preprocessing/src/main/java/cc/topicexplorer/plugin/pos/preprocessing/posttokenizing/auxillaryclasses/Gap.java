package cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.auxillaryclasses;

import java.util.ArrayList;
import java.util.List;

import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.matchingelements.Wordtype;
import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.tokens.Token;


public class Gap 
{
	public boolean lastingGap = false;
	List<Wordtype> wordtypesAllowedInGap;
	
	public Gap(boolean lastingGap, List<String> wordtypes)
	{
		this.lastingGap=lastingGap;
		this.wordtypesAllowedInGap= new ArrayList<Wordtype>();
		for(int i=0; i<wordtypes.size(); i++)
			this.wordtypesAllowedInGap.add(new Wordtype(new String[]{wordtypes.get(i)}));
	}
	
	public boolean tokenFitsInGap(Token currentToken){
		for (int i=0; i<this.wordtypesAllowedInGap.size(); i++){
			if(wordtypesAllowedInGap.get(i).tokenMatchSuccessfull(currentToken))
				return true;
		}
		return false;
	}
}
