package wikiParser;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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

	private Properties prop; 
	
	private List<WikiIDTitlePair> getAllArticlesInDocumentTermTopicFromPreprocessingDatabase(Supporter s)
	{

		List<WikiIDTitlePair> list = new ArrayList<WikiIDTitlePair>();
		try
		{

			String databasePreprocessing = prop.getProperty("database.DB");
			
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
	private void startThreadpool() throws InterruptedException
	{
		Supporter s = new Supporter(prop, true); // target-database, different from other db, otherwise the corrected articles where overwritten 

		Integer multiplicator; 		Integer numberOfAvailableProcesssors;		ArrayList<WikiIDTitlePair> newList ;
		
		
		multiplicator = Integer.valueOf((String) prop.getProperty("Wiki_multiplicatorForNumberOfThreads"));
		numberOfAvailableProcesssors = Integer.valueOf((String) prop.getProperty("Wiki_numberOfParallelThreads"));

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
			poolExecutor.execute(new JsonoutgetToken(newList, group, "threadpart-" + new Integer(i+1) + "of" + listLenght,prop));
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
	
	public void start(Properties prop) throws InterruptedException
	{
			PreJsonoutgettoken h = new PreJsonoutgettoken();
			h.prop = prop;
			h.startThreadpool();
	}
}