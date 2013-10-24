package wikiParser;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//import org.apache.log4j.Appender;
//import org.apache.log4j.ConsoleAppender;
//import org.apache.log4j.Logger;

import wikiParser.Supporter;

public class Prehelper
{

	private Database db;
	private Properties prop;
	private Stopwatch stopWatch;
	
	private final String fileSeparator = System.getProperty("file.separator");

	private final String fileNameInputSQLParsed = "inputsql.csv";
	private final String fileNameInputMallet = "inputmallet.csv";
	private final String fileNameLogging = "logging.txt";
	
//	final static Logger logger = Logger.getRootLogger();
	
	public Prehelper(Properties prop)
	{
		this.prop=prop;
		
		try
		{
			this.init();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void init() throws Exception
	{
		db = new Database(prop);
		
		String outputFolder = prop.getProperty("Wiki_outputFolder"); 
		File dir = new File(outputFolder);
		dir.mkdir();

		dir = new File(outputFolder + fileSeparator + "temp");
		dir.mkdir();
		

		stopWatch = new Stopwatch();
	}
	
	

	
	/**
	 * reads the output-directory and joins the outputfiles into one 
	 */
	private void joinTheOutputsAndDeleteTempFilesInTempFolder()
	{

		System.out.println("joining the outputfiles and deleting the temp files");
		
		try
		{
			String outputFolder = prop.getProperty("Wiki_outputFolder");
			
			File tempFolder = new File(outputFolder + fileSeparator + "temp");
			File[] fileList = tempFolder.listFiles();

			File fileInputMallet = new File(outputFolder + fileSeparator+ fileNameInputMallet);
			File fileInputSQLParsed = new File(outputFolder + fileSeparator+ fileNameInputSQLParsed);
			File fileLogging = new File(outputFolder + fileSeparator+ fileNameLogging);
		
			this.mergeDataIntoOneFile(fileInputMallet, fileList, "malletWikiText");
			this.mergeDataIntoOneFile(fileInputSQLParsed, fileList, "inputParsedText");
			this.mergeDataIntoOneFile(fileLogging, fileList, "logging");

						
			if (prop.getProperty("Wiki_deleteTempFolder").equalsIgnoreCase("true"))
			{
				deleteTempOutputFiles(tempFolder);
			}

		}
		catch (Exception e)
		{
			System.err.println("Failure in joinTheOutputsAndDeleteTempFilesInTempFolder.");
			e.printStackTrace();
			System.exit(0);
		}

	}

	private void mergeDataIntoOneFile(File fileName, File[] fileArray, String filterName) throws Exception
	{
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
		BufferedReader br;

		if (filterName.contains("mallet"))
		{
			bw.write("\"DOCUMENT_ID\";\"POSITION_OF_TOKEN_IN_DOCUMENT\";\"TERM\";\"TOKEN\";\"WIKI$POSITION\"\n");
		}
		
		for (int i = 0; i < fileArray.length; i++)
		{
			if (fileArray[i].getName().contains(filterName))
			{
				br = new BufferedReader(new InputStreamReader(new FileInputStream(fileArray[i]), "UTF-8"));

				String line = br.readLine();
				while (line != null)
				{

					bw.write(line);
					bw.write("\n");
					line = br.readLine();
				}
				br.close();
				bw.flush();
			}
		}
		bw.flush();
		bw.close();
	}

	private void deleteTempOutputFiles(File path)
	{
		try
		{
			for ( File file : path.listFiles() )
			    {
			      if ( file.isDirectory() )
			        deleteTempOutputFiles( file );
			      file.delete();
			    }
			    path.delete();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Testfunktion für Ausgabe
	 */
	private void startOnlyOneParsing(Integer old_id) throws InterruptedException, Exception
	{
		System.out.println("Start parsing one article.");
		
		Supporter t = new Supporter(prop);
		
		Vector <WikiIDTitlePair> vec = new Vector<WikiIDTitlePair>(1);
		
		vec.add( t.getOldIdAndWikiTitleFromWikiPageIdFromDatabase(old_id));
		
		t.closeDBConnection();
		
		Helper h = new Helper(vec,prop,null, "Thread-0");
		h.setOnlyOneOutputParameter(true);
		
		h.start();
		h.join();
	}
		
	private void startWithOffsetNew(Integer limitOrAll, Integer numberOfAvailableProcessors, Integer offset , Integer multiplicator) throws InterruptedException
	{
		ArrayList<WikiIDTitlePair> newList;
		
		// get all articles from database 
		Supporter s = new Supporter(prop);
		List <WikiIDTitlePair> inputList = s.getArticlesLimitOffset(limitOrAll, offset);
		s.closeDBConnection();
		
		ThreadGroup group = new ThreadGroup("wikiParsing");
		
		
		//split the whole array into parts
		ArrayList [] splittedWikiIDTitleArray = s.splitIntoArray(numberOfAvailableProcessors*multiplicator, inputList);
		inputList = null;
		
		
		
		//declare threadpool
		Integer listLenght = splittedWikiIDTitleArray.length ;
		Integer processorMin = numberOfAvailableProcessors;
		Integer processorMax = numberOfAvailableProcessors;
		long keepAliveTime = 1; 
		final LinkedBlockingQueue workQueue = new LinkedBlockingQueue();
		ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(processorMin, processorMax, keepAliveTime, TimeUnit.SECONDS, workQueue);
		poolExecutor.setCorePoolSize(numberOfAvailableProcessors);
		
		//start the threads
		for (Integer i = 0; i < splittedWikiIDTitleArray.length; i++)
		{
			newList = (ArrayList<WikiIDTitlePair>) splittedWikiIDTitleArray[i].clone();
			poolExecutor.execute(new Helper(newList, prop, group, "threadpart-" + new Integer(i+1) + "of" + listLenght ));
			newList = null;
		}
		splittedWikiIDTitleArray = null;
		
		// wait till the shutdown is finished, otherwise the program tries to join the unfinished outputs 
		poolExecutor.shutdown();
		while (!poolExecutor.awaitTermination(30, TimeUnit.SECONDS))
		{
//			do nothing
		}
		poolExecutor = null;
	}

	
	public void start()
	{
		
		try
		{			
			Integer limit, offset, old_id, numberOfAvailableProcesssors, multiplicator;
		
			Prehelper p = new Prehelper(prop);		
			
			String functionName = "parsing of wiki-articles incl joining the outputs";
			p.stopWatch.startStopping(functionName);

			//read properties
			numberOfAvailableProcesssors = Integer.valueOf((String) prop.get("Wiki_numberOfParallelThreads"));
			limit = Integer.valueOf(prop.getProperty("Wiki_limit"));
			offset = Integer.valueOf(prop.getProperty("Wiki_offset"));
			
			try
			{
				multiplicator = Integer.valueOf(prop.getProperty("Wiki_multiplicatorForNumberOfThreads"));
				old_id = Integer.valueOf(prop.getProperty("Wiki_old_id"));
			}
			catch (NumberFormatException e)
			{
				System.err.println("perhaps a failure in get property, but it doesn't matter, optinal multiplicator is set to two and old_id to zero");
				multiplicator = 2;
				old_id = 0;
			}
			
//			false bedeutet, dass die csv und sql auf den geparsten Text aufbauen			
//			true bedeutet, dass die csv vom Orignaltext erstellt wird, und originalem Input
//			offset == -1 bedeutet random Artikel

////mainpart			
			// für Testzwecke, kann zusammengeschrupft oder mit extra Option gemacht werden
			if (old_id > 0)
			{
				p.startOnlyOneParsing(old_id);
			}
			else
			{
				p.startWithOffsetNew(limit, numberOfAvailableProcesssors, offset,multiplicator);
			}

			
			p.joinTheOutputsAndDeleteTempFilesInTempFolder();
			p.stopWatch.stopStoppingAndDoOutputToConsole(functionName);
			
		}
		catch (Exception e)
		{
			System.err.println("Prehelper:");
//			logger.error("prehelper " + e.getMessage() + " " + e.getStackTrace());
			e.printStackTrace();
		}
	}	
}
