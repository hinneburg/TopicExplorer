package cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.auxillaryclasses;

import java.util.ArrayList;
import java.util.List;

import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.tokens.ContentStack;
import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.tokens.Token;
import cc.topicexplorer.plugin.pos.preprocessing.posttokenizing.xmlreader.SubdictionaryReader;

public class Dictionary 
{
	public List<Subdictionary> dictionaries;
	public String filePath="";
	
	
	public Dictionary(String filePath)
	{
		this.filePath = filePath;
		
		dictionaries= new ArrayList<Subdictionary>();
		SubdictionaryReader reader = new SubdictionaryReader();
		
		dictionaries.add(new Subdictionary(filePath+"/english", reader));
		dictionaries.add(new Subdictionary(filePath+"/german", reader));
		dictionaries.add(new Subdictionary(filePath+"/japaneese", reader));
	}
	
	public void useDictionary(int language, Token token, ContentStack context)
	{
		dictionaries.get(language).tryToken(token, context);
	}
}
