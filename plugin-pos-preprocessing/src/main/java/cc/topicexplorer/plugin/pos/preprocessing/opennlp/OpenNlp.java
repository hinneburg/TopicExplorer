package cc.topicexplorer.plugin.pos.preprocessing.opennlp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvWriter;

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

	String[] tableHeader = null;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String[] getHeader() {
		return tableHeader;
	}

	public void setHeader(String[] tableHeaders) {
		this.tableHeader = tableHeaders;

	}

	public void SentenceDetect() throws InvalidFormatException, IOException {

		// always start with a model, a model is learned from training data
		InputStream is = new FileInputStream(path + "en-sent.bin");
		// InputStream is = PropertiesUtil.class.getResourceAsStream("/"
		// + "en-sent.bin");
		SentenceModel model = new SentenceModel(is);
		SentenceDetectorME sdetector = new SentenceDetectorME(model);
		is.close();

		is = new FileInputStream(path + "sentences");
		// is = PropertiesUtil.class.getResourceAsStream("/" + "sentences");
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
			getFile(TokenizeSentences(sentences[i]));
		}
	}

	public List<String[]> TokenizeSentences(String sent)
			throws InvalidFormatException, IOException {
		InputStream modelIn = new FileInputStream(path + "en-token.bin");
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
		/*
		 * for (int i = 0; i < tokens.length; i++) {
		 * System.out.println(tokens[i]);
		 * 
		 * }
		 */
		return PartOfSpeechTagging(tokens);

	}

	public List<String[]> PartOfSpeechTagging(String[] sent) throws IOException {
		InputStream modelIn = null;
		POSModel model;
		try {
			modelIn = new FileInputStream(path + "en-pos-maxent.bin");
			// modelIn = PropertiesUtil.class.getResourceAsStream("/"
			// + "en-pos-maxent.bin");

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
		String[] tags = tagger.tag(sent);

		for (int i = 0; i < tags.length; i++) {
			String[] tag = { "Document_ID", String.valueOf(i), sent[i], tags[i] };
			/*
			 * System.out.println(sent[i] + " on position " + i + " is a " +
			 * tags[i]);
			 */
			result.add(tag);
		}
		return result;
	}

	public void getOutputCSV() throws InvalidFormatException, IOException {

		this.setHeader(new String[] { "DOCUMENT_ID", "POSITION", "TOKEN",
				"PART_OF_SPEECH" });

		this.deleteOutputFile();

		this.writeHeader();

		this.SentenceDetect();

	}

	private void writeHeader() throws IOException {
		CsvWriter writer = new CsvWriter(path + "/nlpOutput.csv");
		writer.writeRecord(this.tableHeader);
		writer.close();
	}

	private void getFile(List<String[]> line) throws IOException {
		FileOutputStream stream = new FileOutputStream(path + "/nlpOutput.csv",
				true);
		CsvWriter writer = new CsvWriter(stream, ',', Charset.defaultCharset());

		for (int i = 0; i < line.size(); i++) {
			writer.writeRecord(line.get(i));
		}

		writer.close();
	}

	private void deleteOutputFile() {
		String tempFile = path + "/nlpOutput.csv";
		// Delete if tempFile exists
		File fileTemp = new File(tempFile);
		if (fileTemp.exists()) {
			fileTemp.delete();
		}

	}
}
