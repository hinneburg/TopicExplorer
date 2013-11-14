package wikiParser;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;



//import org.sweble.wikitext.engine.CompiledPage;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.WtEngine;
import org.sweble.wikitext.engine.utils.DefaultConfigEn;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.nodes.EngCompiledPage;

import tools.WikiArticle;
import tools.WikiIDTitlePair;
import tools.WikiTextToCSVForeward;
import wikiParser.SupporterForBothTypes;


public class PreMalletParallelisation extends Thread
{

	
	final int wrapCol = 180; //Breite bis Zeilenumbruch, könnte erhöht werden aba 
	
	private Properties prop;
	private Database db;
	
	private List <WikiIDTitlePair> articleNames ;
	
	private BufferedWriter bwCSVOrigText;
	private BufferedWriter bwInputSQLParsedText;
	private BufferedWriter bwLogger;	
	
	private final String endOfDocumentInSQLOutput = "|s|q|l|e|n|d|i|n|g|\n";
	
	private final String fileseparator = System.getProperty("file.separator");
	
	private boolean debug;

	private boolean onlyOneOutput;
	
	private PreparedStatement stmt;
	
	public PreMalletParallelisation()
	{
	}

	public PreMalletParallelisation(List<WikiIDTitlePair> articleNames, Properties prop)
	{
		this(articleNames,prop, null, "Thread-"+System.currentTimeMillis()); // ????
	}
	
	public PreMalletParallelisation(List<WikiIDTitlePair> articleNames, Properties prop, ThreadGroup tg , String threadName)
	{
		super(tg,null,threadName);
		
		this.articleNames = (List<WikiIDTitlePair>) articleNames;
		this.prop = prop;
	}
	
