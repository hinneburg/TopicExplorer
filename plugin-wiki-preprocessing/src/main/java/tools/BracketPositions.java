package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.TreeMap;

public class BracketPositions {

	private final String wikiOrigText;
	private HashMap<Integer, Integer> bracketPositionsHashMapLinksStartTillEnd = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> bracketPositionsHashMapLinkTargetInclPipePosition = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> bracketPositionsNestedLinks = new HashMap<Integer, Integer>();
	private TreeMap<Integer, Integer> bracketPositionsLinksWithoutAnyPipes = new TreeMap<Integer, Integer>();

	private NavigableMap<Integer, Integer> bracketPositionsLinkTarget = new TreeMap<Integer, Integer>();

	public BracketPositions(String wikiText) {
		this.wikiOrigText = wikiText;
		putBracketPositionsIntoHashMaps();
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
						// 100 % correct link, with only one pipe
						bracketPositionsHashMapLinkTargetInclPipePosition.put(posBracketStarts, pipeList.getFirst()); // link-target
						bracketPositionsHashMapLinksStartTillEnd.put(posBracketStarts, i);
						boolBracketOpen = false;
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

									if (pipePos > 0) {
										bracketPositionsHashMapLinkTargetInclPipePosition.put(start, pipePos + start);
										bracketPositionsHashMapLinksStartTillEnd.put(start, end);
									} else if (pipePos == -1) {
										bracketPositionsLinksWithoutAnyPipes.put(start, end);
									}

								}
								boolInsertNestedLinksIntoBracketsPositionsHashMap = false;
							}

						} else if (nestedStart.size() == 0) {
							// ending of normal link without any nested links
							if (!boolHasPipe) {
								bracketPositionsLinksWithoutAnyPipes.put(posBracketStarts, i);
								boolBracketOpen = false;
							}
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

	public List<LinkElement> getLinkElementListOfAllLinks() {

		ArrayList<LinkElement> output = new ArrayList<LinkElement>();
		LinkElement l;

		for (Integer i : bracketPositionsLinkTarget.keySet()) {

			l = new LinkElement();
			// System.out.println(i + " " +
			// bracketPositionsHashMapLinkTargetInclPipePosition.get(i) + " "
			// + bracketPositionsHashMapLinksStartTillEnd.get(i) + " "
			// + wikiOrigText.substring(i,
			// bracketPositionsHashMapLinksStartTillEnd.get(i)));
			l.setCompleteLinkSpan(new PointInteger(i, bracketPositionsHashMapLinksStartTillEnd.get(i)));
			l.setLinkTextPosition(new PointInteger(bracketPositionsLinkTarget.get(i),
					bracketPositionsHashMapLinksStartTillEnd.get(i)));
			l.setTargetString(wikiOrigText.substring(l.getCompleteLinkStartPosition(), l.getLinkTextStart()));
			l.setText(wikiOrigText.substring(l.getLinkTextStart() + 1, l.getLinkTextEnd()));

			// System.out.println(l.getText() + " " + l.getTarget());

			output.add(l);

			l = null;
		}
		return output;
	}

}
