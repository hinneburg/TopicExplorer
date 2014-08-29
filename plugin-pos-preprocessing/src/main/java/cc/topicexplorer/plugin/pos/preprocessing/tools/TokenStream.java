package cc.topicexplorer.plugin.pos.preprocessing.tools;

import java.util.List;

import cc.topicexplorer.plugin.pos.preprocessing.dispatchers.Dispatcher;

public class TokenStream 
{
	Dispatcher dispatcher;
	int currentSentence=0; 
	int currentToken=0;
	List<String[]> currentTokens;
	
	//sets up all the operations necessary to begin tagging
	public void initialize(String path, int language)
	{
		dispatcher = Dispatcher.getDispatcher(language);
		dispatcher.initialize(path);
		if(dispatcher.getSentencesNumber()>0)
			currentTokens= dispatcher.tokenizeSentence(0);
		
	}
		
	public boolean outputStream(Token token){
		
		if(analyzeSentence(token)){
			return true;
			}

		currentSentence++;
		
		if(dispatcher.getSentencesNumber()-1<currentSentence){
			token = null;
			return false;
		}
		currentTokens= dispatcher.tokenizeSentence(currentSentence);
		analyzeSentence(token);
		return true;
	}
	
	private boolean analyzeSentence(Token token){
		
		if(currentTokens.size()-1<currentToken){
			currentToken=0;
			return false;
		}
		
		token.content = getNextToken();
		currentToken++;
		return true;
	}
	
	private String[] getNextToken(){
		return currentTokens.get(currentToken);
	}
}
