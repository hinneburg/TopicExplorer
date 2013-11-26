package tools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Scanner;
import java.util.TreeSet;

/*
 - pointersache kann auch mit hashmaps oder treemap gemacht werden , wäre glaube besser, damit nicht eventtuell pointerfehler entstehen kann bei checkIfPositionOfWordIsWithinBoxBrackets
 - vllt nochmal assert test
 - !! testen und vergleichen ob es auch wirklich geht, testen mit indem verglichen wird mit der alten implementierung
 */

/**
 * 
 * um für den geparsten Text die Positionen aus dem originalem Text zu bestimmen
 * und dann die Daten für den Malletinput erzeugen
 * 
 */
public class WikiTextToCSVForeward {

	private final String wikiOrigText;
	private final String wikiParsedText;
	private final Integer old_id;
	private final String wikiTitle;
	private final String wikiParsedTextReadable;

	private List<String> tokensParsedText = new ArrayList<String>();
	private List<Integer> startPositionsWikiText = new ArrayList<Integer>();
	private List<Integer> startPositionsReadableText = new ArrayList<Integer>();

	private List<String> tokensParsedReadableText = new ArrayList<String>();
	private List<Integer> startPositionsReadableTextNew = new ArrayList<Integer>();

	// private List<PointInteger> posBracketsBox;
	// private Integer intPointerBracketsBox;

	private BufferedWriter bwlogger;

	private HashMap<Integer, Integer> bracketPositionsHashMap;
	private NavigableSet<Integer> bracketsNavigableSet;

	private String extraNextToLeftElement = "";

	private HashMap<Integer, Integer> sectionCaptionPosition = new HashMap<Integer, Integer>();

	private Boolean boolBreak = false;
	private BracketPositions bp;

	public WikiTextToCSVForeward(WikiArticle w) {

		this.wikiOrigText = new String(w.getWikiOrigText());
		this.wikiParsedText = w.getParsedWikiText();
		this.old_id = w.getOldID();
		this.wikiTitle = w.getWikiTitle();
		this.wikiParsedTextReadable = w.getParsedWikiTextReadable();
	}

	public WikiTextToCSVForeward(WikiArticle w, BufferedWriter bwLogger) {
		this(w);
		this.bwlogger = bwLogger;
	}

	/**
	 * 
	 * new variant, now all words are given, the set from the wikiText is more
	 * or equal than the set from the readable / normalized text, so there could
	 * be words who have no join partner
	 */
	private void tokenizeReadableText(String text) {

		Integer endToken = -1;
		Integer startToken = 0;

		while (startToken < text.length()) {
			for (startToken = endToken + 1; startToken < text.length(); startToken++) {
				if (Character.toString(text.charAt(startToken)).matches("\\p{L}"))
					break;
			}
			for (endToken = startToken; endToken < text.length(); endToken++) {
				if (Character.toString(text.charAt(endToken)).matches("\\P{L}"))
					break;
			}
			if (startToken < text.length()) {

				// only words with length greater than 1 are parsed
				if (text.substring(startToken, endToken).length() > 1) {
					tokensParsedReadableText.add(text.substring(startToken, endToken));
					startPositionsReadableTextNew.add(startToken);
				}
			}
		}

	}

	// //old
	// private void tokenizeReadableTextWithFixedTokenSequence(String text,
	// List<String> tokenSequence) {
	// // assumption: every parsed word of the wikitext can be found in the
	// // normalised text
	// // readable text and parsed original text are not equal and could differ
	// // in some parts like interpunction and prefix postfix of wiki-links)
	//
	// try {
	// Iterator<String> itToken = tokenSequence.iterator();
	// Integer position = 0;
	// Integer savedPosition = 0;
	// String token;
	//
	// while (itToken.hasNext()) {
	// token = itToken.next();
	// position = text.substring(savedPosition, text.length()).indexOf(token);
	// // every
	// // time
	// // the
	// // search-string
	// // becomes
	// // shorter
	// startPositionsReadableText.add(position + savedPosition);
	//
	// if (position == -1 && itToken.hasNext()) {
	// try {
	// bwlogger.append("tokenize readable text: textposition not found, " +
	// this.wikiTitle + " "
	// + token + "\n"); // perhaps it
	// // doesn't
	// // come to
	// // this
	// // point
	// } catch (IOException e) {
	// System.err.println("bwlogger not found, tokenize readable text textposition not found");
	// e.printStackTrace();
	// }
	// } else {
	// savedPosition = position + savedPosition + token.length();
	//
	// }
	// }
	// } catch (Exception e) {
	// System.err
	// .println("failure in tokenizeReadableTextWithFixedTokenSequence, does only belongs to the tokenisation of the readable text. "
	// + e.getMessage());
	// // e.printStackTrace();
	// }
	// }

