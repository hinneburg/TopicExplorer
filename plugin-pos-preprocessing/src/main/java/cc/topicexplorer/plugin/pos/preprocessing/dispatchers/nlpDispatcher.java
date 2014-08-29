package cc.topicexplorer.plugin.pos.preprocessing.dispatchers;

import java.util.List;

import cc.topicexplorer.plugin.pos.preprocessing.opennlp.OpenNlp;

public class nlpDispatcher extends Dispatcher{
	
	public nlpDispatcher(int language) {
		super(language);
		// TODO Auto-generated constructor stub
	}
	OpenNlp nlp = new OpenNlp();
	
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
	@Override
	public List<String[]> tokenizeSentence(int sentenceNumber) 
	{ 
		return nlp.getPosTokens(sentenceNumber);
	}
}
