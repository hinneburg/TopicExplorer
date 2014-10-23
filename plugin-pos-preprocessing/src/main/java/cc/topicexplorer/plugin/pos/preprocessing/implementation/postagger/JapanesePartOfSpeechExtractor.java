package cc.topicexplorer.plugin.pos.preprocessing.implementation.postagger;



public interface JapanesePartOfSpeechExtractor
{
	// TODO DB getter und setter?

//	public void parse( String inputFile, String outputFile );
//	public void parse( ArrayList<String> inputFiles, String outputFile );
	
	public void parseString( String str );
	

}
