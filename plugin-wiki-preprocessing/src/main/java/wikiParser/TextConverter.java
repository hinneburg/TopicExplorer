package wikiParser;

/**
 * Copyright 2011 The Open Source Research Group,
 *                University of Erlangen-Nürnberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.nodes.EngPage;
import org.sweble.wikitext.parser.nodes.WtBold;
import org.sweble.wikitext.parser.nodes.WtExternalLink;
import org.sweble.wikitext.parser.nodes.WtHorizontalRule;
import org.sweble.wikitext.parser.nodes.WtIllegalCodePoint;
import org.sweble.wikitext.parser.nodes.WtImageLink;
import org.sweble.wikitext.parser.nodes.WtInternalLink;
import org.sweble.wikitext.parser.nodes.WtItalics;
import org.sweble.wikitext.parser.nodes.WtListItem;
import org.sweble.wikitext.parser.nodes.WtNode;
import org.sweble.wikitext.parser.nodes.WtNodeList;
import org.sweble.wikitext.parser.nodes.WtOrderedList;
import org.sweble.wikitext.parser.nodes.WtPageSwitch;
import org.sweble.wikitext.parser.nodes.WtParagraph;
import org.sweble.wikitext.parser.nodes.WtSection;
import org.sweble.wikitext.parser.nodes.WtTagExtension;
import org.sweble.wikitext.parser.nodes.WtTemplate;
import org.sweble.wikitext.parser.nodes.WtTemplateArgument;
import org.sweble.wikitext.parser.nodes.WtTemplateParameter;
import org.sweble.wikitext.parser.nodes.WtText;
import org.sweble.wikitext.parser.nodes.WtUnorderedList;
import org.sweble.wikitext.parser.nodes.WtUrl;
import org.sweble.wikitext.parser.nodes.WtWhitespace;
import org.sweble.wikitext.parser.nodes.WtXmlCharRef;
import org.sweble.wikitext.parser.nodes.WtXmlComment;
import org.sweble.wikitext.parser.nodes.WtXmlElement;
import org.sweble.wikitext.parser.nodes.WtXmlEntityRef;
import org.sweble.wikitext.parser.parser.LinkTargetException;

import tools.ExtraInformations;
import de.fau.cs.osr.ptk.common.AstVisitor;
import de.fau.cs.osr.utils.StringUtils;

//neu dazu

/**
 * A visitor to convert an article AST into a pure text representation. To
 * better understand the visitor pattern as implemented by the Visitor class,
 * please take a look at the following resources:
 * <ul>
 * <li>{@link http://en.wikipedia.org/wiki/Visitor_pattern} (classic pattern)</li>
 * <li>{@link http://www.javaworld.com/javaworld/javatips/jw-javatip98.html}
 * (the version we use here)</li>
 * </ul>
 * 
 * The methods needed to descend into an AST and visit the children of a given
 * node <code>n</code> are
 * <ul>
 * <li><code>dispatch(n)</code> - visit node <code>n</code>,</li>
 * <li><code>iterate(n)</code> - visit the <b>children</b> of node
 * <code>n</code>,</li>
 * <li><code>map(n)</code> - visit the <b>children</b> of node <code>n</code>
 * and gather the return values of the <code>visit()</code> calls in a list,</li>
 * <li><code>mapInPlace(n)</code> - visit the <b>children</b> of node
 * <code>n</code> and replace each child node <code>c</code> with the return
 * value of the call to <code>visit(c)</code>.</li>
 * </ul>
 */
public class TextConverter extends AstVisitor<WtNode> {
	private static final Pattern ws = Pattern.compile("\\s+");

	private final WikiConfig config;

	private final int wrapCol;

	private StringBuilder sb;

	private StringBuilder line;

	private int extLinkNum;

	private boolean pastBod;

	private int needNewlines;

	private boolean needSpace;

	private boolean noWrap;

	// private LinkedList<Integer> sections;

	// =========================================================================

	private boolean csvOrReadable = false; // false = readable, true = csv , see
											// below setCsvOrReadable()
	private boolean debug = false;
	private boolean debugAstNode = false;

	private boolean bool_has_extra_information = false;
	private String extra_information;

	private List<String> listNodeIgnor;

	// =========================================================================

	public TextConverter(WikiConfig config, int wrapCol) {
		this.config = config;
		this.wrapCol = wrapCol;

		this.init();
	}

