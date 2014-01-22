package wikiParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import tools.WikiIDTitlePair;

/**
 * 
 * die Daten werden von unterschiedlichen Datenbank bezogen: wird über
 * wikiconfig.ini gesteuert - Tabelle DOCUMENT_TERM_TOPIC und TOPIC aus
 * DatabasePreprocessing weil sonst alles sinnlos kopiert werden müsste -
 * Datenbank mit den bereinigten Wikidaten, die seit Preprocessing nicht mehr
 * verändert wurden (schreibgeschützes Wiki)
 * 
 * 
 * 
 */
public class MediawikiColorationAction_EntryPointForParallelisation {

	private final Properties prop;

	public MediawikiColorationAction_EntryPointForParallelisation(Properties prop) {
		this.prop = prop;
	}

	private List<WikiIDTitlePair> getAllArticlesInDocumentTermTopicFromPreprocessingDatabase(SupporterForBothTypes s) {

		List<WikiIDTitlePair> list = new ArrayList<WikiIDTitlePair>();
		try {

			String databasePreprocessing = prop.getProperty("database.DB");

			// debug
			String sqlDebug = "SELECT COUNT(*) FROM (SELECT COUNT(DOCUMENT_ID) FROM " + databasePreprocessing
					+ ".DOCUMENT_TERM_TOPIC GROUP BY DOCUMENT_ID) a ";
			ResultSet rsDebug = s.getDatabase().executeQuery(sqlDebug);

			if (rsDebug.next()) {
				System.out.println("Wiki_coloration: number of documents " + rsDebug.getString((1)));
			}
			sqlDebug = null;
			rsDebug = null;

			String sql = "SELECT DOCUMENT_ID FROM " + databasePreprocessing
					+ ".DOCUMENT_TERM_TOPIC GROUP BY DOCUMENT_ID";

			ResultSet rs = s.getDatabase().executeQuery(sql);

			while (rs.next()) {
				list.add(new WikiIDTitlePair(Integer.valueOf(rs.getString(1)), ""));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;

	}

	/**
	 * 
	 */
	private void startThreadpool() throws InterruptedException {
		SupporterForBothTypes s = null;
		try {
			s = new SupporterForBothTypes(prop, true);
		} catch (SQLException e) {
			System.err.println("Database supporter could not be constructed.");
			e.printStackTrace();
		} catch (InstantiationException e) {
			System.err.println("Database supporter could not be constructed.");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println("Database supporter could not be constructed.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("Database supporter could not be constructed.");
			e.printStackTrace();
		} // target-database,
			// different
			// from
			// other
			// db,
			// otherwise
			// the
			// corrected
			// articles
			// where
			// overwritten

		Integer multiplicator;
		Integer numberOfAvailableProcesssors;
		ArrayList<WikiIDTitlePair> newList;

		multiplicator = Integer.valueOf(prop.getProperty("Wiki_multiplicatorForNumberOfThreads"));
		numberOfAvailableProcesssors = Integer.valueOf(prop.getProperty("Wiki_numberOfParallelThreads"));

		// split the whole number of articles into parts
		ArrayList[] splittedWikiIDTitleArray = s.splitIntoArray(numberOfAvailableProcesssors * multiplicator,
				getAllArticlesInDocumentTermTopicFromPreprocessingDatabase(s));

		try {
			s.closeDBConnection();
		} catch (SQLException e) {
			System.err.println("Database could not be closed.");
			e.printStackTrace();
		}

		// declare threadpool
		Integer listLenght = splittedWikiIDTitleArray.length;
		Integer processorMin = numberOfAvailableProcesssors;
		Integer processorMax = numberOfAvailableProcesssors;
		long keepAliveTime = 1;
		final LinkedBlockingQueue workQueue = new LinkedBlockingQueue();
		ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(processorMin, processorMax, keepAliveTime,
				TimeUnit.DAYS, workQueue);
		ThreadGroup group = new ThreadGroup("wikiJsonoutgettoken");

		// start the threads
		for (Integer i = 0; i < splittedWikiIDTitleArray.length; i++) {
			newList = (ArrayList<WikiIDTitlePair>) splittedWikiIDTitleArray[i].clone();
			poolExecutor.execute(new MediawikiColorationParallelisation(newList, group, "threadpart-"
					+ new Integer(i + 1) + "of" + listLenght, prop));
			newList = null;
		}

		splittedWikiIDTitleArray = null;

		poolExecutor.shutdown();
		// wait till shutdown is finished
		while (!poolExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
			// do nothing
		}
		poolExecutor = null;
	}

	public void start() throws InterruptedException {
		MediawikiColorationAction_EntryPointForParallelisation h = new MediawikiColorationAction_EntryPointForParallelisation(
				prop);
		h.startThreadpool();
	}

	private static Properties forLocalExcetution() throws IOException {

		Properties prop;
		String fileName = "src/test/resources/localwikiconfig.ini";

		File f = new File(fileName);
		if (f.exists()) {
			prop = new Properties();
			// prop.load(this.getClass().getResourceAsStream("/config.ini"));

			FileInputStream fis = new FileInputStream(fileName);

			prop.load(fis);

			return prop;

		} else {
			System.err.print(f.getAbsolutePath() + "\n");
			throw new FileNotFoundException(f + "not found.");
		}

	}

	public static void main(String[] args) {

		try {
			MediawikiColorationAction_EntryPointForParallelisation p = new MediawikiColorationAction_EntryPointForParallelisation(
					forLocalExcetution());
			p.start();
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		} catch (InterruptedException intEx) {
			intEx.printStackTrace();
		}

	}
}