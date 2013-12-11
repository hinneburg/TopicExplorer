package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.TreeMap;

import wikiParser.SupporterForBothTypes;

public class BracketPositions {

	private final String wikiOrigText;
	private HashMap<Integer, Integer> bracketPositionsHashMapLinksStartTillEnd;
	private HashMap<Integer, Integer> bracketPositionsHashMapLinkTargetInclPipePosition;
	private HashMap<Integer, Integer> bracketPositionsNestedLinks;
	private TreeMap<Integer, Integer> bracketPositionsLinksWithoutAnyPipes;

	private NavigableMap<Integer, Integer> bracketPositionsLinkTarget;

	private Integer oldId;
	private String title;

	private HashMap<Integer, Integer> bracketPositionsCategoryLinks;

	public BracketPositions(String wikiText, Integer oldId, String wikiTitle) {
		this.wikiOrigText = wikiText;
		this.oldId = oldId;
		this.title = wikiTitle;
		init();
		putBracketPositionsIntoHashMaps();
	}

	private void init() {

		SupporterForBothTypes s = new SupporterForBothTypes();
		Integer capacity = s.getNumberOfElementsForGettingCapacity(wikiOrigText, "[[");

		bracketPositionsHashMapLinksStartTillEnd = new HashMap<Integer, Integer>(capacity);
		bracketPositionsHashMapLinkTargetInclPipePosition = new HashMap<Integer, Integer>(capacity);
		bracketPositionsNestedLinks = new HashMap<Integer, Integer>();
		bracketPositionsLinksWithoutAnyPipes = new TreeMap<Integer, Integer>();
		bracketPositionsLinkTarget = new TreeMap<Integer, Integer>();

		bracketPositionsCategoryLinks = new HashMap<Integer, Integer>();
	}

	public HashMap<Integer, Integer> getBracketPositionWhereTheLinkTargetIsAsHashMap() {
		return bracketPositionsHashMapLinkTargetInclPipePosition;
	}