	private void init() {
		listNodeIgnor = new ArrayList<String>();
		listNodeIgnor.add("XmlAttribute");
		listNodeIgnor.add("XmlElementOpen");
		listNodeIgnor.add("XmlElementClose"); // tritt auf, wenn z.B. ein
												// schließendes
		// Tag kommt ohne das es geöffnet wurde
		// , z.B. </ref>
		// list.add("SemiPre");
		listNodeIgnor.add("XmlElementEmpty");
		listNodeIgnor.add("XmlAttributeGarbage");

		// Tables
		listNodeIgnor.add("TableCaption");
		listNodeIgnor.add("TableCell");
		listNodeIgnor.add("TableHeader");
		listNodeIgnor.add("Table");
		// list.add("");
	}

	@Override
	protected boolean before(WtNode node) {
		// This method is called by go() before visitation starts
		sb = new StringBuilder();
		line = new StringBuilder();
		extLinkNum = 1;
		pastBod = false;
		needNewlines = 0;
		needSpace = false;
		noWrap = false;
		// sections = new LinkedList<Integer>();
		return super.before(node);
	}

	@Override
	protected Object after(WtNode node, Object result) {
		finishLine();

		// This method is called by go() after visitation has finished
		// The return value will be passed to go() which passes it to the caller
		return sb.toString();
	}

	// =========================================================================

	public void visit(WtNode n) {

		if (debugAstNode)
			System.err.println("visit AstNode n: " + n.getNodeName());

		// TODO wahrscheinlich kann das weg
		if (n.getNodeName().equals("DefinitionDefinition")) {
			dispatch(n.get(1));
		} else if (n.getNodeName().equals("SemiPre")) {
			dispatch(n.get(0));
			// TODO überdenken , 1 wirft exception
		} else if (n.getNodeName().equals("SemiPreLine")) {
			dispatch(n.get(0)); // 1 wirft exception
		} else {
			if (debugAstNode) {
				// //for debugging
				if (!proofAstnodeIgnored(n.getNodeName())) {
					System.err.println("visit AstNode n: " + n.getNodeName());
				}
				// dispatch(n.get(0));
				// dispatch(n.get(1));

			}
		}

	}

	private boolean proofAstnodeIgnored(String nodeName) {
		return listNodeIgnor.contains(nodeName);
	}

	public void visit(WtNodeList n) {
		iterate(n);
	}

	public void visit(WtUnorderedList e) {
		// iterate(e); //damit werden ungeordnete Aufzählungslisten geparst
	}

	public void visit(WtOrderedList e) {
		// iterate(e); //damit werden nummerierte Aufzählungslisten geparst
	}

	public void visit(WtListItem item) // sind Elemente von den Listen, falls
										// sie geparst werden
	{
		if (!csvOrReadable)
			newline(1);

		iterate(item);
	}

	public void visit(EngPage p) {
		iterate(p);
	}

	public void visit(WtText text) {

		String txt_orig = text.getContent();
		String txt = txt_orig.trim();

		if (ExtraInformations.getIsPictureStartsWith(txt)) {
			bool_has_extra_information = true;
			extra_information = ExtraInformations.extraPicure3Append;

			// erkanntes bild entfernen
			if (!csvOrReadable) {
				resetSettingsBeforeReturn();
				return;
			}
		}

		if (csvOrReadable && txt.length() == 0) {
			return;
		}

		write(txt_orig);

	}

	public void visit(WtWhitespace w) {
		if (!csvOrReadable)
			write(" ");
	}

	public void visit(WtBold b) {

		bool_has_extra_information = true;
		extra_information = ExtraInformations.extraBoldAppend;

		if (b.size() > 0 && b.get(0).size() > 1) {
			resetSettingsBeforeReturn(); // only reset,
			// System.err.println(b.size() + " " + b.get(0).size() + " " +
			// b.get(0).toString());
		}
		iterate(b);
	}

	public void visit(WtItalics i) {
		bool_has_extra_information = true;
		extra_information = ExtraInformations.extraCursiveAppend;
		if (i.size() > 0 && i.get(0).size() > 1) {
			resetSettingsBeforeReturn(); // only reset,
			// System.err.println(i.size() + " " + i.get(0).size() + " " +
			// i.get(0).toString());
		}
		iterate(i);
	}

