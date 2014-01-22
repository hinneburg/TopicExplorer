package tools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

//import org.junit.Assert;

/**
 * 
 * um für den geparsten Text die Positionen aus dem originalem Text zu bestimmen
 * und dann die Daten für den Malletinput erzeugen
 * 
 */
public class WikiTextToCsvbackward {

	private final String wikiOrigText;
	private final String wikiParsedText;
	// private int failureId;
	private final Integer old_id;
	private final String wikiTitle;
	private final String wikiParsedTextReadable;

	private final List<String> tokens = new ArrayList<String>();
	private final List<Integer> startPositionsWikitext = new ArrayList<Integer>();
	private final List<Integer> startPositionsReadable = new ArrayList<Integer>();

	private List<PointInteger> posBracketsCurly;

	private List<PointInteger> posBracketsBox;

	private Integer intPointerBracketsCurly;
	private Integer intPointerBracketsBox;

	private BufferedWriter bwlogger;

	public WikiTextToCsvbackward(WikiArticle w) {

		this.wikiOrigText = new String(w.getWikiOrigText());
		this.wikiParsedText = w.getParsedWikiText();
		// this.failureId = w.getFailureId();
		this.old_id = w.getOldID();
		this.wikiTitle = w.getWikiTitle();
		this.wikiParsedTextReadable = w.getParsedWikiTextReadable();
	}

	public WikiTextToCsvbackward(WikiArticle w, BufferedWriter bwLogger) {
		this(w);
		this.bwlogger = bwLogger;
	}

