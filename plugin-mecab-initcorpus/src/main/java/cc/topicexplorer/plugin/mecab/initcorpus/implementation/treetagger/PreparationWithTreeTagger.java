package cc.topicexplorer.plugin.mecab.initcorpus.implementation.treetagger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import cc.topicexplorer.lucene.analyzer.treetagger.TreeTaggerAnalyzer;

import com.google.common.base.Joiner;

public class PreparationWithTreeTagger {
	private String outToken;
	private String outTerm;
	private String outWordType;
	private Integer outStartPosition;

	private char outCSVSeparator;
	private HashMap<String,Integer> tag2id;
	private static Logger logger;
	private static Pattern colonTokenPattern = Pattern.compile("Token:(.*)");
	private static Pattern colonLemmaPattern = Pattern.compile("Lemma:(.*)");
	private static Matcher matcher;

	private TreeTaggerAnalyzer analyzer;

	public PreparationWithTreeTagger(char outCSVSeparator,
			String pathToTreeTagger, String treeTaggerModel, HashMap<String,Integer> tag2id) {
		this.outToken = new String();
		this.outTerm = new String();
		this.outWordType = new String();
		this.outStartPosition = new Integer(0);
		this.outCSVSeparator = outCSVSeparator;
		this.analyzer = new TreeTaggerAnalyzer(pathToTreeTagger,
				treeTaggerModel);
		this.tag2id = tag2id;
	}

	public void setLogger(Logger logger) {
		PreparationWithTreeTagger.logger = logger;
	}


	public List<String> parse(Integer outDocumentId, String fullText)
			throws IOException {

		ArrayList<String> out = new ArrayList<String>();
		logger.info("Document:" + outDocumentId);
		final TokenStream tokenStream = this.analyzer.tokenStream("testField",
				fullText);

		OffsetAttribute offsetAttribute = tokenStream
				.addAttribute(OffsetAttribute.class);
		CharTermAttribute charTermAttribute = tokenStream
				.addAttribute(CharTermAttribute.class);
		TypeAttribute typeAttribute = tokenStream
				.addAttribute(TypeAttribute.class);

		tokenStream.reset();
		// A TreeTagger stream produces alternating token and lemma
		// indicated by same startOffset.
		while (tokenStream.incrementToken()) {
			this.outStartPosition = offsetAttribute.startOffset();
			matcher = colonTokenPattern.matcher(typeAttribute.type());
			if (matcher.find()) {
				this.outWordType = matcher.group(1);
			}
			this.outToken = charTermAttribute.toString();
			if (!tokenStream.incrementToken()) {
				logger.error("Uneven number of token produced by TreeTagger-Analyzer");
				break;
			}
			this.outTerm = charTermAttribute.toString();
			matcher = colonLemmaPattern.matcher(typeAttribute.type());
			String lemmaType = "";
			if (matcher.find()) {
				lemmaType = matcher.group(1);
			}

			if (!this.outStartPosition.equals(offsetAttribute.startOffset())
					|| !this.outWordType.equals(lemmaType)) {
				logger.error("Position or type of respective token and term produced by TreeTagger-Analyzer are not matching.");
				break;
			}
			if (!this.tag2id.containsKey(outWordType)) {
				logger.error("Pos tag " + outWordType + " is not in table POS_TYPE");
				throw new RuntimeException();
			}
			String[] outRecord = { outDocumentId.toString(),
					outStartPosition.toString(), "\""+outTerm+"\"", "\""+outToken+"\"", this.tag2id.get(outWordType).toString() };

			out.add(Joiner.on(this.outCSVSeparator).join(outRecord));
		}
		tokenStream.close();
		return out;
	}

}
