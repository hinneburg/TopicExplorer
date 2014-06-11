package cc.topicexplorer.plugin.pos.preprocessing.opennlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;
public class OpenNlp 
{
	String path = "/home/slayer/workspace/TopicExplorer/"
			+ "plugin-POS-preprocessing/src/main/resources/opennlpResources/";
	
	public void SentenceDetect() throws InvalidFormatException, IOException {

		
		// always start with a model, a model is learned from training data
		InputStream is = new FileInputStream(path+"en-sent.bin");
		SentenceModel model = new SentenceModel(is);
		SentenceDetectorME sdetector = new SentenceDetectorME(model);
		is.close();
		
		is = new FileInputStream(path+"sentences");
		int content=0;
		String paragraph="";
		while ((content = is.read()) != -1) 
		{
			//System.out.print((char) content);
			paragraph+=((char) content);
		}
		String sentences[] = sdetector.sentDetect(paragraph);
		for(int i=0; i<sentences.length;i++)
		{
			System.out.println(sentences[i]);
		}
		is.close();
	}
	
	public void PartOfSpeechTagging() throws IOException
	{
		InputStream modelIn = null;
		POSModel model;
		try {
		  modelIn = new FileInputStream(path+"en-pos-maxent.bin");
		  model = new POSModel(modelIn);
		}
		catch (IOException e) {
		  // Model loading failed, handle the error
		  e.printStackTrace();
		  return;
		}
		finally {
		  if (modelIn != null) {
		    try {
		      modelIn.close();
		    }
		    catch (IOException e) {
		    }
		  }
		}
		POSTaggerME tagger = new POSTaggerME(model);
		
		String sent[] = new String[]{"Most", "large", "cities", "in", "the", "US", "had",
                "morning", "and", "afternoon", "newspapers", "."};	
		
		String tags[] = tagger.tag(sent);
		for(int i=0; i<tags.length;i++)
		{
			System.out.println(sent[i]+" is a "+tags[i]);
		}
	}
	
}
