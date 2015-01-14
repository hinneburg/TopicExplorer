package cc.topicexplorer.plugin.mecab.preprocessing.implementation.postagger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.chasen.mecab.Node;
import org.chasen.mecab.Tagger;

public class JPOSMeCab {
	
	public JPOSMeCab(String libraryPath) {
		try {
			File f;
			f = new File(libraryPath);
			if(f.exists() && !f.isDirectory()) {
				System.load(libraryPath);
			} else {
				System.err.println(libraryPath.equals("/opt/local/lib/libmecab-java.dylib") + " " + f.exists() + " " + f.isDirectory() + "mecab-java library not found. Please install mecab-java libraries and check the config");
				System.exit(1);
			}
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Cannot load the required mecab-java libraries\n" + e);
			System.exit(1);
		}
	}

	public List<String> parseString(int docID, String str) {
		List<String> csvList = new ArrayList<String>();
		Tagger tagger = new Tagger();

		// do not leave out this one as parseToNode() will output weird symbols (文字化け) otherwise...
		tagger.parse(str);		
		
		Node node = tagger.parseToNode(str);

		JPOSTokenPipeline queue = new JPOSTokenPipeline();

		int pos = -1;
		int newPos = -1;
		JPOSToken jposToken = null;
		String token = "";

		for (;node != null; node = node.getNext()) {
			token = node.getSurface();
			if(token.trim().length() > 0) {
				newPos = str.indexOf(token, pos);
				if(newPos > -1) {
					pos = newPos;
					
					jposToken = new JPOSToken(docID, pos, this.getNodeTerm(node), token, node.getPosid());
					
					queue.add( jposToken );
		
					// SPECIAL 1: suru verb
					if ( this.isSuruVerb(jposToken) ) {
						JPOSToken previousToken = queue.getPreviousToken();
						
						if ( previousToken != null && this.isNoun( previousToken ) ) {
							previousToken.increaseContinuation();
						}
					}
					// end special 1
					
					
					// SPECIAL 2: verb or adjective
					if ( this.isUnindependendVerb(jposToken) || this.isAuxiliaryVerb(jposToken) ) {
						int prevStep = 1;
						JPOSToken previousToken = queue.getPreviousToken(prevStep);
						JPOSToken currentToken = jposToken;
						
						while ( previousToken != null &&
								( this.isVerb( previousToken ) || this.isAuxiliaryVerb( previousToken ) || this.isAdjective( previousToken ) )
							) {
							// break in case of consecutive verbs
							// example 1: 食べ.きれる
							if ( this.isVerb( currentToken ) && this.isVerb( previousToken ) )
								break;
							// example 2: 戻っ.て.行く
							if ( this.isVerb( currentToken ) && this.isAuxiliaryVerb( previousToken ) )
								break;
							
							previousToken.increaseContinuation();
							prevStep++;
							
							currentToken = previousToken;
							previousToken = queue.getPreviousToken(prevStep);
						}
					}
					// end special 2
					
		
					// empty queue
					while ( queue.full() ) {
						csvList.add(queue.flush().csvString());
					}
				} else {
					System.out.println("Char not found: " + token);
				}
			}
		}

		while ( queue.tokensLeft() ) {
			csvList.add(queue.flush().csvString());
		}
		
		tagger.delete();
		return csvList;	
	}
	
	private boolean isAdjective( JPOSToken token ) {
		int posID = token.getPosID();
		return ( posID >= 10 && posID <= 12 );
	}

	
	private boolean isSuruVerb( JPOSToken token ) {
		// no verb
		if ( !this.isIndependendVerb(token) ) {
			return false;
		}
		boolean isSuruVerb = token.getTerm().equals("する");
		return isSuruVerb;
	}

	private boolean isVerb( JPOSToken token ) {
		return this.isIndependendVerb(token) || this.isUnindependendVerb(token);
	}
	
	private boolean isIndependendVerb( JPOSToken token ) {
		return token.getPosID() == 31;
	}
	
	private boolean isUnindependendVerb( JPOSToken token ) {
		return		token.getPosID() == 32	// 動詞 → 接尾
				|| token.getPosID() == 33	// 動詞 → 非自立
				;
	}
	
	private boolean isAuxiliaryVerb( JPOSToken token ) {
		return		token.getPosID() == 25	// 助動詞
				|| token.getPosID() == 18	// 助詞 → 接続助詞
				;
	}
	
	private boolean isNoun( JPOSToken token ) {
		int posID = token.getPosID();
		return ( posID >= 36 && posID <= 67 );
	}
	
	
	
	
	private String getNodeTerm( Node node ) {
		String feature = node.getFeature();
		String[] fields = feature.split(",");
	//	System.out.println(feature);
		// pick out 7th field (index 6)
		// 動詞,自立,*,*,サ変・スル,連用形,する,シ,シ

		return fields[6]; // term
	}
}
