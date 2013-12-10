package cc.topicexplorer.plugin.duplicates.preprocessing.implementation;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.csvreader.CsvReader;

import org.apache.log4j.Logger;
import org.jgrapht.alg.util.UnionFind;


public class Duplicates {

	protected cc.topicexplorer.database.Database database;

	private String csvFilePath;
	private CsvReader inCsv;
//	private String[] headerEntries;
	private int frameSize = 30;
	private UnionFind<String> unionFind;
	private HashSet<Frame> duplicateFrames;

	private Logger logger;

	private TimeKeeper timeKeeper;
	
	private boolean consoleDebugOutput = false;

	
	public cc.topicexplorer.database.Database getDB() {
		return database;
	}

	public void setDB( cc.topicexplorer.database.Database database ) {
		this.database = database;
	}
	
	

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	

	public int getFrameSize() {
		return frameSize;
	}

	public void setFrameSize(int frameSize) {
		this.frameSize = frameSize;
	}

	public String getCsvFilePath() {
		return csvFilePath;
	}

	public void setCsvFilePath(String csvFilePath) {
		this.csvFilePath = csvFilePath;
	}
	
	
	
	
	
	public void findDuplicates()
	{
		timeKeeper = new TimeKeeper();
		timeKeeper.startTimer();
		
		// memorize frames that appear at least twice
		HashSet<String> duplicateFrameHashes = new HashSet<String>();

		duplicateFrames = new HashSet<Frame>();

		// memorize document IDs, that have to be reprocessed again at the end
		// (these are the documents where duplicate frames appeared for the first time
		//  thus they were not processed yet)
		HashSet<Integer> reprocessDocuments = new HashSet<Integer>();

		unionFind = new UnionFind<String>(new HashSet<String>());


		try {
			// Step 1 - process all documents once
			processCsvStep1(duplicateFrameHashes, reprocessDocuments);

			// output timer stuff
			if ( this.consoleDebugOutput )
			{
				System.out.println("time after first processing: " + timeKeeper.getElapsedTimeInSeconds());

				System.out.println("duplicate frame hashes: " + new ArrayList<String>(duplicateFrameHashes).size());
				System.out.println("frames to insert: " + duplicateFrames.size());
				System.out.println("documents to reprocess: " + reprocessDocuments.size());

				System.out.println("---");
			}

			// Step 2 - reprocess documents that could not be processed in step 1,
			// as at the time of their processing it was not known that they contain duplicates
			processCsvStep2(duplicateFrameHashes, reprocessDocuments);
			
		} catch (IOException e) {
//			logger.fatal("Input CSV-File couldn't be read - maybe the path is incorrect");
			e.printStackTrace();
		}


		if ( this.consoleDebugOutput )
		{
			// output timer stuff				
			System.out.println("time after second processing: " + timeKeeper.getElapsedTimeInSeconds());

			System.out.println("duplicate frame hashes: " + new ArrayList<String>(duplicateFrameHashes).size());
			System.out.println("frames to insert: " + duplicateFrames.size());

			System.out.println("---");
		}
	}
	
	
	
	

	public void writeDuplicatesToDB()
			throws SQLException {
		// write frame data to DB
//				dbManager.RunQuery("TRUNCATE TABLE DUPLICATES$DUPLICATES");
		this.database.executeQuery("TRUNCATE TABLE DUPLICATES$DUPLICATES");

		Iterator<Frame> fIt = duplicateFrames.iterator();
		
		// status output stuff
		final int nextOutputStep = 10;
		int nextOutput = nextOutputStep; // percent
		int counter = 0;
		
		// iterate
		while (fIt.hasNext())
		{
			if ( this.consoleDebugOutput )
			{
				// status output stuff
				counter++;
				if ( 100. * counter / duplicateFrames.size() > nextOutput )
				{
					System.out.println("progress: " + nextOutput + "%");
					nextOutput += nextOutputStep;
				}
			}
			
			
			// write frame to DB
			Frame currentFrame = fIt.next();
			
			String groupHash;
			
			// get parent (group) hash
			try {
				groupHash = unionFind.find(currentFrame.getMD5Hash());
			}
			// no parent hash found? -> use own hash as parent hash
			catch (IllegalArgumentException e) {
				groupHash = currentFrame.getMD5Hash();
			}
			
			String query = "INSERT INTO DUPLICATES$DUPLICATES (MD5SUM, DOCUMENT_ID, START_POSITION, END_POSITION, GROUP_ID) " +
								"VALUES ('" + currentFrame.getMD5Hash() + "', '" + currentFrame.getDocID() +
									"', '" + currentFrame.getStartPos() + "', '" + currentFrame.getEndPos() +
									"', '" + groupHash + "')";
			
//			System.out.println(query);

			this.database.executeUpdateQuery( query );
		}
		
		
		if ( this.consoleDebugOutput )
		{
			System.out.println("finished.");

			// output timer stuff
			System.out.println("time after inserting into DB: " + timeKeeper.getElapsedTimeInSeconds());
		}
	}

	
	