	public void visit(WtXmlCharRef cr) {
		if (debug)
			System.out.println(" xmlCharRef ");

		if (!csvOrReadable) {
			write(Character.toChars(cr.getCodePoint()));
		}
	}

	/*
	 * übersetzt xml , z.B. &alpha in alphazeichen
	 */
	public void visit(WtXmlEntityRef er) {
		// // T O D O nochmal überdenken ob wirklich auslassen, wirft aber immer
		// // Fehler weil die zeichen nicht so bleiben wie sie im originaltext
		// // waren
		// // //System.out.println(er.getName()+ " " +
		// // er.getLocation().toString());
		//
		// // if (!csvOrReadable) {

		// String ch = er.getResolved();
		// if (ch == null) {
		// write('&');
		// write(er.getName());
		// write(';');
		// } else {
		// // // einzelnes geschütztes leerzeichen stört, wenn es innerhalb von
		// // // bold-Element an führender Stelle eingesetz wird
		// if (!ch.trim().equalsIgnoreCase("\u00A0")) {
		// write(ch);
		// } else {
		// resetSettingsBeforeReturn();
		// }
		// }
		// // // }
		// if (debug) {
		// System.err.println("WtXmlEntityRef " + er.getName());
		// }
	}

	public void visit(WtUrl wtUrl) {

		if (debug) {
			System.err.println("visit url überprüfen " + wtUrl.getPath());
		}

		// if (!wtUrl.getProtocol().isEmpty())
		// {
		// write(wtUrl.getProtocol());
		// write(':');
		// }
		// write(wtUrl.getPath());
	}

	public void visit(WtExternalLink link) {
		// links sind nur nummern
		if (debug)
			System.out.println("externallink überprüfen " + extLinkNum);

		//
		// write('[');
		// write(extLinkNum++);
		// write(']');
	}

	public void visit(WtInternalLink link) {

		if (link.getTarget().getContent().contains(":")) {

			String target = link.getTarget().getContent();
			// nur wenn csv erstellt wird,
			if (ExtraInformations.getIsPictureStartsWith(target)) {

				if (csvOrReadable) {
					bool_has_extra_information = true;
					extra_information = ExtraInformations.extraPicure4Append;

					write(target);

					// bool_has_extra_information = true;
					// extra_information = ExtraInformations.extraPicure5Append;

					// für Abstand zwischen Bild und Bildtext
					if (!csvOrReadable) {
						write(" ");
					}

					iterate(link.getTitle());

					if (!csvOrReadable) {
						newline(1);
					}
					// abbruch weil restliche Teil für Links ohne Doppelpunkte
					// bestimmt ist und nicht doppelt ausgeführt werden soll
					return;
				} else {
					// abbruch damit keine bilder ausgegeben werden im
					// readablemodus ,
					// aba eigentlich schlecht für steuerung wenn doch...
					return;
				}

			} else {
				// abbruch damit keine kategorien oder allgemein Links mit
				// Doppelpunkt ausgeben werden
				return;
			}
		}

		try {
			PageTitle page = PageTitle.make(config, link.getTarget().getContent());
			if (page.getNamespace().equals(config.getNamespace("Category")))
				return;
		} catch (LinkTargetException e) {
		}

		if (link.getPrefix().length() > 0) {
			// T O D O noch in Logg rein, bzw sie sind nur NULL
			System.err.println("Es gibt prefix !!" + link.getPrefix());
		}

		if (!csvOrReadable)
			write(link.getPrefix());

		bool_has_extra_information = true;
		extra_information = ExtraInformations.extraInternalLink;

		if (!link.hasTitle()) {
			write(link.getTarget().getContent());
		} else {

			// wenn mehr als nur ein Textfeld im Linktitel enthalten ist, kann
			// die Extrainformation nicht mehr an den Linktitel angefügt werden,
			// weil es sonst zu Fehlern beim suchen kommt. tritt z.B. auf wenn
			// xml Tags enthalten sind
			// link.get(1) == titel

			if (link.get(1).size() > 1) {
				resetSettingsBeforeReturn();
			} else if (link.get(1).size() == 1) {
				if (link.get(1).get(0).size() > 1) {
					resetSettingsBeforeReturn();
				}
			}

			iterate(link.getTitle());
		}

		if (!csvOrReadable)
			write(link.getPostfix());
	}