	private void putBracketPositionsIntoHashMaps() {
		Integer posBracketStarts = 0;
		Boolean boolBracketOpen = false;
		Boolean boolHasPipe = false;
		Integer bracketsCounter = 0;

		LinkedList<Integer> pipeList = new LinkedList<Integer>();

		// um vorne und hinten anzufügen, bei gerader Anzahl sind die jeweiligen
		// Endelemente zusammengehörig
		// erste und letzte Klammern werden weggelassen, dh. nur
		// eingeschlossene Klammern
		// Deque<Integer> pipedLinks = new LinkedList<Integer>();

		Queue<Integer> nestedStart = new LinkedList<Integer>();
		Queue<Integer> nestedEnd = new LinkedList<Integer>();

		// length -1, because char at i + 1
		for (Integer i = 0; i < wikiOrigText.length() - 1; i++) {

			if (wikiOrigText.charAt(i) == '[' && wikiOrigText.charAt(i + 1) == '[') {
				if (!boolBracketOpen) {
					posBracketStarts = i + 2;
				} else {
					// pipedLinks.add(i);
					nestedStart.add(i + 2);
				}

				boolBracketOpen = true;
				bracketsCounter++;

			} else if (wikiOrigText.charAt(i) == ']' && wikiOrigText.charAt(i + 1) == ']') {

				if (boolBracketOpen) {

					if (bracketsCounter > 1) {
						// pipedLinks.addLast(i);
						nestedEnd.add(i);
						bracketsCounter--;
					} else if (boolHasPipe && pipeList.size() == 1 && bracketsCounter == 1 && nestedStart.size() == 0) {

						// Links als Bilder mit nur einem Pipe erkennen und
						// ignorieren
						String subString = wikiOrigText.substring(posBracketStarts, i);

						if (ExtraInformations.getIsPictureStartsWith(subString)) {
							boolBracketOpen = false;

						} else if (ExtraInformations.getIsCategory(subString)) {
							boolBracketOpen = false;
							// pipe ist für die Sortierung zuständig und stört
							Integer posPipe = subString.indexOf('|');
							if (posPipe > -1) {
								bracketPositionsCategoryLinks.put(posBracketStarts, posPipe + posBracketStarts);
							}
						} else {

							// 100 % correct link, with only one pipe
							bracketPositionsHashMapLinkTargetInclPipePosition
									.put(posBracketStarts, pipeList.getFirst()); // link-target
							bracketPositionsHashMapLinksStartTillEnd.put(posBracketStarts, i);
							boolBracketOpen = false;
						}
						subString = null;
					} else {

						// wenigstens Behandlung der inneren Links
						if (nestedStart.size() > 0) {

							Boolean boolInsertNestedLinksIntoBracketsPositionsHashMap = false;

							if (nestedStart.size() == nestedEnd.size()) {
								// gleich groß, alles i.O.
								boolInsertNestedLinksIntoBracketsPositionsHashMap = true;
							} else if (nestedStart.size() - 1 == nestedEnd.size()) {
								// nicht gleich groß, wahrscheinlich muss der
								// erste weg, weil der nicht geschlossen wird
								nestedStart.remove();
								boolInsertNestedLinksIntoBracketsPositionsHashMap = true;
							}

							if (boolInsertNestedLinksIntoBracketsPositionsHashMap) {

								//

								while (nestedStart.size() > 0) {

									// bisherige Annahme : keine weitere
									// Verschachtelungen mehr, nur auf zu

									Integer start = nestedStart.remove();
									Integer end = nestedEnd.remove();

									// bracketPositionsNestedLinks.put(start,
									// end);

									Integer pipePos = getPipePositionIfOnlyOnePipe(start, end);

									String subString2 = wikiOrigText.substring(start, end);

									if (pipePos > 0) {

										if (ExtraInformations.getIsPictureStartsWith(subString2)) {
											// do nothing
										} else {

											bracketPositionsHashMapLinkTargetInclPipePosition.put(start, pipePos
													+ start);
											bracketPositionsHashMapLinksStartTillEnd.put(start, end);
										}
									} else if (pipePos == -1) {

										if (!ExtraInformations.getIsCategory(subString2)) {
											bracketPositionsLinksWithoutAnyPipes.put(start, end);
										} else {
											// category
											bracketPositionsCategoryLinks.put(posBracketStarts, i);
										}

									}
									subString2 = null;

								}
								boolInsertNestedLinksIntoBracketsPositionsHashMap = false;
							}

						} else if (nestedStart.size() == 0) {
							// ending of normal link without any nested links

							String subString3 = wikiOrigText.substring(posBracketStarts, i);
							if (!boolHasPipe) {

								if (!ExtraInformations.getIsCategory(subString3)) {
									bracketPositionsLinksWithoutAnyPipes.put(posBracketStarts, i);
								} else {
									// category
									bracketPositionsCategoryLinks.put(posBracketStarts, i);
								}

								boolBracketOpen = false;
							} else if (ExtraInformations.getIsCategory(subString3)) {

								Integer posPipe = subString3.indexOf('|');
								if (posPipe > -1) {
									bracketPositionsCategoryLinks.put(posBracketStarts, posPipe + posBracketStarts);
								}
							}
							subString3 = null;
						}

						// Behandlung der äusseren Klammer abschließen bzw.
						// alles zurücksetzen mit boolBracketOpen = false
						if (bracketsCounter == 1 && boolHasPipe) {
							// do nothing, or there will be failures, because
							// the "linkparts" must be found, they were parsed
							// as normal text

							boolBracketOpen = false;
						}
					}

					if (!boolBracketOpen) {
						pipeList.clear();
						boolHasPipe = false;
						// pipedLinks.clear();
						nestedStart.clear();
						nestedEnd.clear();
						bracketsCounter = 0;
					}

				}
			} else if (boolBracketOpen && wikiOrigText.charAt(i) == '|') {
				boolHasPipe = true;
				pipeList.add(i);

			}

		}

		generateTreeMapLinkTargetInclPipe();

	}

