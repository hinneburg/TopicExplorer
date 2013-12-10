package cc.topicexplorer.plugin.duplicates.preprocessing.implementation;

public class TimeKeeper {

	private long startTime;
	
	
	public TimeKeeper()
	{
		this.startTimer();
	}
	
	public void startTimer()
	{
		this.startTime = System.nanoTime();
	}
	
	public void resetTimer()
	{
		this.startTimer();
	}

	
	public double getElapsedTimeInSeconds()
	{
		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		return (double)duration / 1000000000.0;
	}
	
	
}
