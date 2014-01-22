package wikiParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.sweble.wikitext.engine.CompilerException;
//import org.sweble.wikitext.engine.CompiledPage;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.WtEngine;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.nodes.EngCompiledPage;
import org.sweble.wikitext.engine.utils.DefaultConfigEn;
import org.sweble.wikitext.parser.parser.LinkTargetException;

import tools.BracketPositions;
import tools.PointInteger;
import tools.WikiArticle;
import tools.WikiIDTitlePair;
import tools.WikiTextToCSVForeward;

public class PreMalletParallelization extends Thread {

	final int wrapCol = 500; // Breite bis Zeilenumbruch, könnte erhöht werden

	private Properties prop;
	private Database db;

	private List<WikiIDTitlePair> articleNames;

	private BufferedWriter bwCSVOrigText;
	private BufferedWriter bwInputSQLParsedText;
	private BufferedWriter bwLogger;

	public static final String endOfDocumentInSQLOutput = "|s|q|l|e|n|d|i|n|g|\n";

	private final String fileseparator = System.getProperty("file.separator");

	private boolean debug;
	private boolean onlyOneOutput;
	private boolean onlyParsedLinks;
	private boolean bool_outputInMetaFiles;

	private PreparedStatement stmt;

	public PreMalletParallelization() {
	}

	public PreMalletParallelization(List<WikiIDTitlePair> articleNames, Properties prop) {
		this(articleNames, prop, null, "Thread-" + System.currentTimeMillis()); // ????
	}

	public PreMalletParallelization(List<WikiIDTitlePair> articleNames, Properties prop, ThreadGroup tg,
			String threadName) {
		super(tg, null, threadName);

		this.articleNames = articleNames;
		this.prop = prop;
	}

	public void setOnlyOneOutputParameter(boolean bool) {
		this.onlyOneOutput = bool;
	}

