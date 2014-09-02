package cc.topicexplorer.plugin.pos.preprocessing.opennlp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvWriter;

import cc.topicexplorer.plugin.pos.preprocessing.dispatchers.Dispatcher;
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
	String _path = "";

	String[] _tableHeader = null;

	int[] sentencesPositions = null;
	public String[] sentences = null;
	POSTaggerME tagger;
	Tokenizer tokenizer;

	String[] sentenceTokens = null;
	List<String[]> posTaggedTokens = null;
	OutputStream csvOutputStream = null;
	CsvWriter writer = null;

	
	  public void setCsvOutputStreamAndOpenWriter(OutputStream csvOutputStream)
	  { this.csvOutputStream = csvOutputStream; this.writer = new
	  CsvWriter(this.csvOutputStream, ',', Charset.defaultCharset()); }
	 

	public String getPath() {
		return _path;
	}

	public void setPath(String path) {
		this._path = path;
	}

	public String[] getHeader() {
		return _tableHeader;
	}

	public void setHeader(String[] tableHeaders) {
		this._tableHeader = tableHeaders;

	}

	// reads a given file completely and returns its content as a String
	private String readFile(String filename) throws IOException {

		InputStream is = new FileInputStream(_path + filename);
		int content = 0;
		String paragraph = "";
		while ((content = is.read()) != -1) {
			paragraph += ((char) content);
		}
		is.close();
		return paragraph;
	}

	// detects sentences from a paragraph and saves them as a String in a local
	// var
	private void SentenceDetect(String paragraph, int language)
			throws InvalidFormatException, IOException {
		
		String path = "";
		if(language== Dispatcher.english)
			path = _path + "en-sent.bin";
		else
			path = _path + "de-sent.bin";
		InputStream is = new FileInputStream(path);

		SentenceModel model = new SentenceModel(is);
		SentenceDetectorME sdetector = new SentenceDetectorME(model);
		is.close();

		sentences = sdetector.sentDetect(paragraph);
	}

	// sets up the class for further operations
	public void initializeSentences(String filename, int language) {
		String paragraph = "";
		try {
			paragraph = readFile(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			SentenceDetect(paragraph, language);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// initializes all the classes needed for token detection
	public void initializePosTagging(int language) {
		switch (language) {
		case Dispatcher.english: {
			initializeEnglishNlp();
			break;
		}
		case Dispatcher.german: {
			initializeGermanNlp();
			break;
		}
		default:
			break;
		}
	}

	void initializeGermanNlp() {
		POSModel modelPos = null;
		InputStream modelIn = null;

		try {
			modelIn = new FileInputStream(_path + "de-pos-maxent.bin");
			// modelIn = PropertiesUtil.class.getResourceAsStream("/"
			// + "en-pos-maxent.bin");

			modelPos = new POSModel(modelIn);
		} catch (IOException e) {
			// Model loading failed, handle the error
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}
		tagger = new POSTaggerME(modelPos);

		try {
			modelIn = new FileInputStream(_path + "de-token.bin");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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

		tokenizer = new TokenizerME(model);
	}

	public int[] getSentencesBeginnings(String paragraph)
	{
		int[] positions = new int[sentences.length];
		int charCounter=0;
		positions[0]=charCounter;
		charCounter++;
		
		for(int i=0; i<sentences.length-2; i++){
			charCounter += sentences[i].length();
			while(paragraph.charAt(charCounter)==' '||paragraph.charAt(charCounter)==Character.LINE_SEPARATOR)
			{
				charCounter++;
			}

			positions[i+1]=charCounter;
			charCounter++;
		}
		return positions;
	}
	
	void initializeEnglishNlp() {
		POSModel modelPos = null;
		InputStream modelIn = null;

		try {
			modelIn = new FileInputStream(_path + "en-pos-maxent.bin");
			// modelIn = PropertiesUtil.class.getResourceAsStream("/"
			// + "en-pos-maxent.bin");

			modelPos = new POSModel(modelIn);
		} catch (IOException e) {
			// Model loading failed, handle the error
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}
		tagger = new POSTaggerME(modelPos);

		try {
			modelIn = new FileInputStream(_path + "en-token.bin");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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

		tokenizer = new TokenizerME(model);
	}

	// gets the POS tokens from the sentence specified by its number
	public List<String[]> getPosTokens(int sentenceId) {

		String[] sentence;
		sentence = TokenizeSentence(sentences[sentenceId]);

		return (PartOfSpeechTagging(sentence));
	}

	// splits the given sentence into words
	private String[] TokenizeSentence(String sentence) {

		String tokens[] = tokenizer.tokenize(sentence);

		return tokens;
	}

	// returns the POS tokens of the sentence split into words
	private List<String[]> PartOfSpeechTagging(String[] sentence) {

		List<String[]> result = new ArrayList<String[]>();
		String[] tags = tagger.tag(sentence);

		for (int i = 0; i < tags.length; i++) {
			String[] tag = { "Document_ID", String.valueOf(i), sentence[i],
					tags[i] };
			result.add(tag);
		}
		return result;
	}

	/*
	 * public void tokenizeAndWriteTokensToCSV(InputStream is)
	 * 
	 * throws InvalidFormatException, IOException {
	 * 
	 * this.setHeader(new String[] { "DOCUMENT_ID", "POSITION", "TOKEN",
	 * "PART_OF_SPEECH" });
	 * 
	 * this.deleteOutputFile();
	 * 
	 * this.writeHeader(); sentences = SentenceDetect(new
	 * StringBuilder().toString());
	 * 
	 * for (int i = 0; i < sentences.length; i++) { this.sentenceTokens =
	 * TokenizeSentence(sentences[i]);
	 * 
	 * this.posTaggedTokens = this.PartOfSpeechTagging(sentenceTokens);
	 * 
	 * writeRecord(posTaggedTokens); }
	 * 
	 * this.writer.close(); }
	 */

	/*
	 * private void writeHeader() throws IOException { CsvWriter writer = new
	 * CsvWriter(_path + "/nlpOutput.csv");
	 * writer.writeRecord(this._tableHeader); writer.close(); }
	 */

	/*
	 * private void writeRecord(List<String[]> line) throws IOException {
	 * CsvWriter writer = new CsvWriter(this.csvOutputStream, ',',
	 * Charset.defaultCharset());
	 * 
	 * for (int i = 0; i < line.size(); i++) { writer.writeRecord(line.get(i));
	 * }
	 * 
	 * writer.close(); }
	 */

	/*
	 * private void deleteOutputFile() { String tempFile = _path +
	 * "/nlpOutput.csv"; // Delete if tempFile exists File fileTemp = new
	 * File(tempFile); if (fileTemp.exists()) { fileTemp.delete(); }
	 * 
	 * }
	 */

}