	private void tokenize() throws Exception {

		getBracketsPositionAndPutIntoNavigableSet();

		Scanner scParsed = new Scanner(wikiParsedText); // liegt zeilenweise vor
		Integer savedPosition = 0;
		Integer pos = -1;
		String tmpLineWithoutExtraInfo;
		Boolean bool_found = false;
		Boolean bool_dont_shift_position = false;
		Integer shifting = 0;
		String tmpExtraInfoTextForControll;

		while (scParsed.hasNextLine()) {
			// read line
			String tmpLine = scParsed.nextLine();
			if (tmpLine.trim().length() > 0) {
				// find the line in the original wiki-text

				tmpLineWithoutExtraInfo = getNormalTextIfExtraInfosAddedToTheEnd(tmpLine);
				bool_dont_shift_position = getBoolShift(tmpLine);

				// HashMap <Integer,Integer> pictureList = new HashMap<Integer,
				// Integer>();
				// ArrayList<PointInteger> pictureList2 = new
				// ArrayList<PointInteger>();
				// pictureList2.add(new PointInteger(startPoint, endPoint));

				pos = wikiOrigText.substring(savedPosition).indexOf(tmpLineWithoutExtraInfo);

				if (pos > -1) {

					// System.out.println(pos + " " + tmpLine + " old " );

					try {

						// pos =
						// checkIfFoundedPositionIsWithinABracketNaviagbleSet(pos
						// + savedPosition,
						// tmpLineWithoutExtraInfo);

						while (true) {

							pos = checkIfFoundedPositionIsWithinABracketNaviagbleSet(pos + savedPosition,
									tmpLineWithoutExtraInfo);

							// System.out.println("schleife " + pos + "  " +
							// extraNextToLeftElement + " "
							// + tmpLineWithoutExtraInfo);

							if (extraNextToLeftElement.length() == 0) {
								bool_found = true;
							} else if (extraNextToLeftElement.length() > 0) {

								if (bool_dont_shift_position) {
									shifting = 0;
								} else {
									shifting = extraNextToLeftElement.length();
								}

								if (wikiOrigText.length() >= (pos + tmpLineWithoutExtraInfo.length() + extraNextToLeftElement
										.length())) {
									tmpExtraInfoTextForControll = wikiOrigText.substring(pos - shifting, pos
											+ tmpLineWithoutExtraInfo.length() + extraNextToLeftElement.length());

								} else {

									tmpExtraInfoTextForControll = wikiOrigText.substring(pos - shifting, pos
											+ tmpLineWithoutExtraInfo.length());
								}

								if (tmpExtraInfoTextForControll.startsWith(extraNextToLeftElement)) { // für
																										// den
																										// Fall
																										// das
																										// keine
																										// Leerzeichen
																										// dazwischen
																										// sind
									bool_found = true;
								} else if (tmpExtraInfoTextForControll.endsWith(extraNextToLeftElement)) {
									bool_found = true;
								}

							}

							if ((pos > -1) && (bool_found == true)) {
								break;
							} else if (pos == -1) {

								bwlogger.append("test:failling in tokenize " + wikiTitle + " " + old_id + "\n");

								throw new Exception(
										"failure in checkIfPositionOfWordIsWithinBoxBrackets, tokenize() , pos not found , "
												+ wikiTitle + " " + old_id + " ");
							}

							Integer posTmpSave = pos + tmpLineWithoutExtraInfo.length();
							pos = wikiOrigText.substring(posTmpSave).indexOf(tmpLineWithoutExtraInfo);
							if (pos > -1) {
								pos = pos + posTmpSave;
							}

							// System.out.println(wikiOrigText.substring(pos +
							// tmpLineWithoutExtraInfo.length()
							// + extraNextToLeftElement.length(), pos +
							// tmpLineWithoutExtraInfo.length()
							// + extraNextToLeftElement.length() + 20));

						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						bwlogger.append("fehler in tokenize " + this.wikiTitle + " " + e.getMessage());
						System.err.println("fehler in tokenize " + this.wikiTitle + " " + e.getMessage());

						if (boolBreak) {
							e.printStackTrace();
							System.exit(25);
						}

						// e.printStackTrace();
					}

					// System.out.println(pos + " " + tmpLine + " new ");

					// takes the last position of the line, iterates all
					// elements, saves the position of every token from the text
					// and returns the new startposition (the last )

					savedPosition = splitForOutputAndReturnNewPosition(tmpLineWithoutExtraInfo, pos);

					// for section-caption position? TODO
					if (tmpLine.endsWith(ExtraInformations.extraSectionCaptionAppend)) {
						sectionCaptionPosition.put(pos, savedPosition);
					}

				} else {
					// error output to logger
					bwlogger.append("catched error, " + wikiTitle + ": " + old_id + " *" + tmpLineWithoutExtraInfo
							+ "*:" + (new Integer(pos + savedPosition)) + ": last_savedposition= " + savedPosition
							+ " id:" + old_id + "\n");
				}
			} else {
				savedPosition = savedPosition + 1; // new line also counts as
													// one character in utf-8
			}
		}
		scParsed = null;
	}

	private String getNormalTextIfExtraInfosAddedToTheEnd(String tmpLine) {

		String output;

		if (tmpLine.endsWith(ExtraInformations.extraBoldAppend)) {
			output = tmpLine.replace(ExtraInformations.extraBoldAppend, "");
			extraNextToLeftElement = "'''";
		} else if (tmpLine.endsWith(ExtraInformations.extraCursiveAppend)) {
			output = tmpLine.replace(ExtraInformations.extraCursiveAppend, "");
			extraNextToLeftElement = "''";
		} else if (tmpLine.endsWith(ExtraInformations.extraInternalLink)) {
			output = tmpLine.replace(ExtraInformations.extraInternalLink, "");
			extraNextToLeftElement = "|";
		} else if (tmpLine.endsWith(ExtraInformations.extraPicure1Append)) {
			output = tmpLine.replace(ExtraInformations.extraPicure1Append, "");
			extraNextToLeftElement = getExtraNextToElementForPictures(tmpLine);
		} else if (tmpLine.endsWith(ExtraInformations.extraPicure2Append)) {
			output = tmpLine.replace(ExtraInformations.extraPicure2Append, "");
			extraNextToLeftElement = "|";
		} else if (tmpLine.endsWith(ExtraInformations.extraPicure3Append)) {
			output = tmpLine.replace(ExtraInformations.extraPicure3Append, "");
			extraNextToLeftElement = getExtraNextToElementForPictures(tmpLine);
		} else if (tmpLine.endsWith(ExtraInformations.extraSectionCaptionAppend)) {
			output = tmpLine.replace(ExtraInformations.extraSectionCaptionAppend, "");
			extraNextToLeftElement = "";
		} else {
			extraNextToLeftElement = "";
			output = tmpLine;
		}
		return output;
	}

	/**
	 * because [[Datei:...]] or [[File:...]] is within the string, no shifting
	 * is needed
	 */
	private boolean getBoolShift(String tmpLine) {
		if (tmpLine.endsWith(ExtraInformations.extraPicure1Append)
				|| tmpLine.endsWith(ExtraInformations.extraPicure2Append)
				|| tmpLine.endsWith(ExtraInformations.extraPicure3Append)) {
			return true;
		} else {
			return false;
		}
	}

	private String getExtraNextToElementForPictures(String tmpLine) {

		if (tmpLine.startsWith("Datei")) {
			return ExtraInformations.extraFileNameGerman;
		} else if (tmpLine.startsWith("File")) {
			return ExtraInformations.extraFileNameEnglish;
		} else {
			return "";
		}
	}

	// private Integer checkIfPositionOfWordIsWithinBoxBrackets(Integer pos,
	// String tmpLine) throws Exception
	// {
	// if (intPointerBracketsBox==(posBracketsBox.size()))
	// return pos;
	//
	// Integer posOfFoundedWord = -1;
	// Integer posRemainingText = pos;
	// Integer posSummedUp ;
	// Integer posNew;
	//
	// if (posBracketsBox.get(intPointerBracketsBox).getStartPoint() > pos )
	// {
	// return pos ;
	// }
	// // wenn er genau drin liegt, neu suchen
	// else if (posBracketsBox.get(intPointerBracketsBox).getStartPoint() <= pos
	// && posBracketsBox.get(intPointerBracketsBox).getEndPoint() >= pos)
	// {
	// // rekursiv
	//
	// pos++;
	// posNew = wikiOrigText.substring(pos,
	// wikiOrigText.length()).indexOf(tmpLine);
	//
	// if (posNew > -1)
	// {
	// posSummedUp = posRemainingText + posNew + 1;
	// posOfFoundedWord = checkIfPositionOfWordIsWithinBoxBrackets(posSummedUp,
	// tmpLine);
	//
	// }else
	// {
	// posOfFoundedWord = -1;
	// }
	// }
	// else if (posBracketsBox.get(intPointerBracketsBox).getEndPoint() < pos )
	// {
	// if (intPointerBracketsBox < posBracketsBox.size()-1)
	// {
	// Integer tmp = intPointerBracketsBox + 1;
	// if (posBracketsBox.get(tmp).getStartPoint() >= pos)
	// {
	// intPointerBracketsBox++;
	// posOfFoundedWord = pos;
	// }
	// // rekursiv
	// else if (posBracketsBox.get(tmp).getStartPoint() < pos )
	// {
	// intPointerBracketsBox++;
	// posOfFoundedWord = checkIfPositionOfWordIsWithinBoxBrackets(pos,
	// tmpLine);
	// }
	// }
	// // else
	// // {
	// //
	// // }
	//
	// }
	//
	// if (posOfFoundedWord != -1 )
	// {
	// return posOfFoundedWord;
	// }
	// else
	// {
	// return pos;
	// }
	//
	//
	// //// /*
	// //// pointer muss immer in richtiger und aktueller Position sein
	// //// d.h. wenn die gefundene Position innerhalb eines Klammernbereiches
	// liegt,
	// //// dann wird temporär der vorherige Bereich angeguckt und überprüft,
	// //// ob die Position nicht innerhalb eines Klammernbereiches liegt,
	// //// wenn dies so ist, dann Treffer und return,
	// //// rekursiv
	// //// */
	//
	// }

	private Integer checkIfFoundedPositionIsWithinABracketNaviagbleSet(Integer pos, String tmpLine) {
		return checkIfFoundedPositionIsWithinABracketNaviagbleSet(pos, tmpLine, 0);
	}

	/**
	 * 
	 * 
	 * @param counter
	 *            um Endlosschleife zu vermeiden, 10 Versuche und dann Abbruch,
	 *            passiert wenn dann nur bei einzelnen Zeichen
	 * @return
	 */
	private Integer checkIfFoundedPositionIsWithinABracketNaviagbleSet(Integer pos, String tmpLine, Integer counter) {

		Integer x;
		Integer fx;
		Boolean bool_within_brackets = false;
		Integer newPos = -1;

		if (counter >= 10) {
			return -1;
		}

		x = bracketsNavigableSet.floor(pos);
		fx = bracketPositionsHashMap.get(x);

		if (x == null || fx == null) {
			return pos;
		}

		if (x <= pos && pos <= fx) {
			bool_within_brackets = true;
		}

		if (bool_within_brackets) {

			newPos = wikiOrigText.substring(fx).indexOf(tmpLine);

			// rekursion
			// if (newPos == 0 ) {
			// if (tmpLine.length()>1){
			// newPos =
			// checkIfFoundedPositionIsWithinABracketNaviagbleSet(newPos + fx,
			// tmpLine, counter+1);
			// }else {
			// newPos = -1;
			// }
			// } else
			if (newPos > -1) {
				newPos = checkIfFoundedPositionIsWithinABracketNaviagbleSet(newPos + fx, tmpLine, counter + 1);
				// } else {
				// return pos;
			}

			// } else {
			// return pos;
		}

		if (newPos != -1) {
			return newPos;
		} else {
			return pos;
		}
	}

	private void getBracketsPositionAndPutIntoNavigableSet() {

		try {
			bracketPositionsHashMap = new HashMap<Integer, Integer>();
			bracketsNavigableSet = new TreeSet<Integer>();

			bp = new BracketPositions(wikiOrigText);
			bracketPositionsHashMap = bp.getBracketPositionWhereTheLinkTargetIsAsHashMap();

			for (Integer e : bracketPositionsHashMap.keySet()) {
				bracketsNavigableSet.add(e);
			}
		} catch (Exception e) {

			try {
				bwlogger.append("getBracketsPositionAndPutIntoNavigableSet: failure " + e.getMessage() + ", "
						+ e.getCause() + " " + e.getClass() + "\n");
			} catch (IOException e1) {
				System.err.println("bwlogger error");
			}

		}
	}

	/*
	 * verschachtelte Links machen Probleme, Lösung: Bildlink erkennen, wenn das
	 * vorkommt, dann wird der äussere "Bildlinks" deaktiviert, während mögliche
	 * Links innerhalb der Bildbeschreibung aktiviert werden könnten,
	 */
	// private void putBracketPositionsIntoHashMap() {
	// Integer posBracketStarts = 0;
	// Boolean boolBracketOpen = false;
	// Boolean boolHasPipe = false;
	// Integer bracketsCounter = 0;
	//
	// LinkedList<Integer> pipeList = new LinkedList<Integer>();
	//
	// // um vorne und hinten anzufügen, bei gerader Anzahl sind die jeweiligen
	// // Endelemente zusammengehörig
	// // erste und letzte Klammern werden weggelassen, dh. nur
	// // eingeschlossene Klammern
	// Deque<Integer> pipedLinks = new LinkedList<Integer>();
	//
	// // length -1, because char at i + 1
	// for (Integer i = 0; i < wikiOrigText.length() - 1; i++) {
	//
	// ;
	//
	// if (wikiOrigText.charAt(i) == '[' && wikiOrigText.charAt(i + 1) == '[') {
	// if (!boolBracketOpen) {
	// posBracketStarts = i;
	// } else {
	// pipedLinks.add(i);
	// }
	//
	// boolBracketOpen = true;
	// bracketsCounter++;
	//
	// } else if (wikiOrigText.charAt(i) == ']' && wikiOrigText.charAt(i + 1) ==
	// ']') {
	//
	// if (boolBracketOpen) {
	//
	// if (bracketsCounter > 1) {
	// pipedLinks.addLast(i);
	// bracketsCounter--;
	// } else if (boolHasPipe && pipeList.size() == 1 && bracketsCounter == 1 &&
	// pipedLinks.size() == 0) {
	// // 100 % correct link, with only one pipe
	// bracketPositionsHashMap.put(posBracketStarts, pipeList.getFirst()); //
	// link-target
	// boolBracketOpen = false;
	// } else {
	//
	// // wenigstens Behandlung der inneren Links
	// if (pipedLinks.size() > 0) {
	//
	// Boolean boolInsertPipedLinksIntoBracketsPositionsHashMap = false;
	// if (pipedLinks.size() % 2 == 0) {
	// boolInsertPipedLinksIntoBracketsPositionsHashMap = true;
	// } else if (pipedLinks.size() % 2 == 1) {
	// pipedLinks.pollFirst();
	// boolInsertPipedLinksIntoBracketsPositionsHashMap = true;
	// }
	//
	// if (boolInsertPipedLinksIntoBracketsPositionsHashMap) {
	// while (pipedLinks.size() > 0) {
	// bracketPositionsHashMap.put(pipedLinks.pollFirst(),
	// pipedLinks.pollLast());
	// }
	// boolInsertPipedLinksIntoBracketsPositionsHashMap = false;
	// }
	//
	// }
	//
	// // Behandlung der äusseren Klammer abschließen
	// if (bracketsCounter == 1 && boolHasPipe) {
	// // do nothing, or there will be failures, because
	// // the "linkparts" must be found, they were parsed
	// // as normal text
	//
	// boolBracketOpen = false;
	// }
	//
	// }
	//
	// // } else if (boolHasPipe && pipeList.size() > 1
	// // && bracketsCounter == 1)
	// // {
	// //
	// //
	// // if (pipedLinks.size()>1){
	// // // hier ist mindestens ein Link eingeschlossen
	// //
	// //
	// //
	// // }
	// //
	// // bracketPositionsHashMap.put(posBracketStarts, i);
	// // boolBracketOpen = false;
	// // // FIXME hier werden wohl erstmal alle Bilder
	// // // geblockt...
	// //
	//
	// if (!boolBracketOpen) {
	// pipeList.clear();
	// boolHasPipe = false;
	// pipedLinks.clear();
	// bracketsCounter = 0;
	// }
	//
	// }
	// } else if (boolBracketOpen && wikiOrigText.charAt(i) == '|') {
	// boolHasPipe = true;
	// pipeList.add(i);
	//
	// }
	//
	// }
	// }

	// -------------------------

	// save positions of linktargets , between boxbracket and | , not the
	// linktext
	// pictures could have "|" too
	// private List <PointInteger> getBracketedPositionsBoxBrackets()
	// {
	// Integer tmpx = 0;
	// Boolean boolBracketOpen = false;
	//
	// List <PointInteger> list = new ArrayList<PointInteger>();
	//
	// // rather a test
	// if (-1 == wikiOrigText.indexOf("[[", 0)){
	// intPointerBracketsBox = 0 ;
	// return list;
	// }
	//
	//
	// // length -1, because char at i + 1
	// for (Integer i = 0 ; i < wikiOrigText.length()-1; i++)
	// {
	//
	// if (wikiOrigText.charAt(i) == '[' && wikiOrigText.charAt(i + 1) == '[')
	// {
	// tmpx = i;
	// boolBracketOpen = true;
	// }
	// else if (wikiOrigText.charAt(i) == '|')
	// {
	// if (boolBracketOpen)
	// {
	// list.add(new PointInteger(tmpx, i));
	// boolBracketOpen = false;
	// }
	// }
	// }
	//
	// intPointerBracketsBox = 0;
	// return list;
	// }

	private Integer splitForOutputAndReturnNewPosition(String tmpLine, Integer posOfLineInOriginalText) {
		Integer endToken = -1;
		Integer startToken = 0;

		while (startToken < tmpLine.length()) {
			for (startToken = endToken + 1; startToken < tmpLine.length(); startToken++) {
				if (Character.toString(tmpLine.charAt(startToken)).matches("\\p{L}"))
					break;
			}
			for (endToken = startToken; endToken < tmpLine.length(); endToken++) {
				if (Character.toString(tmpLine.charAt(endToken)).matches("\\P{L}"))
					break;
			}
			if (startToken < tmpLine.length()) {

				// only words with length greater than 1 are parsed
				if (tmpLine.substring(startToken, endToken).length() > 1) {
					tokensParsedText.add(tmpLine.substring(startToken, endToken));
					startPositionsWikiText.add(startToken + posOfLineInOriginalText);
				}
			}
		}
		return posOfLineInOriginalText + tmpLine.length();
	}

	private void startTokenizing() throws Exception {

		tokenize();
		tokenizeReadableText(wikiParsedTextReadable);

	}

	public String getCSV() throws Exception {

		startTokenizing();

		StringBuilder sb = new StringBuilder();

		Iterator<String> itToken = tokensParsedText.iterator();
		Iterator<Integer> itSeqWikitext = startPositionsWikiText.iterator();
		// Iterator<Integer> itSeqReadable =
		// startPositionsReadableText.iterator();

		Iterator<String> itTokenReadable = tokensParsedReadableText.iterator();
		Iterator<Integer> itSeqReadableNew = startPositionsReadableTextNew.iterator();

		String tokenReadable = "";
		String token;
		Integer seqWikitext;
		// Integer seqReadable ; //kann wohl weg
		Integer seqReadableNew = -1;
		Integer seqReadableForSave = -1;

		Boolean boolTokenEquals = false;

		String oldReadable;
		Integer oldSeqReadable;
		// Vorbereitung, fuer ungleiches parsen, also wenn links nicht im
		// normalen Text
		// geladen werden

		// init the values
		if (itTokenReadable.hasNext() && itSeqReadableNew.hasNext()) {
			tokenReadable = itTokenReadable.next();
			seqReadableNew = itSeqReadableNew.next();
		}

		while (itToken.hasNext() && itSeqWikitext.hasNext()) {

			token = itToken.next();
			seqWikitext = itSeqWikitext.next();
			// seqReadable = itSeqReadable.next();

			if (token.equals(tokenReadable)) {
				boolTokenEquals = true;
			} else if (tokenReadable.startsWith(token)) { // for links with
															// postfixes , they
															// are not equal,
				// TODO vielleicht prozentual was machen? aba dürfte nur links
				// betreffen
				boolTokenEquals = true;
			} else {
				boolTokenEquals = false;
				seqReadableForSave = -1;
			}

			if (boolTokenEquals) {
				seqReadableForSave = seqReadableNew;

				// switch to next record from readable text
				if (itTokenReadable.hasNext() && itSeqReadableNew.hasNext()) {
					tokenReadable = itTokenReadable.next();
					seqReadableNew = itSeqReadableNew.next();
				}
			}

			// System.out.println(token);

			sb.append("\"" + old_id + "\";" + "\"" + String.valueOf(seqReadableForSave) + "\";" + "\""
					+ token.toLowerCase() + "\";" + "\"" + token + "\";" + "\"" + String.valueOf(seqWikitext) + "\""
					+ "\n");

			// Iterator<String> itToken = tokensParsedText.iterator();
			// Iterator<Integer> itSeqWikitext =
			// startPositionsWikiText.iterator();
			// Iterator<Integer> itSeqReadable =
			// startPositionsReadableText.iterator();
			//
			// while (itToken.hasNext() && itSeqWikitext.hasNext()) {
			// String token = itToken.next();
			// Integer seqWikitext = itSeqWikitext.next();
			// Integer seqReadable = itSeqReadable.next();
			//
			// sb.append("\"" + old_id + "\";" + "\"" +
			// String.valueOf(seqReadable) + "\";" + "\"" + token.toLowerCase()
			// + "\";" + "\"" + token + "\";" + "\"" +
			// String.valueOf(seqWikitext) + "\"" + "\n");

			// temp assert which is working
			// if (seq > -1 ){
			// Assert.assertEquals(token, wikiOrigText.substring(seq, seq+
			// token.length()));
			// }
		}

		return sb.toString();

	}

	// private HashMap<String, Boolean> getHashmapTokens(){
	//
	// Set<String> map = new Set<String>();
	//
	// Iterator<String> itToken = tokensParsedText.iterator();
	// String token;
	//
	// while (itToken.hasNext()){
	// token = itToken.next();
	// map
	// }
	//
	//
	// }

	public String getLinkInfos() throws Exception {

		startTokenizing();
		StringBuilder sb = new StringBuilder();

		List<LinkElement> list = bp.getLinkElementListOfAllLinks();

		for (LinkElement e : list) {
			// if (e.getLinkText())
			System.out.println(e.getInfosSeparatedInColumns());
			sb.append(e.getInfosSeparatedInColumns() + "\n");
		}

		return sb.toString();
	}

}