	private void processCsvStep1(
			HashSet<String> duplicateFrameHashes,
			HashSet<Integer> reprocessDocuments) throws IOException
	{
		// memorize the document ID of a frame that appears for the first time
		HashMap<String,Integer> frameFirstAppearanceDoc = new HashMap<String,Integer>();


		FrameCreator frameCreator = new FrameCreator( frameSize );

		int prevDocID = -1;
		Frame prevFrame = null;
		
		openInCsvReader();
		readInCsvHeader();

		while (inCsvReadRecord())
		{
			int docID = extractDocIDFromCsvLine();
			FrameToken frameToken = extractFrameTokenFromCsvLine();
			
			// new document
			if ( prevDocID != docID )
			{
				prevDocID = docID;
				
				frameCreator = new FrameCreator( frameSize );
				
				prevFrame = null;
			}
			
			frameCreator.addToken( docID, frameToken );
			
			// currentFrame
			Frame currentFrame = frameCreator.getLastFrame();
			
			if ( currentFrame != null )
			{
				String currentMD5Hash = currentFrame.getMD5Hash();
				
				// is duplicate
				if	( frameFirstAppearanceDoc.containsKey(currentMD5Hash) )
				{
					duplicateFrames.add( currentFrame );
					
					duplicateFrameHashes.add( currentMD5Hash );
					reprocessDocuments.add( frameFirstAppearanceDoc.get(currentMD5Hash) );
					
					checkFramesForOverlapAndPossiblyUnionThem(prevFrame, currentFrame);
					
					prevFrame = currentFrame;
				}
				else
				{
					frameFirstAppearanceDoc.put(currentMD5Hash, docID);
					
					prevFrame = null;
				}
			}
			
		}
		
		closeInCsvReader();
	}

	
	
	private void processCsvStep2(
			HashSet<String> duplicateFrameHashes,
			HashSet<Integer> reprocessDocuments) throws IOException
	{

		FrameCreator frameCreator = new FrameCreator( frameSize );

		int prevDocID = -1;
		Frame prevFrame = null;

		openInCsvReader();
		readInCsvHeader();

		while (inCsvReadRecord())
		{
			if ( reprocessDocuments.size() == 0 )
			{
				break;
			}


			int docID = extractDocIDFromCsvLine();

			
			// new document
			if ( prevDocID != docID )
			{
				if ( reprocessDocuments.contains( prevDocID ) )
				{
					reprocessDocuments.remove( prevDocID );
				}

				prevDocID = docID;
				
				frameCreator = new FrameCreator( frameSize );
				
				prevFrame = null;
			}


			if ( !reprocessDocuments.contains( docID ) )
			{
				continue;
			}

			
			FrameToken frameToken = extractFrameTokenFromCsvLine();
			
			
			frameCreator.addToken( docID, frameToken );
			
			// currentFrame
			Frame currentFrame = frameCreator.getLastFrame();
			
			if ( currentFrame != null )
			{
				// is duplicate
				// in this case the same frame might be added twice.
				// as duplicateFrames is a HashSet, this will not be a problem.
				if ( duplicateFrameHashes.contains( currentFrame.getMD5Hash() ) )
				{
					duplicateFrames.add( currentFrame );

					checkFramesForOverlapAndPossiblyUnionThem(prevFrame, currentFrame);
					
					prevFrame = currentFrame;
				}
			}
		}
		
		closeInCsvReader();
	}


	
	// CSV structure
	// "DOCUMENT_ID";"POSITION_OF_TOKEN_IN_DOCUMENT";"TERM";"TOKEN";"WORDTYPE$WORDTYPE"

	private int extractDocIDFromCsvLine() throws IOException
	{
		return Integer.parseInt( inCsv.get("DOCUMENT_ID") );
	}

	private FrameToken extractFrameTokenFromCsvLine() throws IOException
	{
		int posOfToken = Integer.parseInt( inCsv.get("POSITION_OF_TOKEN_IN_DOCUMENT") );
		String token = inCsv.get("TOKEN");
		
		return new FrameToken( posOfToken, token );
	}
	
	
	

	private void checkFramesForOverlapAndPossiblyUnionThem(Frame prevFrame,
			Frame currentFrame)
	{
		if ( prevFrame == null || currentFrame == null )
			return;
		
		// check for overlap
		// if overlap -> into UnionFind
		if ( currentFrame.getStartPos() <= prevFrame.getEndPos() )
		{
			addToUnionFindStructureAndUnion(unionFind,
					currentFrame.getMD5Hash(), prevFrame.getMD5Hash());
		}
	}


	private void addToUnionFindStructureAndUnion(
			UnionFind<String> unionFind, String currentMD5Hash,
			String prevMD5Hash) {
		unionFind.addElement(currentMD5Hash);
		unionFind.addElement(prevMD5Hash);
		
		unionFind.union(currentMD5Hash, prevMD5Hash);
	}

	
	
	
	
	
	
	

	private void openInCsvReader() {
		try {
			inCsv = new CsvReader(new FileInputStream(csvFilePath), ';', Charset.forName("UTF-8"));
		} catch (FileNotFoundException e) {
			logger.fatal("Input CSV-File couldn't be read - maybe the path is incorrect");
			e.printStackTrace();
			System.exit(3);

		}
	}

	private void closeInCsvReader() {
		inCsv.close();
	}

	private void readInCsvHeader() {
		try {
			inCsv.readHeaders();
//			headerEntries = inCsv.getHeaders();
			// for (int i = 0; i < h.length; i++) {
			// this.headerEntries.add(h[i]);
			// }
		} catch (IOException e) {
			logger.fatal("CSV-Header not read");
			e.printStackTrace();
			System.exit(2);
		}

	}

	private boolean inCsvReadRecord() {
		Boolean result = false;
		try {
			result = inCsv.readRecord();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return result;
	}

	
	
}
