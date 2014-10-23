package cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.auxillaryclasses;

import java.util.ArrayList;
import java.util.List;

import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.tokens.ContentStack;
import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.tokens.Token;
import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.xmlreader.SubdictionaryReader;
import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.matchingelements.MatchingElement;


public class Subdictionary
{
	List<MatchingElement> rulesStartingElements = new ArrayList<MatchingElement>();
	List<List<Rule>> allRulesOfTheLanguage = new ArrayList<List<Rule>>();
	
	public void insertNewMatchingElement(MatchingElement element){
		rulesStartingElements.add(element);
	}
	
	public void insertNewRuleList(List<Rule> rules){
		allRulesOfTheLanguage.add(rules);
	}
	
	public Subdictionary(String filepath, SubdictionaryReader reader)
	{
		//loads Subdictionary contents from a file via 
		//XMLReader
		
		reader.updateConfiguration(filepath, this);
		reader.readSubdictionaryConfig();
	}
	
	public void tryToken(Token token, ContentStack context)
	{
		ruleSaveData saveData = new ruleSaveData(-1,0, token.wordType);
		for(int i=0; i<rulesStartingElements.size(); i++)
		{
			if(rulesStartingElements.get(i).tokenMatchSuccessfull(token)){
				for(int j=0; j<allRulesOfTheLanguage.get(i).size();j++){
					if(allRulesOfTheLanguage.get(i).get(j).fitsTheContext(context)){
						if(saveData.isInferiorTo(allRulesOfTheLanguage.get(i).get(j)))
						{
							saveData = allRulesOfTheLanguage.get(i).get(j).saveRule();
						}
					}
				}
			}
		}
		
		//TODO: update the tokens in context with the saveData
		
	}

}