	/*
	 * Abschnitt, eingeleitet mit == / oder mehr ===
	 */
	public void visit(WtSection s) {
		finishLine();

		StringBuilder saveSb = sb; // stringbuiler gespeichert
		boolean saveNoWrap = noWrap;

		sb = new StringBuilder(); // sb neu erstellt
		noWrap = true;

		bool_has_extra_information = true;
		extra_information = ExtraInformations.extraSectionCaptionAppend + ExtraInformations.extraSectionLevelStart
				+ s.getLevel() + ExtraInformations.extraSectionLevelEnd;

		iterate(s.getHeading()); // wahrscheinlich in sb gespeichert

		finishLine();

		String title = sb.toString(); // .trim();

		sb = saveSb; // gespeicherteret sb zurück

		if (!csvOrReadable) {
			newline(2);
			write(title.trim());
			newline(1);
		} else {
			write(title);
		}

		noWrap = saveNoWrap;

		iterate(s.getBody());
	}

	public void visit(WtParagraph p) {
		iterate(p);
		if (!csvOrReadable) {
			newline(2);
		}
	}

	public void visit(WtHorizontalRule hr) {
		if (!csvOrReadable) {
			newline(1);
			write(StringUtils.strrep('-', wrapCol));
			newline(2);
		}
	}

	/*
	 * write(" ") eingefügt
	 */
	public void visit(WtXmlElement e) {
		if (e.getName().equalsIgnoreCase("br")) {
			newline(1);
		} else {
			// System.out.println( " xmlElement " + e.getBody());
			write(" ");
			iterate(e.getBody());
			write(" ");
		}
	}

	// =========================================================================
	// Stuff we want to hide

	public void visit(WtImageLink n) {
		if (debug)
			System.out.println(" ImageLink " + n.getTarget());
		// write(" ImageLink " + n.getTarget());

		if (csvOrReadable) {
			// Dateiname:

			bool_has_extra_information = true;
			extra_information = ExtraInformations.extraPicure1Append;

			write(n.getTarget().getContent());

			// Titel??, gibt es auch mehr Unterknoten? eventuell dynamisch
			// machen?

			// bool_has_extra_information = true;
			// extra_information = ExtraInformations.extraPicure2Append; wird
			// falsch geparst in: Australia_at_the_1984_Summer_Olympics , Bild
			// in überschrift

			dispatch(n.get(2));

			// Bilder sollen nicht in den Text
			// } else {
			// write(n.getTarget().getContent());
			// dispatch(n.get(2));
			// writeWord(" ");
		}

	}

	public void visit(WtIllegalCodePoint n) {
		if (debug)
			System.out.println(" illegalcodepoint");
	}

	public void visit(WtXmlComment n) {
		if (debug)
			System.out.println(" xmlComment " + n.getContent());
	}

	/*
	 * Templates werden ignoriert
	 */
	public void visit(WtTemplate n) {
		// Infobox z.B.

		// String s = n.getName().toString();
		// if (s.startsWith("[Text(\"Infobox "))
		// {
		// return;
		// }
		// else
		// {
		// visit(n.getArgs());
		// }

		// System.out.println( " Template " + n.getName() );
		// write(" Template " + n.getName());
		// visit(n.getArgs());
	}

	public void visit(WtTemplateArgument n) {
		// System.out.println( " Templateargument " + n.getName() + " " +
		// n.getValue());

		// dispatch(n.getName());
		// writeWord(" ");
		// dispatch(n.getValue());
		// writeWord(" ");

		// iterate(n.get(1));
		// dispatch(n.getValue());
		// writeWord(" ");
	}

	public void visit(WtTemplateParameter n) {
		if (debug)
			System.out.println(" Templateparameter ");
	}

	public void visit(WtTagExtension n) {
		if (debug)
			System.out.println(" TagExtension " + n.getBody());
	}

	public void visit(WtPageSwitch n) {
		if (debug)
			System.out.println(" Tagwtpageswitch " + n.getName());
	}

	// public void visit( MagicWord n)
	// {
	// System.out.println(" MagicWord ");
	// }

	// extra dazu
	// public void visit (WtTable table){
	//
	// if (!table.getBody().isEmpty())
	// iterate(table.getBody());
	// }
	//
	// public void visit(WtTableCaption tableCaption)
	// {
	// iterate(tableCaption);
	// }
	//
	// public void visit(WtTableCell tableCell)
	// {
	// iterate(tableCell);
	// }
	//
	// public void visit(WtTableHeader tableHeader)
	// {
	// iterate(tableHeader);
	// }
	//
	// public void visit(WtTableRow tableRow)
	// {
	// iterate(tableRow);
	// }
	//
	// public void visit(WtTableImplicitTableBody tableImplicitTablebody)
	// {
	// iterate(tableImplicitTablebody);
	// }

