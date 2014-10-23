package cc.topicexplorer.plugin.pos.preprocessing.implementation.postagger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.chasen.mecab.Node;
import org.chasen.mecab.Tagger;

public class JPOSMeCab implements JapanesePartOfSpeechExtractor {
	// my own edits, so I can use MeCab with my classes
	private Node node;
	private Tagger tagger;
	private JPOSTokenPipeline queue;
	private int pos = 0;
	private String str="";
	private JPOSToken token = null;

	public void initialize(String paragraph) {

		System.out.println("start parsing");

		try {
			// TODO dynamic machen!!
			System.load("/Users/Roman/Roman/Uni/Japanologie/MeCab/mecab-java-0.994/libMeCab.so");
			System.loadLibrary("MeCab");
		} catch (UnsatisfiedLinkError e) {
			System.err
					.println("Cannot load the example native code.\nMake sure your LD_LIBRARY_PATH contains \'.\'\n"
							+ e);
			System.exit(1);
		}

		tagger = new Tagger();

		// do not leave out this one as parseToNode() will output weird symbols
		// (文字化け) otherwise...
		tagger.parse(paragraph);

		// System.out.println("dictionary filename: " +
		// tagger.dictionary_info().getFilename());
		// System.out.println("dictionary charset: " +
		// tagger.dictionary_info().getCharset());

		node = tagger.parseToNode(str);

		queue = new JPOSTokenPipeline();
		
		fillQueue();
	}

	private void fillQueue(){
		while(!queue.full())
		{
			putTokenIntoQueue();
		}
	}
	
	public boolean getJPOSToken(JPOSToken token)
	{
		if(this.node != null){
			putTokenIntoQueue();
			token = queue.flush();
			return true;
		}
		return emptyQueue(token);
	}
	
	private boolean emptyQueue(JPOSToken token){
		if(queue.tokensLeft()){
			token = queue.flush();
			return true;
		}
		return false;
	}
	
