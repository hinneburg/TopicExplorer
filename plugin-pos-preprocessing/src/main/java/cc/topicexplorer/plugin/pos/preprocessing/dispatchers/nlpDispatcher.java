package cc.topicexplorer.plugin.pos.preprocessing.dispatchers;

import java.util.List;

import cc.topicexplorer.plugin.pos.preprocessing.opennlp.OpenNlp;
import cc.topicexplorer.plugin.pos.preprocessing.tools.Token;

public class nlpDispatcher extends Dispatcher{
	
	public nlpDispatcher(int language) {
		super(language);
		// TODO Auto-generated constructor stub
	}
	OpenNlp nlp = new OpenNlp();
	
	private List<String[]> currentSentenceTokens;
	private int sentenceNumber = 0;
	private int listCounter = 0;
	
	@Override
	public void initialize(String path)
	{
		nlp.setPath(path);
		nlp.initializePosTagging(language);
		nlp.initializeSentences("sentences", language);
	}

	@Override
	public int getSentencesNumber() 
	{
		return nlp.sentences.length;
	}

	private boolean tokenizeSentence() 
	{ 
		if(getSentencesNumber()<=sentenceNumber){
			return false;
		}
		
		currentSentenceTokens = nlp.getPosTokens(sentenceNumber);
		sentenceNumber++;
		return true;
	}
	
	@Override
	public boolean getNextToken (Token token)
	{
		if(currentSentenceTokens.size()<=listCounter)
		{
			if(!tokenizeSentence()){
				token = null; 
				return false;
			}
			listCounter = 0;
		}
		token = new Token(currentSentenceTokens.get(listCounter));
		listCounter++;
		return true;
	}
	
}