	private void tokenizeReadabletextWithFixedTokenSequence(String text, List<String> tokenSearching) {
		// searching must be started from the back of the text in cause of the
		// same handling from the others

		// assumption: every word can be found in the text (the two parsing
		// texts,
		// readable text and parsed original text are not equal and could differ
		// in some parts like interpunction and prefix prostfix)

		try {
			Iterator<String> itToken = tokenSearching.iterator();
			Integer pos = text.length();
			String token;

			while (itToken.hasNext()) {
				token = itToken.next();
				pos = text.substring(0, pos).lastIndexOf(token); // every time
																	// the
																	// searchstring
																	// becomes
																	// shorter
				startPositionsReadable.add(pos);

				if (pos == -1 && itToken.hasNext()) {
					try {
						bwlogger.append("tokenize readable text: textposition not found, " + this.wikiTitle + " "
								+ token);
					} catch (IOException e) {
						System.err.println("bwlogger not found, tokenize readable text textposition not found");
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) // TODO specify exception type
		{
			System.err.println("failure in tokenize find, does only belongs to the tokenisation of the readable text. "
					+ e.getMessage());
		}
	}

	private void tokenize() throws Exception {

		posBracketsCurly = getBracketedPositionsCurlyBrackets();
		posBracketsBox = getBracketedPositionsBoxBrackets();

		Scanner scParsed = new Scanner(wikiParsedText); // liegt zeilenweise vor
		Integer savedPosition = wikiOrigText.length();
		Integer pos = -1;
		List<String> list = new ArrayList<String>();

		// preparation for reversing the article
		while (scParsed.hasNextLine()) {
			list.add(scParsed.nextLine());
		}
		scParsed = null;

		// beginning from behind
		for (int i = list.size() - 1; i >= 0; i--) {
			// read line
			String tmpLine = list.get(i);
			if (tmpLine.trim().length() > 0) {
				// find line in original text
				pos = wikiOrigText.substring(0, savedPosition).lastIndexOf(tmpLine);

				if (pos > -1) {

					// System.out.println(pos + " " + tmpLine + " old ");

					// the searching is done backwards

					// macht mit zuviele Fehler wegen Formeln die Brüche haben
					// und 2 Klammern öffnen {{
					// pos =
					// checkIfPositionOfWordIsWithinDoubleCurlyBrackets(pos,tmpLine);
					// // recursiv

					pos = checkIfPositionOfWordIsWithinBoxBrackets(pos, tmpLine);

					// System.out.println(pos + " " + tmpLine + " new " );
					//
					// takes the new position and the line, iterates and returns
					// the new position
					savedPosition = splitForOutputAndReturnNewPosition(tmpLine, pos);
				} else {
					// error output to logger
					bwlogger.append("catched error, " + wikiTitle + ": " + old_id + " *" + tmpLine + "*:" + pos
							+ ": last_savedposition= " + savedPosition + "\n");
				}
			} else {
				savedPosition = savedPosition - tmpLine.length() - 1; // new
																		// line
																		// also
																		// counts
																		// as
																		// one
																		// character
																		// in
																		// utf-8
			}
		}
		list.clear();
	}

	/**
	 * recursiv call, which returns the earliest position of the word which is
	 * not enclosed in curly brackets that means for example templates
	 * 
	 * @throws Exception
	 */
	// private Integer checkIfPositionOfWordIsWithinDoubleCurlyBrackets(Integer
	// posOfFoundedWord, String strLine) //throws Exception
	private Integer checkIfPositionOfWordIsWithinDoubleCurlyBrackets(Integer pos, String tmpLine) // throws
																									// Exception
	{
		if (intPointerBracketsCurly == 0) {
			return pos;
		}

		Integer posOfFoundedWord = -1;

		if (posBracketsCurly.get(intPointerBracketsCurly).getEndPosition() < pos) {
			return pos;
		}
		// wenn er genau drin liegt, neu suchen
		else if (posBracketsCurly.get(intPointerBracketsCurly).getStartPosition() <= pos
				&& posBracketsCurly.get(intPointerBracketsCurly).getEndPosition() >= pos) {
			// rekursiv
			intPointerBracketsCurly--;

			posOfFoundedWord = checkIfPositionOfWordIsWithinDoubleCurlyBrackets(wikiOrigText.substring(0, pos)
					.lastIndexOf(tmpLine), tmpLine);

		} else if (posBracketsCurly.get(intPointerBracketsCurly).getStartPosition() > pos) {
			if (intPointerBracketsCurly > 0) {
				Integer tmp = intPointerBracketsCurly - 1;
				if (posBracketsCurly.get(tmp).getEndPosition() < pos) {
					intPointerBracketsCurly--;
					posOfFoundedWord = pos;
				} else if (posBracketsCurly.get(tmp).getEndPosition() > pos) {
					// rekursiv
					intPointerBracketsCurly--;
					posOfFoundedWord = checkIfPositionOfWordIsWithinDoubleCurlyBrackets(pos, tmpLine);
				}
			}
		}

		if (posOfFoundedWord != -1) {
			return posOfFoundedWord;
		} else {
			return pos;
		}

		// // backwards
		//
		// Integer intReturn = posOfFoundedWord;
		// Integer tmp ;
		//
		// if (posBracketsCurly.size() == 0)
		// return intReturn;
		//
		// // if the position is within the brackets then the position must be
		// recalculated
		// if (posBracketsCurly.get(intPointerBracketsCurly).getStartPoint() <
		// posOfFoundedWord &&
		// posBracketsCurly.get(intPointerBracketsCurly).getEndPoint() >
		// posOfFoundedWord)
		// {
		// intReturn =
		// checkIfPositionOfWordIsWithinDoubleCurlyBrackets(wikiOrigText.substring(0,
		// Integer.valueOf(posBracketsCurly.get(intPointerBracketsCurly).getStartPoint())).lastIndexOf(strLine),strLine);
		// }
		// // if the founded position is behind the actual endpoint of the last
		// bracket, the position is ok
		// else if (posBracketsCurly.get(intPointerBracketsCurly).getEndPoint()
		// < posOfFoundedWord)
		// {
		// return posOfFoundedWord;
		// }
		// // if position is in front of the actual startpoint of a double curly
		// bracket, continue looking
		// else if
		// (posBracketsCurly.get(intPointerBracketsCurly).getStartPoint()>
		// posOfFoundedWord ) // tempWert verringern und gucken ob da schon
		// drüber ,dann Verringerung speichern
		// {
		// // if there are also brackets in lower textposition
		// if (intPointerBracketsCurly > 0 )
		// {
		//
		// // go temporary one bracketposition deeper (to the beginning)
		// tmp = intPointerBracketsCurly - 1;
		//
		// // if the founded position is behind the actual endpoint of the last
		// bracket, the position is ok
		// if (posBracketsCurly.get(tmp).getEndPoint() < posOfFoundedWord)
		// {
		// return posOfFoundedWord; // vorzeitiger abbruch
		// }
		// // renew the current pointer and recalculate the position with
		// recursiv call
		// else
		// {
		// intPointerBracketsCurly--;
		// intReturn =
		// checkIfPositionOfWordIsWithinDoubleCurlyBrackets(wikiOrigText.substring(0,
		// posBracketsCurly.get(intPointerBracketsCurly).getStartPoint()).lastIndexOf(strLine),strLine);
		// // rekursion
		// }
		// }
		// // return last founded position
		// else
		// {
		// intReturn = posOfFoundedWord;
		// }
		// }
		//
		// if (intReturn == -1){
		// if (posOfFoundedWord!=-1){
		// return posOfFoundedWord;
		// }
		//
		// // after splitting up the recursion the last founded position which
		// is not -1 will be returned
		// //
		// // else
		// // {
		// //// throw new Exception (wikiTitle + " " + old_id + " " + strLine +
		// ", pos:" + posOfFoundedWord +
		// " ,  funktion: checkIfPositionOfWordIsWithinAOpeningBracket " );
		// // }
		// }
		//
		// return intReturn;
		//
		// /*
		// pointer muss immer in richtiger und aktueller Position sein
		// d.h. wenn die gefundene Position innerhalb eines Klammernbereiches
		// liegt,
		// dann wird temporär der vorherige Bereich angeguckt und überprüft,
		// ob die Position nicht innerhalb eines Klammernbereiches liegt,
		// wenn dies so ist dann Treffer und return
		// */
	}

	// searching although backwards
	private Integer checkIfPositionOfWordIsWithinBoxBrackets(Integer pos, String tmpLine) {
		if (intPointerBracketsBox == 0) {
			return pos;
		}

		Integer posOfFoundedWord = -1;

		if (posBracketsBox.get(intPointerBracketsBox).getEndPosition() < pos) {
			return pos;
		}
		// wenn er genau drin liegt, neu suchen
		else if (posBracketsBox.get(intPointerBracketsBox).getStartPosition() <= pos
				&& posBracketsBox.get(intPointerBracketsBox).getEndPosition() >= pos) {
			// rekursiv
			intPointerBracketsBox--;

			posOfFoundedWord = checkIfPositionOfWordIsWithinBoxBrackets(
					wikiOrigText.substring(0, pos).lastIndexOf(tmpLine), tmpLine);

		} else if (posBracketsBox.get(intPointerBracketsBox).getStartPosition() > pos) {
			if (intPointerBracketsBox > 0) {
				Integer tmp = intPointerBracketsBox - 1;
				if (posBracketsBox.get(tmp).getEndPosition() < pos) {
					intPointerBracketsBox--;
					posOfFoundedWord = pos;
				}
				// rekursiv
				else if (posBracketsBox.get(tmp).getEndPosition() > pos) {
					intPointerBracketsBox--;
					posOfFoundedWord = checkIfPositionOfWordIsWithinBoxBrackets(pos, tmpLine);
				}
			}
		}

		if (posOfFoundedWord != -1) {
			return posOfFoundedWord;
		} else {
			return pos;
		}
	}

	// save positions of two consecutive curly brackets {{ ... }}, linear
	private List<PointInteger> getBracketedPositionsCurlyBrackets() {
		Integer bracketsCounter = 0;
		Boolean bracketsfound = false;
		Integer tmpx = 0;

		List<PointInteger> list = new ArrayList<PointInteger>();

		// rather a test
		if (-1 == wikiOrigText.indexOf("{{", 0)) {
			intPointerBracketsCurly = 0;
			return list;
		}

		// Länge -1, wegen char at i + 1
		for (Integer i = 0; i < wikiOrigText.length() - 1; i++) {

			if (wikiOrigText.charAt(i) == '{' && wikiOrigText.charAt(i + 1) == '{') {
				if (bracketsCounter == 0) {
					tmpx = i;
					bracketsfound = true;
				}

				bracketsCounter++;
			} else if (wikiOrigText.charAt(i) == '}' && wikiOrigText.charAt(i + 1) == '}') {
				bracketsCounter--;
			}

			if (bracketsfound && bracketsCounter == 0) {
				list.add(new PointInteger(tmpx, i));
				bracketsfound = false;
			}
		}
		intPointerBracketsCurly = list.size() - 1;
		return list;
	}

	// save positions of linktargets , between box bracket and | , not the
	// linktext
	// pictures could have "|" too
	private List<PointInteger> getBracketedPositionsBoxBrackets() {
		Integer tmpx = 0;
		Boolean boolBracket = false;

		List<PointInteger> list = new ArrayList<PointInteger>();

		// rather a test
		if (-1 == wikiOrigText.indexOf("[[", 0)) {
			intPointerBracketsBox = 0;
			return list;
		}

		// Länge -1, wegen char at i + 1
		for (Integer i = 0; i < wikiOrigText.length() - 1; i++) {

			if (wikiOrigText.charAt(i) == '[' && wikiOrigText.charAt(i + 1) == '[') {
				tmpx = i;
				boolBracket = true;
			} else if (wikiOrigText.charAt(i) == '|') {
				if (boolBracket) {
					list.add(new PointInteger(tmpx, i));
					boolBracket = false;
				}

			}

		}

		intPointerBracketsBox = list.size() - 1;
		return list;
	}

	/**
	 * calculation in forward direction, reversing the output that the csv file
	 * for mallet is completely in backward direction
	 * 
	 */
	private Integer splitForOutputAndReturnNewPosition(String tmpLine, int posOfLine) {
		Integer endToken = -1;
		Integer startToken = 0;

		// for reversing
		List<String> tokens_temp = new ArrayList<String>();
		List<Integer> startPositions_temp = new ArrayList<Integer>();

		while (startToken < tmpLine.length()) {
			for (startToken = endToken + 1; startToken < tmpLine.length(); startToken++) {
				if (Character.toString(tmpLine.charAt(startToken)).matches("\\p{L}")) {
					break;
				}
			}
			for (endToken = startToken; endToken < tmpLine.length(); endToken++) {
				if (Character.toString(tmpLine.charAt(endToken)).matches("\\P{L}")) {
					break;
				}
			}
			if (startToken < tmpLine.length()) {
				// // System.out.println(startToken +" "+ endToken);
				// // System.out.println(tmpLine.substring(startToken,
				// endToken));

				// only words with length greater than 1 are parsed
				if (tmpLine.substring(startToken, endToken).length() > 1) {
					tokens_temp.add(tmpLine.substring(startToken, endToken));
					startPositions_temp.add(startToken + posOfLine);
				}
			}
		}

		// reversing the output
		for (Integer i = tokens_temp.size() - 1; i >= 0; i--) {
			tokens.add(tokens_temp.get(i));
			startPositionsWikitext.add(startPositions_temp.get(i));
		}

		// release the variables
		tokens_temp.clear();
		tokens_temp = null;

		startPositions_temp.clear();
		startPositions_temp = null;

		return posOfLine;
	}

	public String getCSV() throws Exception {

		tokenize();

		tokenizeReadabletextWithFixedTokenSequence(wikiParsedTextReadable, tokens);

		StringBuilder sb = new StringBuilder();

		Iterator<String> itToken = tokens.iterator();
		Iterator<Integer> itSeqWikitext = startPositionsWikitext.iterator();
		Iterator<Integer> itSeqReadable = startPositionsReadable.iterator();

		while (itToken.hasNext() && itSeqWikitext.hasNext()) {
			String token = itToken.next();
			Integer seqWikitext = itSeqWikitext.next();
			Integer seqReadable = itSeqReadable.next();

			sb.append("\"" + old_id + "\";" + "\"" + String.valueOf(seqReadable) + "\";" + "\"" + token.toLowerCase()
					+ "\";" + "\"" + token + "\";" + "\"" + String.valueOf(seqWikitext) + "\";" + "\n");

			// temp assert which is working
			// if (seq > -1 ){
			// Assert.assertEquals(token, wikiOrigText.substring(seq, seq+
			// token.length()));
			// }
		}

		return sb.toString();

	}
}