	private void generateTreeMapLinkTargetInclPipe() {

		for (Integer i : bracketPositionsHashMapLinkTargetInclPipePosition.keySet()) {
			bracketPositionsLinkTarget.put(i, bracketPositionsHashMapLinkTargetInclPipePosition.get(i));
		}
	}

	/**
	 * 
	 * returns position from the pipe, when there is only one pipe within the
	 * textpositions
	 */
	private Integer getPipePositionIfOnlyOnePipe(Integer start, Integer end) {

		Integer output;
		String tmp = wikiOrigText.substring(start, end);

		output = tmp.indexOf('|');

		if (output > -1 && tmp.substring(output + 1).indexOf('|') > -1) {
			output = -1;
		}
		return output;
	}

	private Boolean checkIfhasNoPipe(Integer start, Integer end) {

		String tmp = wikiOrigText.substring(start, end);

		if (tmp.indexOf('|') > 0) {
			return false;
		} else {
			return true;
		}
	}

	public List<PointInteger> getSortedListOfAllBracketsWithoutPipes() {

		ArrayList<PointInteger> output = new ArrayList<PointInteger>();

		for (Integer i : bracketPositionsLinksWithoutAnyPipes.keySet()) {
			output.add(new PointInteger(i, bracketPositionsLinksWithoutAnyPipes.get(i)));
		}
		return output;
	}

	public List<LinkElement> getLinkElementlistOfAllLinks() {

		ArrayList<LinkElement> output = new ArrayList<LinkElement>(bracketPositionsLinkTarget.size());
		LinkElement l;

		for (Integer i : bracketPositionsLinkTarget.keySet()) {

			l = new LinkElement();
			// System.out.println(i + " " +
			// bracketPositionsHashMapLinkTargetInclPipePosition.get(i) + " "
			// + bracketPositionsHashMapLinksStartTillEnd.get(i) + " "
			// + wikiOrigText.substring(i,
			// bracketPositionsHashMapLinksStartTillEnd.get(i)));

			l.setOldId(oldId);
			l.setCompleteLinkSpan(new PointInteger(i, bracketPositionsHashMapLinksStartTillEnd.get(i)));
			l.setWikiTextPosition(new PointInteger(bracketPositionsLinkTarget.get(i) + 1,
					bracketPositionsHashMapLinksStartTillEnd.get(i))); // eine
																		// Stelle
																		// nach
																		// dem
																		// pipe
			l.setTargetString(wikiOrigText.substring(l.getCompleteLinkStartPosition(), l.getWikiTextStartPosition() - 1)); // sonst
			// ist
			// pipe
			// mit
			// drauf
			l.setText(wikiOrigText.substring(l.getWikiTextStartPosition(), l.getWikiTextEndPosition()));

			// System.out.println(l.getText() + " " + l.getTarget());

			output.add(l);

			l = null;
		}
		return output;
	}

	public List<CategoryElement> getCategoryLinkList() {

		ArrayList<CategoryElement> output = new ArrayList<CategoryElement>(bracketPositionsCategoryLinks.size());
		CategoryElement l;

		for (Integer i : bracketPositionsCategoryLinks.keySet()) {

			l = new CategoryElement();
			// System.out.println(i + " " +
			// bracketPositionsHashMapLinkTargetInclPipePosition.get(i) + " "
			// + bracketPositionsHashMapLinksStartTillEnd.get(i) + " "
			// + wikiOrigText.substring(i,
			// bracketPositionsHashMapLinksStartTillEnd.get(i)));

			l.setOldId(oldId);
			// categoryspan...
			Integer endPosition = bracketPositionsCategoryLinks.get(i);
			l.setText(wikiOrigText.substring(i, endPosition));
			l.setTitle(title);

			output.add(l);

			l = null;
		}
		return output;
	}

}
