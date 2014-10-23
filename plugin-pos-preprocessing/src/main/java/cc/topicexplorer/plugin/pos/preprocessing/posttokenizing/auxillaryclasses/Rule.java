package cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.auxillaryclasses;

import java.util.List;

import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.matchingelements.MatchingElement;
import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.tokens.ContentStack;

public class Rule {
	
	List<List<MatchingElement>> allRuleElements;
	List<Gap> allRuleGaps;
	String continuationPOS="";
	int priority; 
	
	int stackCounter = 0;
	

	public Rule(List<List<MatchingElement>> allRuleElements, List<Gap> allRuleGaps, 
			String continuationPOS, int priority) 
	{
		
		this.allRuleElements = allRuleElements;
		this.allRuleGaps = allRuleGaps;
		this.continuationPOS = continuationPOS;
		this.priority = priority;
	}
	
	void resetRule(){
		stackCounter = 0;
	}
	
	public boolean fitsTheContext(ContentStack context) 
	{
		for (int i = 0; i < this.allRuleElements.size(); i++) 
		{
			for (int j = 0; j < this.allRuleElements.get(i).size(); j++)
			{
				if (stackCounter + j > context.content.size() - 1) 
				{
					//there are no more words in the context
					resetRule();
					return false;
				}
				
				
				if(allRuleElements.get(i).get(j).tokenMatchSuccessfull(
						context.content.get(context.content.size()-(stackCounter+1+j)))){
					continue;
				}
				else
				{
					if(i==0)
					{
						//rule is not working, if the first sequence of ruleElements doesn't 
						//match the context
						resetRule();
						return false;
					}
					else
					{
						//every word in this rule element, which was successfully matched
						//(the one with mismatch as well) will be now matched to the 
						//corresponding gap
						for(int k=0; k<j; k++)
						{
							if(allRuleGaps.get(i-1).tokenFitsInGap(
									context.content.get(context.content.size()-(stackCounter+1+k)))){
								stackCounter++;
							}
							else
							{
								resetRule();
								return false;
							}
						}
						//all the words were able to fit into the gap
						//reset the ruleElement
						j=0;
						continue;
					}
				}
			}
			//all words from the current ruleElement were successfully matched to the context
			stackCounter += allRuleElements.get(i).size();
		}
		//all the ruleElements were successfully matched
		return true;
	}
	
	public ruleSaveData saveRule()
	{
		ruleSaveData save = new ruleSaveData(priority, stackCounter, continuationPOS);
		resetRule();
		return save;
	}

}
