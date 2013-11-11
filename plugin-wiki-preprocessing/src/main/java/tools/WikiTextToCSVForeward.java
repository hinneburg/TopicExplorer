package tools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import tools.PointInteger;
import tools.WikiArticle;


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
public class WikiTextToCSVForeward
{

	private final String wikiOrigText;
	private final String wikiParsedText;
	private final Integer old_id ;
	private final String wikiTitle;
	private final String wikiParsedTextReadable;
	
	private List<String> tokensParsedText = new ArrayList<String>();
	private List<Integer> startPositionsWikiText = new ArrayList<Integer>();
	private List<Integer> startPositionsReadableText = new ArrayList<Integer>();
	
	private List<PointInteger> posBracketsBox ;
	private Integer intPointerBracketsBox;
	
	private BufferedWriter bwlogger ;
	
	
	
	
	public WikiTextToCSVForeward(WikiArticle w){
		
		this.wikiOrigText = new String (w.getWikiOrigText());
		this.wikiParsedText	= w.getParsedWikiText();
		this.old_id = w.getOldID();
		this.wikiTitle = w.getWikiTitle();
		this.wikiParsedTextReadable = w.getParsedWikiTextReadable();
	}

	public WikiTextToCSVForeward(WikiArticle w, BufferedWriter bwLogger)
	{
		this(w);
		this.bwlogger = bwLogger;
	}
	
		
	private void tokenizeReadableTextWithFixedTokenSequence (String text, List<String> tokenSequence)
	{
		// assumption: every parsed word of the wikitext can be found in the normalised text 
		// readable text and parsed original text are not equal and could differ in some parts like interpunction and prefix postfix of wiki-links)
		
		try
		{
			Iterator<String> itToken = tokenSequence.iterator();
			Integer position = 0;
			Integer savedPosition = 0;
			String token;
			
			while (itToken.hasNext())
			{
				token = itToken.next();
				position = text.substring(savedPosition, text.length()).indexOf(token);  // every time the search-string becomes shorter
				startPositionsReadableText.add(position+savedPosition);
				
				if (position == -1 && itToken.hasNext())
				{
					try
					{
						bwlogger.append("tokenize readable text: textposition not found, "+ this.wikiTitle + " " + token ); // perhaps it doesn't come to this point
					}
					catch (IOException e)
					{
						System.err.println("bwlogger not found, tokenize readable text textposition not found");
						e.printStackTrace();
					}
				}
				else
				{
					savedPosition = position+savedPosition;
				}
			}
		}
		catch (Exception e)
		{
			System.err.println("failure in tokenizeReadableTextWithFixedTokenSequence, does only belongs to the tokenisation of the readable text. " + e.getMessage());
//			e.printStackTrace();
		}
	}
	
	
	private void tokenize() throws Exception{

		posBracketsBox = getBracketedPositionsBoxBrackets();
		
		Scanner scParsed = new Scanner(wikiParsedText); // liegt zeilenweise vor
		Integer savedPosition = 0;
		Integer pos = -1;

		while (scParsed.hasNextLine())
		{
			// read line
			String tmpLine = scParsed.nextLine();
			if (tmpLine.trim().length() > 0)
			{
				// find the line in the original wiki-text
				pos = wikiOrigText.substring(savedPosition, wikiOrigText.length()-1).indexOf(tmpLine); 
				
				if (pos > -1)
				{

//					System.out.println(pos + " " + tmpLine + " old " );
					pos = checkIfPositionOfWordIsWithinBoxBrackets(pos+savedPosition, tmpLine);
//					System.out.println(pos + " " + tmpLine  + " new ");
					
					// takes the last position of the line, iterates all elements, saves the position of every token from the text and returns the new startposition (the last  )  
					savedPosition = splitForOutputAndReturnNewPosition(tmpLine, pos);
				}
				else
				{
					//error output to logger
					bwlogger.append("catched error, "+ wikiTitle + ": "+ old_id + " *"+tmpLine + "*:"  + (new Integer(pos+savedPosition)) + ": last_savedposition= " + savedPosition + "\n");
				}
			}
			else
			{
				savedPosition = savedPosition + 1;	// new line also counts as one character in utf-8 	
			}
		}
		scParsed = null;
	}
	