	// =========================================================================

	private void newline(int num) {
		if (pastBod) {
			if (num > needNewlines)
				needNewlines = num;
		}
	}

	private void wantSpace() {
		if (pastBod)
			needSpace = true;
	}

	private void finishLine() {
		sb.append(line.toString());
		line.setLength(0);
	}

	private void writeNewlines(int num) {
		finishLine();
		sb.append(StringUtils.strrep('\n', num));
		needNewlines = 0;
		needSpace = false;
	}

	private void writeWord(String s) {

		int length = s.length();
		if (length == 0) {
			resetSettingsBeforeReturn();
			return;
		}

		// einzelnes geschütztes Leerzeichen
		if (s.trim().equalsIgnoreCase("\u00A0")) {
			resetSettingsBeforeReturn();
			return;
		}

		if (getIsTermNOTAllowed(s)) {
			resetSettingsBeforeReturn();
			return;
		}

		if (csvOrReadable) {
			if (bool_has_extra_information) {
				bool_has_extra_information = false;
				s = s + extra_information;
			}
		}

		if (!noWrap && needNewlines <= 0) {
			if (needSpace)
				length += 1;

			if (line.length() + length >= wrapCol && line.length() > 0)
				writeNewlines(1);
		}

		if (needSpace && needNewlines <= 0)
			line.append(' ');

		if (needNewlines > 0)
			writeNewlines(needNewlines);

		needSpace = false;
		pastBod = true;
		line.append(s);
	}

	private void resetSettingsBeforeReturn() {
		if (bool_has_extra_information) {
			bool_has_extra_information = false;
			extra_information = "";
		}
	}

	/**
	 * s.trim(); eingefügt damit keine leerzeichen geschrieben werden, nur aktiv
	 * wenn csvOrReadable = true
	 * 
	 * @param s
	 */
	private void write(String s) {

		if (s.isEmpty()) {
			resetSettingsBeforeReturn();
			return;
		}

		if (csvOrReadable) {
			// writeWord(s.trim());
			if (s.trim().length() > 0) {
				writeWord(s);
				newline(1);
			}
		} else if (!csvOrReadable) {

			// bilder werden nicht ausgegeben
			if (bool_has_extra_information) {
				if (testIfContainsPicturePhrase(extra_information)) {
					resetSettingsBeforeReturn();
					return;
				}
			}

			if (Character.isSpaceChar(s.charAt(0)))
				wantSpace();

			String[] words = ws.split(s);
			for (int i = 0; i < words.length;) {
				writeWord(words[i]);
				if (++i < words.length)
					wantSpace();
			}

			if (Character.isSpaceChar(s.charAt(s.length() - 1)))
				wantSpace();
		}
	}

	private void write(char[] cs) {
		write(String.valueOf(cs));
	}

	private void write(char ch) {
		writeWord(String.valueOf(ch));
	}

	private void write(int num) {
		writeWord(String.valueOf(num));
	}

	private boolean getIsTermNOTAllowed(String txt) {

		Boolean bool = false;
		if (csvOrReadable) {
			if (txt.equals("{{") || txt.equals("[[") || txt.equals("]]") || txt.equals("]") || txt.equals("|")
					|| txt.equals("[") || txt.equals(".") || txt.equals(":") || txt.equals(",") || txt.equals(";")
					|| txt.equals("(") || txt.equals(")") || txt.equals("|") || txt.equals("||") || txt.equals("=")
					|| txt.equals("==") || txt.equals("|-")) {
				bool = true;
			}

		} else if (!csvOrReadable) {
			if (txt.equals("{{") || txt.equals("}}") || txt.equals("[[") || txt.equals("]]") || txt.equals("|")
					|| txt.equals("|-")) {
				bool = true;
			}

		}

		return bool;
	}

	private boolean testIfContainsPicturePhrase(String s) {
		return (s.contains("isPicture"));
	}

	public void setCsvOrReadable(boolean boolCsvOrReadable) {
		this.csvOrReadable = boolCsvOrReadable;
	}
}
