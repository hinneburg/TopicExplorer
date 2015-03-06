package cc.topicexplorer.plugin.mecab.initcorpus.implementation.postagger;

import java.util.ArrayList;

public class JPOSTokenPipeline
{
	
	private int queueSize = 20;
	
	private ArrayList<JPOSToken> tokenList;
	

	public JPOSTokenPipeline()
	{
		this.init();
	}

	public JPOSTokenPipeline( int queueSize )
	{
		if ( queueSize < 1 )
			return;
		
		this.queueSize = queueSize;
		
		this.init();
	}
	
	private void init()
	{
		this.tokenList = new ArrayList<JPOSToken>();
	}

	
	
	public void add( JPOSToken token )
	{
		this.tokenList.add(token);
	}
	
	
	
	public boolean full()
	{
		return this.tokenList.size() >= this.queueSize;
	}
	
	public boolean tokensLeft()
	{
		return this.tokenList.size() > 0;
	}
	
	
	/**
	 * 
	 * @return First element of token queue. <i>null</i> if queue is empty.
	 */
	public JPOSToken flush()
	{
		if ( this.tokenList.size() == 0 )
			return null;
		
		JPOSToken flushedToken = this.tokenList.get(0);
		
		this.tokenList.remove(0);
		
		return flushedToken;
	}
	
	
	/**
	 * 
	 * @return Last token of the queue.
	 */
	public JPOSToken getLastToken()
	{
		return getPreviousToken(0);
	}
	
	
	/**
	 * 
	 * @return 2nd last token of the queue.
	 */
	public JPOSToken getPreviousToken()
	{
		return getPreviousToken(1);
	}

	/**
	 * 
	 * @param n Steps to go back.
	 * @return n'th last token of the queue.<br />n = 0 returns the last token.<br />n = 1 returns the 2nd last token.<br />
	 * 			...<br />
	 * 			Null, if n is bigger than the queue's length.<br />
	 * 			Null, if the queue is empty.
	 */
	public JPOSToken getPreviousToken( int n )
	{
		if ( this.tokenList.size() == 0 )
			return null;
		
		if ( n >= this.tokenList.size() )
//			return this.tokenList.get(0);
			return null;

		return this.tokenList.get(this.tokenList.size() - 1 - n);
	}
}
