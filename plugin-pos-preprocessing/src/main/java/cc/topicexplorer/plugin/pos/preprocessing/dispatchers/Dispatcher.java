package cc.topicexplorer.plugin.pos.preprocessing.dispatchers;

import java.util.List;

import cc.topicexplorer.plugin.pos.preprocessing.tools.Token;

public class Dispatcher {
	
	public static final int german = 1;
	public static final int english = 2;
	public static final int japaneese = 3;
	
	int language = -1;
	
	public static Dispatcher getDispatcher(int language)
	{
		switch (language)
		{
		case german: 
		{
			return new nlpDispatcher(language);
		}
		case english:
		{
			return new nlpDispatcher(language);
		}
		} 
		return null;
	}
	
	public Dispatcher(int language)
	{
		this.language = language;
	}
	
	public void initialize(String path){}
	public int getSentencesNumber() {return 0;}
	public boolean getNextToken (Token token) {return false;}
	}
