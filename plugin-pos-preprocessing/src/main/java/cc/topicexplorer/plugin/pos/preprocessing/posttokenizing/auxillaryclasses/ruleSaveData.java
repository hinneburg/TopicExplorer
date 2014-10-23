package cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.auxillaryclasses;

public class ruleSaveData 
{
	public int priority = 0;
	public int continuationLength = 0;
	public String continuationPOS="";
	
	public ruleSaveData(int priority, int continuationLength, String continuationPOS){
		this.priority = priority;
		this.continuationLength = continuationLength;
		this.continuationPOS = continuationPOS;
	}
	
	public boolean isInferiorTo(Rule rule){
		return rule.priority>priority;
	}
}
