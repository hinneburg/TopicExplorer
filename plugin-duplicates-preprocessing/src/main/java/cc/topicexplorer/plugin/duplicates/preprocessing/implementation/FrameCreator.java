package cc.topicexplorer.plugin.duplicates.preprocessing.implementation;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;


public class FrameCreator {

	private int frameSize = -1;
	
	private LinkedList<FrameToken> tokenList = new LinkedList<FrameToken>();

	private ArrayList<Frame> frames = new ArrayList<Frame>();
	
	
	public FrameCreator( int frameSize )
	{
		this.frameSize = frameSize;
	}
	
	
	
	public void addToken( int docID, int posOfToken, String token )
	{
		this.addToken( docID, new FrameToken(posOfToken, token) );
	}

	
	public void addToken( int docID, FrameToken frameToken )
	{
		this.tokenList.add( frameToken );
		
		if ( this.tokenList.size() == this.frameSize )
		{
			FrameToken firstToken = this.tokenList.get(0);
			FrameToken lastToken = this.tokenList.get(this.tokenList.size()-1);
			
			int startPos = firstToken.getPosition();
			int endPos = lastToken.getPosition() + lastToken.getToken().length();

			String md5Hash = this.getMD5HashOfCurrentFrame();
			
			this.frames.add( new Frame( md5Hash, docID, startPos, endPos ) );
		}
		
		if ( this.tokenList.size() >= this.frameSize )
		{
			this.tokenList.remove();
		}
	}
	
	
	
	public ArrayList<Frame> getFrames()
	{
		return this.frames;
	}
	
	
	public Frame getLastFrame()
	{
		if ( this.frames.size() == 0 )
			return null;
		
		return this.frames.get( this.frames.size() - 1 );
	}
	
	
	
	
	private String getMD5Hash( String str )
	{
		MessageDigest m;
		try
		{
			m = MessageDigest.getInstance("MD5");

			m.reset();
			m.update(str.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1,digest);
			String hashtext = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32 chars.
			while(hashtext.length() < 32 ){
				hashtext = "0"+hashtext;
			}
			return hashtext;
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	
	private String getMD5HashOfCurrentFrame()
	{
		String tempStr = "";
		
		Iterator<FrameToken> i = this.tokenList.iterator();
		while ( i.hasNext() )
		{
			tempStr += i.next().getToken();
		}
		
		tempStr = this.getMD5Hash(tempStr);
		
		return tempStr;
	}
	
}
