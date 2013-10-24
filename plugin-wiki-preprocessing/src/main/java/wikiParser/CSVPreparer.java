package wikiParser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 
 * wird für die Outputerstellung für Anzeige im TE benutzt, 
 * um geparsten Text in CSV umzuwandeln, Gegenpart zu wikitexttocsv, da wird mit Originaltext abgeglichen
 * 
 * von Alex
 * statisches rausgenommen
 *
 */
public class CSVPreparer
{
	private List<String> tokens = new ArrayList<String>();
	private List<Integer> startPositions = new ArrayList<Integer>();;

	private WikiArticle w;
	
	private boolean linesReversed;

	public CSVPreparer(WikiArticle w, boolean linesReversed)
	{
		this.w = w;
		this.linesReversed = linesReversed;
	}
	
	public CSVPreparer(WikiArticle w)
	{
		this(w, false);
	}

	private void tokenize(String fulltext)
	{
		Integer endToken = -1;
		Integer startToken = 0;
		
		// for reversing
		List<String> tokens_temp = new ArrayList<String>();
		List<Integer> startPositions_temp = new ArrayList<Integer>();
		
		while (startToken < fulltext.length())
		{
			for (startToken = endToken + 1; startToken < fulltext.length(); startToken++)
			{
				if (Character.toString(fulltext.charAt(startToken)).matches("\\p{L}"))
					break;
			}
			for (endToken = startToken; endToken < fulltext.length(); endToken++)
			{
				if (Character.toString(fulltext.charAt(endToken)).matches("\\P{L}"))
					break;
			}
			if (startToken < fulltext.length())
			{
				
				if (linesReversed)
				{
					if (endToken - startToken > 1)
					{
						tokens_temp.add(fulltext.substring(startToken, endToken));
						startPositions_temp.add(startToken);
					}
				}
				else
				{
					// System.out.println(startToken +" "+ endToken);
					// System.out.println(fulltext.substring(startToken,
					// endToken));
					tokens.add(fulltext.substring(startToken, endToken));
					startPositions.add(startToken);
				}
			}
		}
		
		if (linesReversed)
		{
			// reversing the output
			for (Integer i = tokens_temp.size() - 1; i >= 0; i--)
			{
				tokens.add(tokens_temp.get(i));
				startPositions.add(startPositions_temp.get(i));
			}

			// release the variables
			tokens_temp.clear();
			tokens_temp = null;

			startPositions_temp.clear();
			startPositions_temp = null;
		}
		
		
		
// //SK		
//		Integer endToken = -1;
//		Integer startToken = 0;
//		
//		while (startToken < fulltext.length())
//		{
//			for (startToken = endToken + 1; startToken < fulltext.length(); startToken++)
//			{
//				if (Character.toString(fulltext.charAt(startToken)).matches("\\p{L}"))
//					break;
//			}
//			for (endToken = startToken; endToken < fulltext.length(); endToken++)
//			{
//				if (Character.toString(fulltext.charAt(endToken)).matches("\\P{L}"))
//					break;
//			}
//			if (startToken < fulltext.length())
//			{
//				// System.out.println(startToken +" "+ endToken);
//				// System.out.println(fulltext.substring(startToken, endToken));
//				tokens.add(fulltext.substring(startToken, endToken));
//				startPositions.add(startToken);
//			}
//		}
	}

////	vorbereuitet	bzw mal stehen gelassen
//	private void generateOutputReversed(Integer documentId, StringBuilder sb)
//	{
		
////		---------------------------------------------------------------------------------		
//		// umdrehen geht nicht weil kein getPrevious vorhanden
//		
//		Iterator<String> itToken = tokens.iterator(tokens.size());
//		Iterator<Integer> itSeq = startPositions.iterator(startPositions.size());
//		
////		while (itToken.hasNext() && itSeq.hasNext())
//		while (itToken.hasPrevi..() && itSeq.hasPre...())
//		{
//			String token = itToken.next();
//			sb.append("\""+ documentId.toString() + "\";\"" + 
//					  itSeq.next().toString() + "\";\"" + 
//					  token.toLowerCase() + "\";\"" + 
//					  token.toLowerCase() + "\"" + 
//					  "\n");
//		}
//	//	---------------------------------------------------------------------------------
//		
//	}
	
	public String getCSV()
	{
		StringBuilder sb = new StringBuilder();

		Integer documentId = w.getOldID();
		String fulltext = w.getParsedWikiTextReadable();
//		System.out.println(documentId + ", " + title);

		if (fulltext == null || fulltext.length() == 0)
		{
			return "";
		}
				
		tokenize(fulltext);
		
		
////		generateOutputReversed(documentId, sb);
		
		Iterator<String> itToken = tokens.iterator();
		Iterator<Integer> itSeq = startPositions.iterator();
		while (itToken.hasNext() && itSeq.hasNext())
		{
			String token = itToken.next();
			sb.append("\""+ documentId.toString() + "\";\"" + 
					  itSeq.next().toString() + "\";\"" + 
					  token.toLowerCase() + "\";\"" + 
					  token.toLowerCase() + "\"" + 
					  "\n");
		}
		
		
		return sb.toString();
	}

}