	private Integer checkIfPositionOfWordIsWithinBoxBrackets(Integer pos, String tmpLine)
	{
		if (intPointerBracketsBox==(posBracketsBox.size())) 
			return pos;
		
		Integer posOfFoundedWord = -1;
		Integer posRemainingText = pos; 
		Integer posSummedUp ;
		Integer posNew;
		
		if (posBracketsBox.get(intPointerBracketsBox).getStartPoint() > pos )
		{
			return pos ;			
		}
		// wenn er genau drin liegt, neu suchen
		else if (posBracketsBox.get(intPointerBracketsBox).getStartPoint() <= pos && posBracketsBox.get(intPointerBracketsBox).getEndPoint() >= pos)
		{
//		rekursiv

			pos++;
			posNew = wikiOrigText.substring(pos, wikiOrigText.length()).indexOf(tmpLine);
			
			if (posNew > -1)
			{
				posSummedUp  = posRemainingText + posNew + 1;
				posOfFoundedWord = 	checkIfPositionOfWordIsWithinBoxBrackets(posSummedUp, tmpLine);
				
			}else 
			{
				posOfFoundedWord = -1;
			}
		}
		else if (posBracketsBox.get(intPointerBracketsBox).getEndPoint() < pos )
		{
			if (intPointerBracketsBox < posBracketsBox.size()-1)
			{
				Integer tmp = intPointerBracketsBox + 1;
				if (posBracketsBox.get(tmp).getStartPoint() > pos)
				{
					intPointerBracketsBox++;
					posOfFoundedWord = pos;
				}
				// rekursiv
				else if (posBracketsBox.get(tmp).getStartPoint() < pos )
				{
					intPointerBracketsBox++;
					posOfFoundedWord = checkIfPositionOfWordIsWithinBoxBrackets(pos, tmpLine);
				}
			}
		}
		
		if (posOfFoundedWord != -1 )
		{
			return posOfFoundedWord;	
		}
		else
		{
			return pos; 
		}
		
		
////	/*
////	  pointer muss immer in richtiger und aktueller Position sein
////	  d.h. wenn die gefundene Position innerhalb eines Klammernbereiches liegt, 
////	  dann wird temporär der vorherige Bereich angeguckt und überprüft,
////	  ob die Position nicht innerhalb eines Klammernbereiches liegt, 
////	  wenn dies so ist, dann Treffer und return,
////	  rekursiv
////	 */

	}
	
	
	// save positions of linktargets , between boxbracket and | , not the linktext
	// pictures could have "|" too
	private List <PointInteger> getBracketedPositionsBoxBrackets()
	{
		Integer tmpx = 0;
		Boolean boolBracketOpen = false;
				
		List <PointInteger> list = new ArrayList<PointInteger>();
		
		// rather a test
		if (-1 == wikiOrigText.indexOf("[[", 0)){
			intPointerBracketsBox = 0 ;
			return list;
		}
			
		
		// length -1, because char at i + 1
		for (Integer i = 0 ; i < wikiOrigText.length()-1; i++) 
		{

			if (wikiOrigText.charAt(i) == '[' && wikiOrigText.charAt(i + 1) == '[')
			{
				tmpx = i;
				boolBracketOpen = true;
			}
			else if (wikiOrigText.charAt(i) == '|')
			{
				if (boolBracketOpen)
				{
					list.add(new PointInteger(tmpx, i));
					boolBracketOpen = false;
				}
			}
		}
		
		intPointerBracketsBox = 0;
		return list;
	}
	
	private Integer splitForOutputAndReturnNewPosition(String tmpLine, Integer posOfLineInOriginalText)
	{
		Integer endToken = -1;
		Integer startToken = 0;
		
		while (startToken < tmpLine.length())
		{
			for (startToken = endToken + 1; startToken < tmpLine.length(); startToken++)
			{
				if (Character.toString(tmpLine.charAt(startToken)).matches("\\p{L}"))
					break;
			}
			for (endToken = startToken; endToken < tmpLine.length(); endToken++)
			{
				if (Character.toString(tmpLine.charAt(endToken)).matches("\\P{L}"))
					break;
			}
			if (startToken < tmpLine.length())
			{

				// only words with length greater than 1 are parsed
				if (tmpLine.substring(startToken, endToken).length() > 1)
				{
					tokensParsedText.add(tmpLine.substring(startToken, endToken));
					startPositionsWikiText.add(startToken + posOfLineInOriginalText);
				}
			}
		}
		return posOfLineInOriginalText + tmpLine.length();   
	}

	public String getCSV() throws Exception
	{

		tokenize();
		
		tokenizeReadableTextWithFixedTokenSequence(wikiParsedTextReadable, tokensParsedText);
						
		StringBuilder sb = new StringBuilder();

		Iterator<String> itToken = tokensParsedText.iterator();
		Iterator<Integer> itSeqWikitext = startPositionsWikiText.iterator();
		Iterator<Integer> itSeqReadable = startPositionsReadableText.iterator();
		
		while (itToken.hasNext() && itSeqWikitext.hasNext())
		{
			String token = itToken.next();
			Integer seqWikitext = itSeqWikitext.next();
			Integer seqReadable = itSeqReadable.next();
			
			sb.append("\"" + old_id + "\";" +
					"\"" + String.valueOf(seqReadable)+ "\";" +
					  "\"" + token.toLowerCase() + "\";" +
					  "\"" + token + "\";" +
					  "\"" + String.valueOf(seqWikitext) + "\";" +
					  "\n");

			// temp assert which is working
//			if (seq > -1 ){
//				Assert.assertEquals(token, wikiOrigText.substring(seq, seq+ token.length()));
//			}
		}

		return sb.toString();

	}
}
