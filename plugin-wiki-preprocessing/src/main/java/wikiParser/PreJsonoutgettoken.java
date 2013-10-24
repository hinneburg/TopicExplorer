package wikiParser;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 
 * 	 die Daten werden von unterschiedlichen Datenbank bezogen: 
 *	 wird über wikiconfig.ini gesteuert
 * 		- Tabelle DOCUMENT_TERM_TOPIC und TOPIC aus DatabasePreprocessing weil sonst alles sinnlos kopiert werden müsste
 * 		- Datenbank mit den bereinigten Wikidaten, die seit Preprocessing nicht mehr verändert wurden (schreibgeschützes Wiki)
 * 		
 * 
 *
 */
public class PreJsonoutgettoken
{

	private List<WikiIDTitlePair> getAllArticlesInDocumentTermTopicFromPreprocessingDatabase(Supporter s)
	{

		List<WikiIDTitlePair> list = new ArrayList<WikiIDTitlePair>();
		try
		{

			String databasePreprocessing = s.getDatabase().getProperties().getProperty("databasePreprocessing");
			
			String sql = "SELECT DOCUMENT_ID FROM " + databasePreprocessing+".DOCUMENT_TERM_TOPIC GROUP BY DOCUMENT_ID";

			ResultSet rs = s.getDatabase().executeQuery(sql);

			while (rs.next())
			{
				list.add(new WikiIDTitlePair(Integer.valueOf(rs.getString(1)), ""));
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;

	}

	
	
	/**
	 * 
	 */
	private void start() throws InterruptedException
	{
		Supporter s = new Supporter(true); // target-database, different from other db, otherwise the corrected articles where overwritten 

		Integer multiplicator; 		Integer numberOfAvailableProcesssors;		ArrayList<WikiIDTitlePair> newList ;
		
		multiplicator = Integer.valueOf(s.getDatabase().getProperties().getProperty("multiplicatorForNumberOfThreads"));
		numberOfAvailableProcesssors = Integer.valueOf((String) s.getDatabase().getProperties().get("numberOfParallelThreads"));

		// split the whole number of articles into parts
		ArrayList [] splittedWikiIDTitleArray = s.splitIntoArray(numberOfAvailableProcesssors * multiplicator, getAllArticlesInDocumentTermTopicFromPreprocessingDatabase(s));
		
		s.closeDBConnection();
		
		// declare threadpool		
		Integer listLenght = splittedWikiIDTitleArray.length ;
		Integer processorMin = numberOfAvailableProcesssors;
		Integer processorMax = numberOfAvailableProcesssors;
		long keepAliveTime = 1; 
		final LinkedBlockingQueue workQueue = new LinkedBlockingQueue();
		ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(processorMin, processorMax, keepAliveTime, TimeUnit.DAYS, workQueue);
		ThreadGroup group = new ThreadGroup("wikiJsonoutgettoken");

		// start the threads
		for (Integer i = 0; i < splittedWikiIDTitleArray.length; i++)
		{
			newList = (ArrayList<WikiIDTitlePair>) splittedWikiIDTitleArray[i].clone();
			poolExecutor.execute(new JsonoutgetToken(newList, group, "threadpart-" + new Integer(i+1) + "of" + listLenght));
			newList = null;
		}
	
		splittedWikiIDTitleArray = null;
		
		poolExecutor.shutdown();
		//wait till shutdown is finished
		while (!poolExecutor.awaitTermination(30, TimeUnit.SECONDS))
		{
			//do nothing
		}
		poolExecutor = null;
	}
	
	public static void main(String [] args)
	{
		
		Stopwatch stopWatch = new Stopwatch();
		String sFunction = "Einfaerbung";
		stopWatch.startStopping(sFunction);
		
		try
		{
			PreJsonoutgettoken h = new PreJsonoutgettoken();
			h.start();
		}
		catch (Exception e)
		{
			System.err.println("PreJsonoutgettoken.java \n");
			e.printStackTrace();
		}
		
		stopWatch.stopStoppingAndDoOutputToConsole(sFunction);		
	}
}