	private void putTokenIntoQueue(){
		for (; this.node != null; node = node.getNext()) {
			pos = str.indexOf(node.getSurface(), pos);

			token = new JPOSToken(this.documentID, pos, this.getNodeTerm(node),
					node.getSurface(), node.getPosid());

			pos += node.getSurface().length();

			if (this.isEmptyToken(token)) {
				continue;
			}

			queue.add(token);

			// SPECIAL 1: suru verb
			if (this.isSuruVerb(token)) {
				JPOSToken previousToken = queue.getPreviousToken();

				if (previousToken != null && this.isNoun(previousToken))
					previousToken.increaseContinuation();
			}
			// end special 1

			// SPECIAL 2: verb or adjective
			if (this.isUnindependendVerb(token) || this.isAuxiliaryVerb(token)) {
				int prevStep = 1;
				JPOSToken previousToken = queue.getPreviousToken(prevStep);
				JPOSToken currentToken = token;

				while (previousToken != null
						&& (this.isVerb(previousToken)
								|| this.isAuxiliaryVerb(previousToken) || this
									.isAdjective(previousToken))) {
					// break in case of consecutive verbs
					// example 1: 食べ.きれる
					if (this.isVerb(currentToken) && this.isVerb(previousToken))
						break;
					// example 2: 戻っ.て.行く
					if (this.isVerb(currentToken)
							&& this.isAuxiliaryVerb(previousToken))
						break;

					previousToken.increaseContinuation();
					prevStep++;

					currentToken = previousToken;
					previousToken = queue.getPreviousToken(prevStep);
				}
			}
			// end special 2
	}
	
	// end of edits
	private int documentID = -1;

	public int getDocumentID() {
		return documentID;
	}

	public void setDocumentID(int documentID) {
		this.documentID = documentID;
	}

	// TODO delete?
	private String readFile(String filename) {
		String content = null;
		File file = new File(filename); // for ex foo.txt
		try {
			FileReader reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public void parse(String inputFile, String outputFile) {
		String str = this.readFile(inputFile);

		parseString(str);
	}

	public void parseString(String str) {
		// System.out.println(str);

		System.out.println("start parsing");

		try {
			// TODO dynamic machen!!
			System.load("/Users/Roman/Roman/Uni/Japanologie/MeCab/mecab-java-0.994/libMeCab.so");
			System.loadLibrary("MeCab");
		} catch (UnsatisfiedLinkError e) {
			System.err
					.println("Cannot load the example native code.\nMake sure your LD_LIBRARY_PATH contains \'.\'\n"
							+ e);
			System.exit(1);
		}

		Tagger tagger = new Tagger();

		// do not leave out this one as parseToNode() will output weird symbols
		// (文字化け) otherwise...
		tagger.parse(str);

		// System.out.println("dictionary filename: " +
		// tagger.dictionary_info().getFilename());
		// System.out.println("dictionary charset: " +
		// tagger.dictionary_info().getCharset());

		Node node = tagger.parseToNode(str);

		JPOSTokenPipeline queue = new JPOSTokenPipeline();

		int pos = 0;

		JPOSToken token = null;

		for (; node != null; node = node.getNext()) {
			pos = str.indexOf(node.getSurface(), pos);

			token = new JPOSToken(this.documentID, pos, this.getNodeTerm(node),
					node.getSurface(), node.getPosid());

			pos += node.getSurface().length();

			if (this.isEmptyToken(token)) {
				continue;
			}

			queue.add(token);

			// SPECIAL 1: suru verb
			if (this.isSuruVerb(token)) {
				JPOSToken previousToken = queue.getPreviousToken();

				if (previousToken != null && this.isNoun(previousToken))
					previousToken.increaseContinuation();
			}
			// end special 1

			// SPECIAL 2: verb or adjective
			if (this.isUnindependendVerb(token) || this.isAuxiliaryVerb(token)) {
				int prevStep = 1;
				JPOSToken previousToken = queue.getPreviousToken(prevStep);
				JPOSToken currentToken = token;

				while (previousToken != null
						&& (this.isVerb(previousToken)
								|| this.isAuxiliaryVerb(previousToken) || this
									.isAdjective(previousToken))) {
					// break in case of consecutive verbs
					// example 1: 食べ.きれる
					if (this.isVerb(currentToken) && this.isVerb(previousToken))
						break;
					// example 2: 戻っ.て.行く
					if (this.isVerb(currentToken)
							&& this.isAuxiliaryVerb(previousToken))
						break;

					previousToken.increaseContinuation();
					prevStep++;

					currentToken = previousToken;
					previousToken = queue.getPreviousToken(prevStep);
				}
			}
			// end special 2

			// empty queue
			while (queue.full()) {
				// TODO flush to DB
				System.out.println(queue.flush().csvString());
			}
		}

		while (queue.tokensLeft()) {
			// TODO flush to DB
			System.out.println(queue.flush().csvString());
		}

	}

	private boolean isEmptyToken(JPOSToken token) {
		boolean isEmpty = token.getToken().equals("");
		return isEmpty;
	}

	private boolean isAdjective(JPOSToken token) {
		int posID = token.getPosID();
		return (posID >= 10 && posID <= 12);
	}

	private boolean isSuruVerb(JPOSToken token) {
		// no verb
		if (!this.isIndependendVerb(token))
			return false;

		boolean isSuruVerb = token.getTerm().equals("する");
		return isSuruVerb;
	}

	private boolean isVerb(JPOSToken token) {
		return this.isIndependendVerb(token) || this.isUnindependendVerb(token);
	}

	private boolean isIndependendVerb(JPOSToken token) {
		return token.getPosID() == 31;
	}

	private boolean isUnindependendVerb(JPOSToken token) {
		return token.getPosID() == 32 // 動詞 → 接尾
				|| token.getPosID() == 33 // 動詞 → 非自立
		;
	}

	private boolean isAuxiliaryVerb(JPOSToken token) {
		return token.getPosID() == 25 // 助動詞
				|| token.getPosID() == 18 // 助詞 → 接続助詞
		;
	}

	private boolean isNoun(JPOSToken token) {
		int posID = token.getPosID();
		return (posID >= 36 && posID <= 67);
	}

	private String getNodeTerm(Node node) {
		String feature = node.getFeature();
		String[] fields = feature.split(",");

		// pick out 7th field (index 6)
		// 動詞,自立,*,*,サ変・スル,連用形,する,シ,シ

		return fields[6]; // term
	}

	private void writeTokenToDB(JPOSToken token) {
		// TODO do it
	}

	// debug
	@Deprecated
	private void printNode(Node node) {
		System.out.println(node.getSurface());
		System.out.println("alpha:\t\t" + node.getAlpha());
		System.out.println("beta:\t\t" + node.getBeta());
		System.out.println("char type:\t" + node.getChar_type());
		System.out.println("cost:\t\t" + node.getCost());
		System.out.println("feature:\t" + node.getFeature());
		System.out.println("id:\t\t" + node.getId());
		System.out.println("is best:\t" + node.getIsbest());
		System.out.println("lc attr:\t" + node.getLcAttr());
		System.out.println("length:\t\t" + node.getLength());
		System.out.println("pos id:\t\t" + node.getPosid());
		System.out.println("prob:\t\t" + node.getProb());
		System.out.println("rc attr:\t" + node.getRcAttr());
		System.out.println("rlength:\t" + node.getRlength());
		System.out.println("stat:\t\t" + node.getStat());
		// System.out.println("surface:\t" + node.getSurface());
		System.out.println("wcost:\t\t" + node.getWcost());
	}

	// private String getWordClass( int posID )
	// {
	// if ( posID <= 0 ) return "";
	// else if ( posID >= 3 && posID <= 9 ) return "SYM";
	// else if ( posID >= 10 && posID <= 12 ) return "ADJ";
	// else if ( posID >= 13 && posID <= 24 ) return "PART";
	// else if ( posID == 25 ) return "AUXV";
	// else if ( posID == 26 ) return "CONJ";
	// else if ( posID >= 27 && posID <= 30 ) return "PREF";
	// else if ( posID >= 31 && posID <= 33 ) return "VERB";
	// else if ( posID >= 34 && posID <= 35 ) return "ADV";
	// else if ( posID >= 36 && posID <= 67 ) return "NOUN";
	// else return "MISC";
	// }

}
