package tools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import wikiParser.PreMalletParallelization;
import wikiParser.SupporterForBothTypes;

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
	private final String wikiParsedTextLineByLine;
	private final Integer old_id;
	private final String wikiTitle;
	private final String wikiParsedTextReadable;

	private List<String> tokensParsedText = new ArrayList<String>();
	private List<Integer> startPositionsWikiText = new ArrayList<Integer>();

	private List<String> tokensParsedReadableText = new ArrayList<String>();
	private List<Integer> startPositionsReadableTextNew = new ArrayList<Integer>();

	// private List<PointInteger> posBracketsBox;
	// private Integer intPointerBracketsBox;

	private BufferedWriter bwlogger;

	private HashMap<Integer, Integer> bracketPositionsHashMap;
	private NavigableSet<Integer> bracketsNavigableSet;

	private String extraNextToLeftElement = "";
	private Integer extraSectionLevel = 0;
	private boolean isPicture = false;

	private HashMap<Integer, SectionElement> sectionCaptionPosition = new HashMap<Integer, SectionElement>();
	private TreeMap<Integer, PictureElement> picturePosition = new TreeMap<Integer, PictureElement>();

	private BracketPositions bp;
	private ArrayList<TokenElement> tokenList;

	private HashMap<Integer, Integer> mapOfPositionInOrigWikiTextAndListPositionInJoinedTokens;

	private final boolean onlyParsedLinks;
	private final boolean debug;

	public WikiTextToCSVForeward(WikiArticle w, BufferedWriter bwLogger, Properties prop) {

		this.wikiOrigText = new String(w.getWikiOrigText());
		this.wikiParsedTextLineByLine = w.getParsedWikiText();
		this.old_id = w.getOldID();
		this.wikiTitle = w.getWikiTitle();
		this.wikiParsedTextReadable = w.getParsedWikiTextReadable();

		if (bwlogger != null) {
			this.bwlogger = bwLogger;
		}

		onlyParsedLinks = prop.getProperty("Wiki_onlyParsedLinks").equalsIgnoreCase("true");
		debug = prop.getProperty("Wiki_debug").equalsIgnoreCase("true");

		init();

	}

	// public WikiTextToCSVForeward(WikiArticle w, BufferedWriter bwLogger) {
	// this(w);
	// this.bwlogger = bwLogger;
	// }

	private void init() {
		try {
			startTokenizing();
		} catch (Exception e) {
			System.err.println("Fehler in " + this.getClass());

			try {
				if (bwlogger != null) {
					bwlogger.append("Fehler in " + this.getClass() + "\n");
				}
			} catch (IOException e1) {
				System.err.println("kein logger in " + this.getClass() + " definiert.");
			}
			e.printStackTrace();
		}
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

		Scanner scParsed = new Scanner(wikiParsedTextLineByLine); // liegt
																	// zeilenweise
																	// vor
		Integer savedPosition = 0;
		Integer pos = -1;
		String tmpLineWithoutExtraInfo;
		Boolean bool_found = false;
		Boolean bool_dont_shift_position = false;
		Integer shifting = 0;
		String tmpExtraInfoTextForControll;
		Boolean bool_from_saved_position = false;

		while (scParsed.hasNextLine()) {
			// read line
			String tmpLine = scParsed.nextLine();
			if (tmpLine.trim().length() > 0) {
				// find the line in the original wiki-text

				tmpLineWithoutExtraInfo = getNormalTextIfExtraInfosAddedToTheEnd(tmpLine);
				bool_dont_shift_position = getBoolShift(tmpLine);

				// System.err.println(tmpLineWithoutExtraInfo + " " +
				// savedPosition);

				// System.err.println(wikiOrigText.substring(savedPosition,
				// savedPosition + tmpLineWithoutExtraInfo.length())
				// + " "
				// + tmpLineWithoutExtraInfo
				// + " "
				// + pos
				// + " "
				// +
				// wikiOrigText.substring(0).indexOf(tmpLineWithoutExtraInfo));

				if (tmpLineWithoutExtraInfo.length() > 0) {
					pos = wikiOrigText.substring(savedPosition).indexOf(tmpLineWithoutExtraInfo);
				} else {
					pos = -1;
				}

				if (pos > -1) {

					// System.err.println(pos + " " + tmpLine + " old ");
					// bwlogger.append(tmpLine + "\n");
					// bwlogger.flush();

					try {

						// pos =
						// checkIfFoundedPositionIsWithinABracketNaviagbleSet(pos
						// + savedPosition,
						// tmpLineWithoutExtraInfo);

						while (true) {

							if (bool_from_saved_position) {
								bool_from_saved_position = false;
								pos = pos + savedPosition;
							}

							pos = checkIfFoundedPositionIsWithinABracketNaviagbleSet(pos, tmpLineWithoutExtraInfo);

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

								if (pos - shifting < 0) {
									tmpExtraInfoTextForControll = wikiOrigText.substring(pos, pos
											+ tmpLineWithoutExtraInfo.length() + extraNextToLeftElement.length());

								} else if (wikiOrigText.length() >= (pos + tmpLineWithoutExtraInfo.length() + extraNextToLeftElement
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
								bool_found = false;
								break;
							} else if (pos == -1) {

								if (bwlogger != null) {
									bwlogger.append("test:failling in tokenize " + wikiTitle + " " + old_id + "\n");
								}

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

						// passiert nur bei Kleinigkeiten
						if (bwlogger != null) {
							bwlogger.append("fehler in tokenize " + this.wikiTitle + " " + this.old_id + " "
									+ e.getMessage() + "\n");
						}

						// System.err.println("fehler in tokenize " +
						// this.wikiTitle + " " + this.old_id + " "
						// + e.getMessage());

						// e.printStackTrace();
						// System.exit(25);
					}

					// System.out.println(pos + " " + tmpLine + " new ");

					if (tmpLine.contains(ExtraInformations.extraSectionCaptionAppend)) {

						// führende Leerzeichen müssen gezählt und entfernt
						// werden, weil es sonst zu Problemen beim Verbinden der
						// zwei Ausgaben kommen kann, Leerzeichen müssen bis
						// hierhin (von TextConverter kommend) erhalten bleiben,
						// weil sonst Kontrolle mit ExtraNextToLeftElement nicht
						// korrekt dem SectionLevel entsprechend geprüft werden
						// kann

						Integer posSectionStart = getNewPositionWithoutBeginningSpaces(tmpLineWithoutExtraInfo, pos);

						sectionCaptionPosition.put(posSectionStart, new SectionElement(tmpLineWithoutExtraInfo.trim(),
								posSectionStart, extraSectionLevel, old_id));
					} else if (isPicture) {

						picturePosition.put(pos, new PictureElement(tmpLineWithoutExtraInfo, pos, old_id));

						isPicture = false;
					}

					// takes the last position of the line, iterates all
					// elements, saves the position of every token from the text
					// and returns the new startposition (the last )

					savedPosition = splitForOutputAndReturnNewPosition(tmpLineWithoutExtraInfo, pos);
					bool_from_saved_position = true;

				} else {
					// error output to logger

					if (tmpLineWithoutExtraInfo.length() > 0) {
						if (bwlogger != null) {
							bwlogger.append("catched error, " + wikiTitle + " *" + tmpLineWithoutExtraInfo + "*:" + pos
									+ ": last_savedposition= " + savedPosition + " id:" + old_id + "\n");
						}
					}
				}
			} else {
				savedPosition = savedPosition + 1; // new line also counts as
													// one character in utf-8
				bool_from_saved_position = true; // neu 20131205
			}
		}
		scParsed = null;
	}

	private Integer getNewPositionWithoutBeginningSpaces(String tmpLineWithoutExtraInfo, Integer pos) {
		Integer output = pos;

		String space = " ";
		Integer counter = 0;

		while (tmpLineWithoutExtraInfo.substring(counter).startsWith(space)) {
			counter++;
		}

		return output + counter;
	}

	private String getNormalTextIfExtraInfosAddedToTheEnd(String tmpLine) {
		return getNormalTextIfExtraInfosAddedToTheEnd(tmpLine, true);
	}

	/*
	 * extrapicture 3 und 5 werden wohl nicht merh verwendet
	 */
	private String getNormalTextIfExtraInfosAddedToTheEnd(String tmpLine, Boolean changeExtraElement) {

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
			isPicture = true;
		} else if (tmpLine.endsWith(ExtraInformations.extraPicure2Append)) {
			output = tmpLine.replace(ExtraInformations.extraPicure2Append, "");
			extraNextToLeftElement = "|";
			isPicture = true;
		} else if (tmpLine.endsWith(ExtraInformations.extraPicure3Append)) {
			output = tmpLine.replace(ExtraInformations.extraPicure3Append, "");
			extraNextToLeftElement = getExtraNextToElementForPictures(tmpLine);
			isPicture = true;

		} else if (tmpLine.endsWith(ExtraInformations.extraPicure4Append)) {
			output = tmpLine.replace(ExtraInformations.extraPicure4Append, "");
			extraNextToLeftElement = getExtraNextToElementForPictures(tmpLine);
			isPicture = true;

		} else if (tmpLine.endsWith(ExtraInformations.extraPicure5Append)) {
			output = tmpLine.replace(ExtraInformations.extraPicure5Append, "");
			extraNextToLeftElement = "|";
			// isPicture = true; // eigentlich kein Bild sondern nur der Text

		} else if (tmpLine.contains(ExtraInformations.extraSectionCaptionAppend)) {

			Integer tmp = tmpLine.indexOf(ExtraInformations.extraSectionCaptionAppend);
			output = tmpLine.substring(0, tmp);

			extraSectionLevel = getExtraSectionLevelAsInteger(tmpLine);
			extraNextToLeftElement = getExtraNextToElementForSectionsLevel(tmpLine);

			// extraNextToLeftElement = "";

		} else {
			extraNextToLeftElement = "";
			extraSectionLevel = -1;
			isPicture = false;
			output = tmpLine;
		}
		return output;
	}

	private Integer getExtraSectionLevelAsInteger(String tmpLine) {

		Integer tmp = tmpLine.indexOf(ExtraInformations.extraSectionLevelStart);
		String tmpString = tmpLine.substring(tmp + ExtraInformations.extraSectionLevelStart.length());
		tmpString = tmpString.replace(ExtraInformations.extraSectionLevelEnd, "");

		return Integer.valueOf(tmpString);
	}

	private String getExtraNextToElementForSectionsLevel(String tmpLine) {

		Integer intLevel = getExtraSectionLevelAsInteger(tmpLine);

		String output = "";
		for (Integer i = 0; i < intLevel; i++) {
			output = output + "=";
		}
		return output;
	}

	/**
	 * because [[Datei:...]] or [[File:...]] is within the string, no shifting
	 * is needed TODO erweitern mit allen möglichen Bildern die geshifftet
	 * werden können bzw müssen
	 */
	private boolean getBoolShift(String tmpLine) {
		if (tmpLine.endsWith(ExtraInformations.extraPicure1Append)
				|| tmpLine.endsWith(ExtraInformations.extraPicure3Append)
				|| tmpLine.endsWith(ExtraInformations.extraPicure4Append)) {
			return true;
		} else if (tmpLine.endsWith(ExtraInformations.extraPicure2Append)
				|| tmpLine.endsWith(ExtraInformations.extraPicure5Append)) {
			return false;
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
			SupporterForBothTypes s = new SupporterForBothTypes();
			bracketPositionsHashMap = new HashMap<Integer, Integer>(s.getNumberOfElementsForGettingCapacity(
					wikiOrigText, "[["));
			s = null;
			bracketsNavigableSet = new TreeSet<Integer>();

			bp = new BracketPositions(wikiOrigText, old_id, wikiTitle);
			bracketPositionsHashMap = bp.getBracketPositionWhereTheLinkTargetIsAsHashMap();

			for (Integer e : bracketPositionsHashMap.keySet()) {
				bracketsNavigableSet.add(e);
			}
		} catch (Exception e) {

			try {
				if (bwlogger != null) {
					bwlogger.append("getBracketsPositionAndPutIntoNavigableSet: failure " + e.getMessage() + ", "
							+ e.getCause() + " " + e.getClass() + "\n");
				}
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
	// // // F I X M E hier werden wohl erstmal alle Bilder
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

		joinTheTwoIteratorsAndFillList();

	}

	public String getCSV() throws Exception {

		// joinTheTwoIteratorsAndFillList();
		return getMalletInput();

		// startTokenizing();

		// StringBuilder sb = new StringBuilder();
		//
		// Iterator<String> itToken = tokensParsedText.iterator();
		// Iterator<Integer> itSeqWikitext = startPositionsWikiText.iterator();
		// // Iterator<Integer> itSeqReadable =
		// // startPositionsReadableText.iterator();
		//
		// Iterator<String> itTokenReadable =
		// tokensParsedReadableText.iterator();
		// Iterator<Integer> itSeqReadableNew =
		// startPositionsReadableTextNew.iterator();
		//
		// String tokenReadable = "";
		// String token;
		// Integer seqWikitext;
		// // Integer seqReadable ; //kann wohl weg
		// Integer seqReadableNew = -1;
		// Integer seqReadableForSave = -1;
		//
		// Boolean boolTokenEquals = false;
		//
		// String oldReadable;
		// Integer oldSeqReadable;
		// // Vorbereitung, fuer ungleiches parsen, also wenn links nicht im
		// // normalen Text
		// // geladen werden
		//
		// // init the values
		// if (itTokenReadable.hasNext() && itSeqReadableNew.hasNext()) {
		// tokenReadable = itTokenReadable.next();
		// seqReadableNew = itSeqReadableNew.next();
		// }
		//
		// while (itToken.hasNext() && itSeqWikitext.hasNext()) {
		//
		// token = itToken.next();
		// seqWikitext = itSeqWikitext.next();
		// // seqReadable = itSeqReadable.next();
		//
		// if (token.equals(tokenReadable)) {
		// boolTokenEquals = true;
		// } else if (tokenReadable.startsWith(token)) { // for links with
		// // postfixes , they
		// // are not equal,
		// // T O D O vielleicht prozentual was machen? aba dürfte nur
		// // links
		// // betreffen
		// boolTokenEquals = true;
		// } else {
		// boolTokenEquals = false;
		// seqReadableForSave = -1;
		// }
		//
		// if (boolTokenEquals) {
		// seqReadableForSave = seqReadableNew;
		//
		// // switch to next record from readable text
		// if (itTokenReadable.hasNext() && itSeqReadableNew.hasNext()) {
		// tokenReadable = itTokenReadable.next();
		// seqReadableNew = itSeqReadableNew.next();
		// }
		// }
		//
		// // System.out.println(token);
		//
		// sb.append("\"" + old_id + "\";" + "\"" +
		// String.valueOf(seqReadableForSave) + "\";" + "\""
		// + token.toLowerCase() + "\";" + "\"" + token + "\";" + "\"" +
		// String.valueOf(seqWikitext) + "\""
		// + "\n");
		//
		// // Iterator<String> itToken = tokensParsedText.iterator();
		// // Iterator<Integer> itSeqWikitext =
		// // startPositionsWikiText.iterator();
		// // Iterator<Integer> itSeqReadable =
		// // startPositionsReadableText.iterator();
		// //
		// // while (itToken.hasNext() && itSeqWikitext.hasNext()) {
		// // String token = itToken.next();
		// // Integer seqWikitext = itSeqWikitext.next();
		// // Integer seqReadable = itSeqReadable.next();
		// //
		// // sb.append("\"" + old_id + "\";" + "\"" +
		// // String.valueOf(seqReadable) + "\";" + "\"" + token.toLowerCase()
		// // + "\";" + "\"" + token + "\";" + "\"" +
		// // String.valueOf(seqWikitext) + "\"" + "\n");
		//
		// // temp assert which is working
		// // if (seq > -1 ){
		// // Assert.assertEquals(token, wikiOrigText.substring(seq, seq+
		// // token.length()));
		// // }
		// }
		//
		// return sb.toString();

	}

	public String getOrgTableString() {
		return this.old_id + " ;\"" + this.wikiTitle + " \";\"" + this.wikiParsedTextReadable + " \""
				+ PreMalletParallelization.endOfDocumentInSQLOutput;
	}

	private Set<String> getSetOfParsedLines() {

		SupporterForBothTypes s = new SupporterForBothTypes();
		Set<String> set = new HashSet<String>(s.getNumberOfElementsForGettingCapacity(wikiParsedTextLineByLine, "\n"));
		s = null;

		Scanner scParsed = new Scanner(wikiParsedTextLineByLine);
		String token;

		while (scParsed.hasNextLine()) {
			token = scParsed.nextLine();
			token = getNormalTextIfExtraInfosAddedToTheEnd(token, false);

			set.add(token);
			token = null;
		}

		scParsed = null;

		return set;
	}

	public String getLinkInfos() {

		if (mapOfPositionInOrigWikiTextAndListPositionInJoinedTokens == null) {
			generateMapPositionsInOrigTextAndListPositions();
		}

		StringBuilder sb = new StringBuilder();
		List<LinkElement> listOfLinks = bp.getLinkElementlistOfAllLinks();

		// im Link ist die Position von wikiText enthalten, damit kann die
		// Verbindung zum geparsten Text hergestellt werden

		for (LinkElement e : listOfLinks) {
			// System.out.println(e.getInfosSeparatedInColumns());

			Integer i = mapOfPositionInOrigWikiTextAndListPositionInJoinedTokens.get(e.getWikiTextStartPosition());
			if (i != null) {

				// Link wird extra bzw anders erstellt, dh. er muss noch mit
				// Positionsangabe verbunden werden, falls eine existiert, es
				// kann keine Positionsangabe geben, wenn das Wort nicht geparst
				// wurde
				e.setParsedTextPoint(tokenList.get(i).getPosReadableTextPointInteger());
			}

			// wenn nur die geparsten Links ausgegeben werden sollen dann muss
			// es einen Joinpartner, d.h. die posReadable geben >=0,
			// anderenfalls werden alle Links ausgegeben
			if (onlyParsedLinks && e.getParsedTextStartPosition() >= 0) {
				sb.append(e.getInfosSeparatedInColumns() + "\n");
			} else if (!onlyParsedLinks) {
				sb.append(e.getInfosSeparatedInColumns() + "\n");
			}

		}

		return sb.toString();
	}

	public String getCategroryInfos() {

		StringBuilder sb = new StringBuilder();
		List<CategoryElement> listOfLinks = bp.getCategoryLinkList();

		// im Link ist die Position von wikiText enthalten, damit kann die
		// Verbindung zum geparsten Text hergestellt werden

		for (CategoryElement e : listOfLinks) {
			sb.append(e.getInfosSeparatedInColumns() + "\n");
		}

		return sb.toString();
	}

	private void joinTheTwoIteratorsAndFillList() {

		tokenList = new ArrayList<TokenElement>();
		TokenElement te;

		Iterator<String> itToken = tokensParsedText.iterator();
		Iterator<Integer> itSeqWikitext = startPositionsWikiText.iterator();

		Iterator<String> itTokenReadable = tokensParsedReadableText.iterator();
		Iterator<Integer> itSeqReadableNew = startPositionsReadableTextNew.iterator();

		String tokenReadable = "";
		String token;
		Integer seqWikitext;
		Integer seqReadableNew = -1;
		Integer seqReadableForSave = -1;

		Boolean boolTokenEquals = false;

		// init the values
		if (itTokenReadable.hasNext() && itSeqReadableNew.hasNext()) {
			tokenReadable = itTokenReadable.next();
			seqReadableNew = itSeqReadableNew.next();
		}

		while (itToken.hasNext() && itSeqWikitext.hasNext()) {

			token = itToken.next();
			seqWikitext = itSeqWikitext.next();

			if (token.equals(tokenReadable)) {
				boolTokenEquals = true;
			} else if (tokenReadable.startsWith(token)) { // for links with
															// postfixes , they
															// are not equal,
				// T O D O vielleicht prozentual was machen? aba dürfte nur
				// links
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

			te = new TokenElement(token.toLowerCase(), token, seqReadableForSave, seqWikitext);
			tokenList.add(te);
		}
	}

	private void generateMapPositionsInOrigTextAndListPositions() {
		mapOfPositionInOrigWikiTextAndListPositionInJoinedTokens = new HashMap<Integer, Integer>(tokenList.size());

		TokenElement tokenElement;
		for (Integer k = 0; k < tokenList.size(); k++) {

			tokenElement = tokenList.get(k);
			mapOfPositionInOrigWikiTextAndListPositionInJoinedTokens.put(tokenElement.getPosWikitext(), k);

			// System.out.println(tokenElement.getPosWikitext() + " " +
			// tokenElement.getTerm());
			// if (k > 10) {
			// return;
			// }
		}

	}

	public String getMalletInput() {

		StringBuilder sb = new StringBuilder();

		for (TokenElement e : tokenList) {

			// z.B. werden Bilder nicht geparst und erzeugen somit -1 Werte
			if (e.getPosReadableText() > -1) {
				sb.append("\"" + old_id + "\";" + "\"" + String.valueOf(e.getPosReadableText()) + "\";" + "\""
						+ e.getToken() + "\";" + "\"" + e.getTerm() + "\";" + "\"" + String.valueOf(e.getPosWikitext())
						+ "\"" + "\n");
			} else {
				if (debug) {
					try {
						if (bwlogger != null) {
							bwlogger.append(old_id + " " + wikiTitle + e.getToken() + " getPosReadableText<0 " + "\n");
						}
					} catch (IOException e1) {
						// e1.printStackTrace();
					}
				}
			}
		}
		return sb.toString();
	}

	public String getSectionCaptions() {

		if (mapOfPositionInOrigWikiTextAndListPositionInJoinedTokens == null) {
			generateMapPositionsInOrigTextAndListPositions();
		}

		StringBuilder sb = new StringBuilder();
		SectionElement s;
		Integer j;
		Integer seqReadable;

		TreeSet<Integer> set = new TreeSet<Integer>();

		for (Integer i : sectionCaptionPosition.keySet()) {
			set.add(i);
		}

		Iterator<Integer> itSet = set.iterator();

		while (itSet.hasNext()) {
			j = itSet.next();

			seqReadable = mapOfPositionInOrigWikiTextAndListPositionInJoinedTokens.get(j);

			if (seqReadable != null) {
				s = sectionCaptionPosition.get(j);
				s.setParsedTextPoint(new PointInteger(seqReadable, new Integer(seqReadable + s.getText().length())));

				sb.append(s.getInfosSeparatedInColumns() + "\n");

				seqReadable = null;
			}
		}

		return sb.toString();
	}

	public String getPictures() {

		if (mapOfPositionInOrigWikiTextAndListPositionInJoinedTokens == null) {
			generateMapPositionsInOrigTextAndListPositions();
		}

		StringBuilder sb = new StringBuilder();
		PictureElement e;
		Integer seqReadable;

		for (Integer i : picturePosition.keySet()) {
			e = picturePosition.get(i);

			seqReadable = mapOfPositionInOrigWikiTextAndListPositionInJoinedTokens.get(e.getWikiTextStartPosition());
			if (null != seqReadable) {
				seqReadable = mapOfPositionInOrigWikiTextAndListPositionInJoinedTokens
						.get(e.getWikiTextStartPosition());
				e.setParsedTextPoint(new PointInteger(seqReadable, new Integer(seqReadable + e.getText().length())));
				sb.append(e.getInfosSeparatedInColumns() + "\n");

				seqReadable = null;
			}
		}

		return sb.toString();
	}

	public LinkedList<String> getCateroryListAsString() {

		List<CategoryElement> listOfLinks = bp.getCategoryLinkList();
		LinkedList<String> list = new LinkedList<String>();

		// im Link ist die Position von wikiText enthalten, damit kann die
		// Verbindung zum geparsten Text hergestellt werden

		for (CategoryElement e : listOfLinks) {
			list.add(ExtraInformations.getTargetWithoutCategoryInformation(e.getText()));
		}

		return list;
	}
}