	/**
	 * 
	 */
	private void init() {

		try {

			db = new Database(prop);

			String outputFolderInclTemp = prop.getProperty("Wiki_outputFolder") + fileseparator + "temp";

			File file = new File(outputFolderInclTemp + fileseparator + this.getName() + "-malletWikiText.csv");
			bwCSVOrigText = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

			File fileSqlParsedText = new File(outputFolderInclTemp + fileseparator + this.getName()
					+ "-inputParsedText.csv");
			bwInputSQLParsedText = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileSqlParsedText),
					"UTF-8"));

			File fileLog = new File(outputFolderInclTemp + fileseparator + fileseparator + this.getName()
					+ "-logging.txt");
			bwLogger = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileLog), "UTF-8"));

			stmt = db.getConnection().prepareStatement("UPDATE text set old_text = ? WHERE old_id = ?");

			if (prop.getProperty("Wiki_debug").equalsIgnoreCase("true")) {
				debug = true;
			}
			onlyParsedLinks = prop.getProperty("Wiki_onlyParsedLinks").equalsIgnoreCase("true");

			if (prop.getProperty("Wiki_fileOutput").equalsIgnoreCase("true")) {
				bool_outputInMetaFiles = true;
			}

		} catch (SQLException sqlEx) {
			System.err.println("init " + this.getName());
			sqlEx.printStackTrace();
		} catch (IOException ioEx) {
			System.err.println("init :" + this.getName());
			ioEx.printStackTrace();
		} catch (InstantiationException instEx) {
			System.err.println("init " + this.getName());
			instEx.printStackTrace();
		} catch (IllegalAccessException iaEx) {
			System.err.println("init " + this.getName());
			iaEx.printStackTrace();
		} catch (ClassNotFoundException cnfEx) {
			System.err.println("init " + this.getName());
			cnfEx.printStackTrace();
		}

	}

	/**
	 * 
	 * 
	 * @param id_title
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	private String getWikiTextWithRevID(WikiIDTitlePair id_title) throws SQLException {

		SupporterForBothTypes t = new SupporterForBothTypes(db);
		String wikitxt = t.getWikiTextOnlyWithID(id_title.getOld_id());
		t = null;

		Integer tmpLength = wikitxt.length();
		String wikiTextCorrected = doArticleCorrection(wikitxt, id_title);

		if (tmpLength != wikiTextCorrected.length()) {

			updateReplacedTextInDatabase(wikiTextCorrected, id_title.getOld_id());
			wikitxt = wikiTextCorrected;

			try {
				// für test
				if (bwLogger != null) {
					bwLogger.append(id_title.getWikiTitle() + " updated.\n");
				}
			} catch (IOException e) {
				System.err.println("Cannot write in logger.");
				// e.printStackTrace();
			}
		}

		return wikitxt;
	}

	String doArticleCorrection(String wikitxt, WikiIDTitlePair id_title) {

		// remove comments from wikitext
		if (wikitxt.contains("<!--")) {
			wikitxt = replaceTheOpenAndCloseTagsFromText(wikitxt, "<!--", "-->");
		}

		// remove links
		// important, with this the correct coloring of links is guaranteed
		if (wikitxt.contains("[[")) {
			wikitxt = correctLinksWithoutPipes(wikitxt, id_title);
		}

		return wikitxt;
	}

	private String correctLinksWithoutPipes(String wikitxt, WikiIDTitlePair idTitle) {

		BracketPositions bp = new BracketPositions(wikitxt, idTitle.getOld_id(), idTitle.getWikiTitle());
		List<PointInteger> list = bp.getSortedListOfAllBracketsWithoutPipes();

		for (Integer i = list.size() - 1; i >= 0; i--) {

			Integer x = list.get(i).getStartPosition();
			Integer y = list.get(i).getEndPosition();

			wikitxt = wikitxt.substring(0, x) + wikitxt.substring(x, y) + "|" + wikitxt.substring(x, y)
					+ wikitxt.substring(y, wikitxt.length());
		}
		list = null;
		bp = null;

		return wikitxt;
	}

	/**
	 * 
	 * wikitext in table text is saved as BLOB, and BLOB can only be saved with
	 * prepared statements
	 * 
	 * prepared statement is opened in init
	 * 
	 * @param wikitxt
	 * @param old_id
	 */
	private void updateReplacedTextInDatabase(String wikitxt, int old_id) {

		try {
			byte[] textAsByte = wikitxt.getBytes();

			stmt.setBytes(1, textAsByte);
			stmt.setInt(2, old_id);
			stmt.executeUpdate();

			stmt.clearParameters();
			textAsByte = null;

		} catch (SQLException e) {
			try {
				bwLogger.append("fail update updateReplacedTextInDatabase: id = " + old_id + ", " + e.getMessage()
						+ "\n");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.err.println("updateReplacedTextInDatabase: " + e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * deletes all comments within the wikitext
	 * 
	 * @param wikiTxt
	 * @param startTag
	 * @param endTag
	 * @return
	 */
	private String replaceTheOpenAndCloseTagsFromText(String wikiTxt, String startTag, String endTag) {
		String sReturn;
		String before = "";
		String behind = "";
		int posStart = 0;

		int posBeginn = -1;
		int posEnd = -1;

		posBeginn = wikiTxt.indexOf(startTag, posStart);

		if (posBeginn > -1) {
			posEnd = wikiTxt.indexOf(endTag, posBeginn + 1);

			if (posEnd > -1) {

				posEnd = posEnd + endTag.length();

				before = wikiTxt.substring(posStart, posBeginn);
				behind = wikiTxt.substring(posEnd, wikiTxt.length());
			}

			// rekursion with remaining string
			if (posEnd + 1 < wikiTxt.length()) {
				behind = replaceTheOpenAndCloseTagsFromText(behind, startTag, endTag);
			}

			sReturn = before + behind;
		} else {
			sReturn = wikiTxt;
		}

		return sReturn;
	}

	/**
	 * 
	 * output: csv for mallet
	 */
	private void appendToBW(String csv) {
		try {
			bwCSVOrigText.append(csv);
			// bwCSVOrigText.flush();
		} catch (IOException e) {
			System.err.println("append to bw ");
			e.printStackTrace();
		}
	}

	private void appendToSqlFile(String filesForOrgTable) {
		try {
			bwInputSQLParsedText.append(filesForOrgTable);
			// bwInputSQLParsedText.flush();
		} catch (IOException e) {
			System.err.println("append to inputsql ");
			e.printStackTrace();
		}
	}

	/**
	 * output: logging
	 */
	private void appendLogging(String loggingLine) {
		try {
			bwLogger.append(loggingLine);
			bwLogger.append("\n");
			// bwLogger.flush();
		} catch (IOException e) {
			System.err.println("appendLogging " + this.getName());
			// e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param id_title
	 * @return parsed Wikitext as string
	 * @throws Exception
	 */
	private WikiArticle getParsedWikiArticle(WikiIDTitlePair id_title) {
		String wikiText = "";

		try {
			wikiText = getWikiTextWithRevID(id_title);

			// double parsing, one for the Wikitext and one for text
			return new WikiArticle(parse(wikiText, id_title, true), id_title.getOld_id(), wikiText,
					id_title.getWikiTitle(), 1, parse(wikiText, id_title, false));
			//
			//
		} catch (CompilerException e3) {
			System.err.println("Failure getParsedWikiText compiler exception " + id_title.getWikiTitle() + " "
					+ id_title.getOld_id());
			return new WikiArticle("Failure in getParsedWikiArticle(): " + e3.getMessage(), id_title.getOld_id(), "",
					id_title.getWikiTitle(), -4, "");
		} catch (LinkTargetException e2) {
			System.err.println("Failure getParsedWikiText linktarget exception" + id_title.getWikiTitle() + " "
					+ id_title.getOld_id());
			return new WikiArticle("Failure in getParsedWikiArticle(): " + e2.getMessage(), id_title.getOld_id(), "",
					id_title.getWikiTitle(), -3, "");
		} catch (SQLException sqlEx) {
			System.err.println("Failure getParsedWikiText linktarget exception" + id_title.getWikiTitle() + " "
					+ id_title.getOld_id());
			return new WikiArticle("Failure in getParsedWikiArticle(): " + sqlEx.getMessage(), id_title.getOld_id(),
					"", id_title.getWikiTitle(), -3, "");
		}
	}

	String parse(String wikiOrigText, WikiIDTitlePair id_title, boolean csvOrReadable) throws CompilerException,
			LinkTargetException {
		WikiConfig config = DefaultConfigEn.generate();

		WtEngine engine = new WtEngine(config);

		// Retrieve a page
		PageTitle pageTitle = PageTitle.make(config, id_title.getWikiTitle());

		PageId pageId = new PageId(pageTitle, -1);

		// Compile the retrieved page
		EngCompiledPage cp = engine.postprocess(pageId, wikiOrigText, null);

		TextConverter p = new TextConverter(config, wrapCol);
		p.setCsvOrReadable(csvOrReadable);

		return (String) p.go(cp.getPage());
	}

	private void createOutputOfInputMalletAndOrgTable(WikiTextToCSVForeward textTocsv) {

		// bw für inputmallet.sql
		appendToBW(textTocsv.getCSV());

		// für orgTable
		appendToSqlFile(textTocsv.getOrgTableString());

	}

	private void ifDebugGenerateOutputAndWriteIntoLogger(Integer i) {
		// testausgabe
		if (debug) {

			String next = "";
			if (i + 1 < articleNames.size()) {
				next = "( nächster Artikel:" + articleNames.get(i + 1).getWikiTitle() + ")";
			}
			appendLogging(articleNames.get(i).getWikiTitle() + " , " + new Integer(articleNames.size() - i - 1)
					+ " left " + next + " \n");
			System.out.println(articleNames.get(i).getWikiTitle() + " , " + new Integer(articleNames.size() - i - 1)
					+ " left ");
		}

	}

	private void createMetafiles(SupporterForBothTypes s, WikiArticle w, WikiTextToCSVForeward textTocsv) {

		String fileOutputFolder = prop.getProperty("Wiki_fileOutputFolder");

		try {
			s.printIntoFile(w.getParsedWikiTextReadable(), fileOutputFolder + fileseparator + w.getOldID().toString()
					+ "_readableText");
		} catch (IOException e) {
			System.err.println("File printing caused a problem.");
			e.printStackTrace();
		}

		// get link information
		String fileInput = textTocsv.getLinkInfos();
		try {
			s.printIntoFile(fileInput, fileOutputFolder + fileseparator + w.getOldID().toString() + "_linkPositions");
		} catch (IOException e) {
			System.err.println("Link information printing caused a problem.");
			e.printStackTrace();
		}

		// get section positions
		fileInput = textTocsv.getSectionCaptions();
		try {
			s.printIntoFile(fileInput, fileOutputFolder + fileseparator + w.getOldID().toString() + "_sectionPositions");
		} catch (IOException e) {
			System.err.println("Section caption printing caused a problem.");
			e.printStackTrace();
		}

		// get picture positions
		fileInput = textTocsv.getPictures();
		try {
			s.printIntoFile(fileInput, fileOutputFolder + fileseparator + w.getOldID().toString() + "_picturePositions");
		} catch (IOException e) {
			System.err.println("Picture position printing caused a problem.");
			e.printStackTrace();
		}

		// get category infos
		fileInput = textTocsv.getCategroryInfos();
		try {
			s.printIntoFile(fileInput, fileOutputFolder + fileseparator + w.getOldID().toString() + "_category");
		} catch (IOException e) {
			System.err.println("Category information printing caused a problem.");
			e.printStackTrace();
		}

	}

	private void generateSingleFileOutput(SupporterForBothTypes s, WikiArticle w) {

		try {
			s.printIntoFile(w.getParsedWikiText(), "outputparsed.txt");
			s.printIntoFile(w.getWikiOrigText(), "inputorig.txt");
		} catch (IOException ioEx1) {
			System.err.println("Generating Single file output caused a problem.");
			ioEx1.printStackTrace();
		}

		// alle Wörter einzeln mit Positionsangaben
		try {
			s.printIntoFile(s.tokenizeEveryElementOfTheTextForTestOutput(w.getWikiOrigText()),
					"tokensInputorigText.txt");
		} catch (IOException ioEx2) {
			System.err.println("Generating Single file output (words with position indication) caused a problem.");
			ioEx2.printStackTrace();
		}

	}

	@Override
	public void run() {

		try {
			WikiArticle w;
			WikiTextToCSVForeward textTocsv;
			// WikiTextToCsvbackward textTocsv ;

			this.init();

			SupporterForBothTypes s = new SupporterForBothTypes();

			if (prop.getProperty("Wiki_transaction").equalsIgnoreCase("true")) {
				db.executeUpdateQuery("START TRANSACTION;");
			}

			for (int i = 0; i < articleNames.size(); i++) {

				ifDebugGenerateOutputAndWriteIntoLogger(i);

				w = getParsedWikiArticle(articleNames.get(i));

				// fürs logging
				if (w.getFailureId() <= 0) {
					// as failuretext
					appendLogging(w.getWikiTitle() + " " + w.getOldID() + "\t\t\t failure_id <= 0 , "
							+ w.getParsedWikiText());
				} else if (w.getParsedWikiTextReadable().length() == 0) {
					appendLogging(w.getWikiTitle() + " " + w.getOldID() + "\t\t\t readableText.length() is 0 ");
				} else {

					// temporäre Ausgabe, zur Veranschaulichung nur wenn ein
					// Artikel geladen wird
					if (onlyOneOutput) {
						generateSingleFileOutput(s, w);
					}

					// generate Metainfos
					textTocsv = new WikiTextToCSVForeward(w, bwLogger, onlyParsedLinks, debug);

					if (bool_outputInMetaFiles) {
						createMetafiles(s, w, textTocsv);
					}
					createOutputOfInputMalletAndOrgTable(textTocsv);
				}

				textTocsv = null;
				w = null;
				bwLogger.flush();
			}

			// alles beenden und schließen, Buffer leeren

			bwCSVOrigText.flush();
			bwCSVOrigText.close();

			bwInputSQLParsedText.flush();
			bwInputSQLParsedText.close();

			bwLogger.flush();
			bwLogger.close();

			stmt.close();

			if (prop.getProperty("Wiki_transaction").equalsIgnoreCase("true")) {
				db.executeUpdateQuery("COMMIT;");
			}

		}

		catch (SQLException e2) {

			appendLogging("sql-failure in run: " + this.getName() + " " + e2.getMessage() + e2.getStackTrace() + " \n");
			if (debug) {
				e2.printStackTrace();
			}
		} catch (IOException e3) {
			appendLogging("io-failure in run: " + this.getName() + " " + e3.getMessage() + e3.getStackTrace() + " \n");
			if (debug) {
				e3.printStackTrace();
			}
		} finally {
			try {
				db.shutdownDB();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
	}

}