	public void setOnlyOneOutputParameter(boolean bool)
	{
		this.onlyOneOutput = bool;
	}
	
	
	/**
	 * 
	 */
	private void init() 
	{
		
		try
		{
			
			db = new Database(prop);
			
			String outputFolderInclTemp = prop.getProperty("Wiki_outputFolder") + fileseparator + "temp";
			
			
			File file = new File(outputFolderInclTemp + fileseparator + this.getName()+"-malletWikiText.csv");
			bwCSVOrigText = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
						
			File fileSqlParsedText= new File(outputFolderInclTemp + fileseparator+ this.getName()+ "-inputParsedText.csv");
			bwInputSQLParsedText = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileSqlParsedText), "UTF-8"));
			
			File fileLog = new File(outputFolderInclTemp + fileseparator + fileseparator+ this.getName()+ "-logging.txt"); 
			bwLogger= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileLog), "UTF-8"));
			
			stmt = db.getConnection().prepareStatement("UPDATE text set old_text = ? WHERE old_id = ?");
			
			if (prop.getProperty("Wiki_debug").equalsIgnoreCase("true")){
				debug = true;
			}
			
			}
		catch (Exception e)
		{
			System.err.println("init " + this.getName() );
			e.printStackTrace();
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
	private String getWikiTextWithRevID(WikiIDTitlePair id_title) throws SQLException, Exception
	{

		SupporterForBothTypes t = new SupporterForBothTypes(db);
		
		String wikitxt =  t.getWikiTextOnlyWithID(id_title.getOld_id());
		wikitxt = doArticleCorrection(wikitxt, id_title);
		
		return wikitxt;
	}
	
	private String doArticleCorrection(String wikitxt, WikiIDTitlePair id_title) 
	{
		Boolean needsSaving = false;

		// remove comments from wikitext
		if (wikitxt.contains("<!--"))
		{
			wikitxt = replaceTheOpenAndCloseTagsFromText(wikitxt, "<!--", "-->");
			needsSaving = true ;
		}
		
		// remove link 
		// important, with this the correct coloring of links is guaranteed
		if (wikitxt.contains("[[")) 
		{
			Integer tmp = wikitxt.length();
			wikitxt = correctLinksWithoutExtraContent(wikitxt);
			
			if (tmp != wikitxt.length())
				needsSaving = true;
		}

		// only do updates on the first run, e.g. after the import of a fresh dump
		if (needsSaving){

			try
			{
				bwLogger.append(id_title.getWikiTitle() + " updated.\n");
			}
			catch (IOException e)
			{
				System.err.println("Cannot write in logger.");
				e.printStackTrace();
			}
			
			updateReplacedTextInDatabase(wikitxt, id_title.getOld_id());
		}
		
		return wikitxt; 
	}

	
	/**
	 * 
	 * preparation for coloring the links
	 */
	private String correctLinksWithoutExtraContent(String wikitxt)
	{
		List <Point> list = getDoubleBracketsPositions(wikitxt,'[', ']');
		
		for (Integer i = list.size()-1; i >= 0; i--){
			
			Integer x = list.get(i).x;
			Integer y = list.get(i).y;
			
			wikitxt = wikitxt.substring(0, x) + wikitxt.substring(x, y) +  "|" + wikitxt.substring(x, y)  + wikitxt.substring(y, wikitxt.length());  
		}
		list = null;
		return wikitxt;
	}
	
	private List<Point> getDoubleBracketsPositions(String wikiTxt, Character openBracket, Character closeBracket)
	{
		List<Point> returnList = new ArrayList<Point>();
		Boolean isLinkOpen = false;
		Integer tmpPosition = 0;
		Integer bracketsCounter = 0;

		// Länge -1, wegen char at i + 1
		for (Integer i = 0; i < wikiTxt.length() - 1; i++)
		{

			if (wikiTxt.charAt(i) == openBracket && wikiTxt.charAt(i + 1) == openBracket)
			{
				if (!isLinkOpen){
					isLinkOpen = true;
					tmpPosition = i + 2;
				}
				bracketsCounter++;
			}
			
			if (isLinkOpen && bracketsCounter==1)
			{
				if (wikiTxt.charAt(i) == closeBracket && wikiTxt.charAt(i + 1) == closeBracket)
				{
					// only adds a point, when there are no "|" or ":" 
					if (!wikiTxt.substring(tmpPosition, i).contains("|") && !wikiTxt.substring(tmpPosition, i).contains(":"))
						returnList.add(new Point(tmpPosition, i));
					
//					System.out.println(wikiTxt.substring(tmpPosition, i));
					isLinkOpen = false;

				}
			}else if (isLinkOpen && bracketsCounter>1){
				bracketsCounter--;
			}
			
			
//			if (!isLinkOpen)
//			{
//				if (wikiTxt.charAt(i) == openBracket && wikiTxt.charAt(i + 1) == openBracket)
//				{
//					isLinkOpen = true;
//					tmpPosition = i + 2;
//				}
//			}
//			else if (isLinkOpen)
//			{
//				if (wikiTxt.charAt(i) == closeBracket && wikiTxt.charAt(i + 1) == closeBracket)
//				{
//					// only adds a point, when there are no "|" or ":" 
//					if (!wikiTxt.substring(tmpPosition, i).contains("|") && !wikiTxt.substring(tmpPosition, i).contains(":"))
//						returnList.add(new Point(tmpPosition, i));
//					
////					System.out.println(wikiTxt.substring(tmpPosition, i));
//					isLinkOpen = false;
//
//				}
//			}
		}
		return returnList;
	}

	/**
	 * 
	 * wikitext in table text is saved as BLOB, 
	 * and BLOB can only be saved with prepared statements
	 * 
	 * prepared statement is opened in init
	 * 
	 * @param wikitxt
	 * @param old_id
	 */
	private void updateReplacedTextInDatabase(String wikitxt, int old_id) 
	{
		
		try
		{
			byte[] textAsByte = wikitxt.getBytes();
			
			stmt.setBytes(1, textAsByte);
			stmt.setInt(2, old_id);
			stmt.executeUpdate();
			
			stmt.clearParameters();
			textAsByte = null;

		}
		catch (SQLException e)
		{
			try
			{
				bwLogger.append("failure update updateReplacedTextInDatabase: " + e.getMessage());
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			System.err.println("updateReplacedTextInDatabase: ");
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
	private String replaceTheOpenAndCloseTagsFromText(String wikiTxt, String startTag, String endTag)
	{
		String sReturn;
		String before = "";
		String behind = "";
		int posStart = 0;

		int posBeginn = -1;
		int posEnd = -1;
		
		posBeginn = wikiTxt.indexOf(startTag, posStart);

		if (posBeginn > -1)
		{
			posEnd = wikiTxt.indexOf(endTag, posBeginn + 1);

			if (posEnd > -1)
			{
				
				posEnd = posEnd+endTag.length();

				before = wikiTxt.substring(posStart, posBeginn);  
				behind = wikiTxt.substring(posEnd, wikiTxt.length());
			}

			// rekursion with remaining string
			if (posEnd + 1 < wikiTxt.length())
			{
				behind = replaceTheOpenAndCloseTagsFromText(behind, startTag, endTag);
			}

			sReturn = before + behind;
		}
		else
		{
			sReturn = wikiTxt;
		}

		return sReturn;
	}

	/**
	 * 
	 * output: csv for mallet
	 */
	private void appendToBW(String csv)
	{
		try
		{
			bwCSVOrigText.append(csv);
//			bwCSVOrigText.flush(); // TODO flush machen? 
		}
		catch (IOException e)
		{
			System.err.println("append to bw ");
			e.printStackTrace();
		}
	}
	
	/**
	 * output: logging 
	 */
	private void appendLogging(String loggingLine)
	{
		try
		{
			bwLogger.append( loggingLine);
			bwLogger.append("\n");
//			bwLogger.flush(); //TODO flush machen? 
		}
		catch (IOException e)
		{
			System.err.println("appendLogging " + this.getName() );
//			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param id_title
	 * @return parsed Wikitext as string
	 * @throws Exception
	 */
	private WikiArticle getParsedWikiArticle(WikiIDTitlePair id_title) 
	{
		String wikiText = "";
		
		try
		{
			wikiText = getWikiTextWithRevID(id_title);
			
			// double parsing, one for the Wikitext and one for text
			return new WikiArticle(parse(wikiText, id_title, true), id_title.getOld_id(), wikiText, id_title.getWikiTitle(), 1, parse(wikiText, id_title,false)) ;
		}
		catch (Exception e)
		{
			System.err.println("Failure getParsedWikiText " + id_title.getWikiTitle() + " " + id_title.getOld_id() );
			
//			if (debug){
//				e.printStackTrace();
//			}
			return new WikiArticle("Failure in getParsedWikiArticle(): " + e.getMessage(), id_title.getOld_id(), "",id_title.getWikiTitle(),-2 , "") ;
		}
	}
	
	private String parse(String wikiOrigText, WikiIDTitlePair id_title, boolean csvOrReadable) throws Exception
	{
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
	
	
	@Override
	public void run()
	{

		try
		{
			WikiArticle w;
			WikiTextToCSVForeward textTocsv ; 			
//			WikiTextToCsvbackward textTocsv ;
			
			
			
			this.init();
			db.executeUpdateQuery("START TRANSACTION;");
			
			for (int i = 0; i < articleNames.size(); i++)
			{
				w = getParsedWikiArticle(articleNames.get(i));

				// fürs logging
				if (w.getFailureId() <= 0)
				{
										// as failuretext
					this.appendLogging(w.getWikiTitle() + " " + w.getOldID() + "\t\t\t failure_id <= 0 , " + w.getParsedWikiText());
				}
				else
				{
						// temporäre Ausgabe, zur Veranschaulichung nur wenn ein Artikel geladen wird
						if (onlyOneOutput)
						{
							SupporterForBothTypes s = new SupporterForBothTypes(prop);
							s.printIntoFile(w.getParsedWikiText(), "outputparsed.txt");
							s.printIntoFile(w.getWikiOrigText(), "inputorig.txt");
							s.closeDBConnection();
						}

						try
						{
							// malletfile with position in wikitext
							textTocsv = new WikiTextToCSVForeward(w, bwLogger);
//							textTocsv = new WikiTextToCsvbackward(w, bwLogger);
							
							appendToBW(textTocsv.getCSV());
							textTocsv = null;

							// saving the readable text for import into the database for displaying the normalized text in the UI
							bwInputSQLParsedText.append(w.getOldID() + " ;\"" + w.getWikiTitle() + " \";\"" + w.getParsedWikiTextReadable() + " \"" + endOfDocumentInSQLOutput);
							
//							bwLogger.append(w.getWikiTitle() + " parsed.");
//								System.out.println(w.getWikiTitle() + "\t" + this.getName() + "\t" + System.currentTimeMillis() );
//							bwLogger.append(w.getWikiTitle() + "\t" + this.getName() + "\t" + System.currentTimeMillis() + "\n");
//							System.out.println(w.getWikiTitle() + "\t" + this.getName() + "\t" + System.currentTimeMillis() );
							
						}
						catch (Exception e)
						{
							bwLogger.append(w.getWikiTitle() + " " + w.getOldID() + " " + this.getClass()+  ".java :failure in preparing original wikitext with wikitextocsv for mallet, " + e.getMessage() + "\n");
							if (debug){
								e.printStackTrace();
							}
						}
				}
				
				w = null;
			}

			bwCSVOrigText.flush();
			bwCSVOrigText.close();

			bwInputSQLParsedText.flush();
			bwInputSQLParsedText.close();
			
			bwLogger.flush();
			bwLogger.close();
			
			stmt.close();
			db.executeUpdateQuery("COMMIT;");
			
			
		}
		catch (Exception e)
		{
			System.err.println(this.getClass()+".run - " + this.getName());

			// for finishing the files
			try
			{
				bwLogger.append("Failure in run: " + this.getName() + " "  + e.getMessage() + e.getStackTrace() );
				
				bwInputSQLParsedText.flush();
				bwInputSQLParsedText.close();
				bwCSVOrigText.flush();
				bwCSVOrigText.close();
				bwLogger.flush();
				bwLogger.close();
				
				db.shutdownDB();
			}
			catch (IOException e1) // catchblock von flush & close
			{
				if (debug)
					e1.printStackTrace();
			}
			catch (SQLException e2)
			{
				if (debug)
					e2.printStackTrace();
			}

			if (debug)
			{
				e.printStackTrace();
			}
		}
	}
}