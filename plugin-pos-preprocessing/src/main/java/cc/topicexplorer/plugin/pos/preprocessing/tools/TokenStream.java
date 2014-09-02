package cc.topicexplorer.plugin.pos.preprocessing.tools;


import cc.topicexplorer.plugin.pos.preprocessing.dispatchers.Dispatcher;

public class TokenStream 
{
	Dispatcher dispatcher;
	
	//sets up all the operations necessary to begin tagging
	public void initialize(String path, int language)
	{
		dispatcher = Dispatcher.getDispatcher(language);
		dispatcher.initialize(path);
	}
		
	public boolean outputStream(Token token){
		return dispatcher.getNextToken(token);
	}
	
}
