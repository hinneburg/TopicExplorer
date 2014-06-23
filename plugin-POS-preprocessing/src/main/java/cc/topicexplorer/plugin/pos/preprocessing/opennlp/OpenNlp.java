package cc.topicexplorer.plugin.pos.preprocessing.opennlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cc.topicexplorer.utils.PropertiesUtil;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

public class OpenNlp {
	String path = "";

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void SentenceDetect() throws InvalidFormatException, IOException {

		// always start with a model, a model is learned from training data
		InputStream is = new FileInputStream(path + "en-sent.bin");
		//InputStream is = PropertiesUtil.class.getResourceAsStream("/"
				//+ "en-sent.bin");
		SentenceModel model = new SentenceModel(is);
		SentenceDetectorME sdetector = new SentenceDetectorME(model);
		is.close();

		is = new FileInputStream(path + "sentences");
		//is = PropertiesUtil.class.getResourceAsStream("/" + "sentences");
		int content = 0;
		String paragraph = "";
		while ((content = is.read()) != -1) {
			// System.out.print((char) content);
			paragraph += ((char) content);
		}
		String sentences[] = sdetector.sentDetect(paragraph);
		for (int i = 0; i < sentences.length; i++) {
			System.out.println(sentences[i]);
		}
		is.close();

		for (int i = 0; i < sentences.length; i++) {
			TokenizeSentences(sentences[i]);
		}
	}

	public void TokenizeSentences(String sent) throws InvalidFormatException, IOException {
		InputStream modelIn = new FileInputStream(path+"en-token.bin");
		TokenizerModel model = null;

		try {
			model = new TokenizerModel(modelIn);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}

		Tokenizer tokenizer = new TokenizerME(model);
		String tokens[] = tokenizer.tokenize(sent);
		for (int i = 0; i < tokens.length; i++) {
			System.out.println(tokens[i]);
		}
			PartOfSpeechTagging(tokens);
		
	}

	public List<String[]> PartOfSpeechTagging(String[] sent) throws IOException {
		InputStream modelIn = null;
		POSModel model;
		try {
			modelIn = new FileInputStream(path + "en-pos-maxent.bin");
			//modelIn = PropertiesUtil.class.getResourceAsStream("/"
					//+ "en-pos-maxent.bin");

			model = new POSModel(modelIn);
		} catch (IOException e) {
			// Model loading failed, handle the error
			e.printStackTrace();
			return null;
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}
		POSTaggerME tagger = new POSTaggerME(model);
		List<String[]> result = new ArrayList<String[]>();
		String [] tags = tagger.tag(sent);
		
		
		for (int i = 0; i < tags.length; i++) {
			String[] tag = {"DUMMY:Document_ID", String.valueOf(i), sent[i], tags[i]};
			System.out.println(sent[i] + " on position " + i + " is a "
					+ tags[i]);
			result.add(tag);
		}
		return result;
	}

}
