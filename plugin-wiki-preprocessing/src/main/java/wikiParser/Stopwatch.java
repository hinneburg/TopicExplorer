package wikiParser;
import java.util.HashMap;

public class Stopwatch
{

	private HashMap<String, Long> stopwatch = new HashMap<String, Long>();

	public void startStopping(String functionName)
	{

		stopwatch.put(functionName, System.currentTimeMillis());
	}

	public void stopStoppingAndDoOutputToConsole(String functionName)
	{

		if (!stopwatch.containsKey(functionName)){
			System.err.println(this.getClass() + ": functionName for stopping the time not found.");
			return;
		}
		
		long duration = System.currentTimeMillis() - stopwatch.get(functionName);
		System.out.println("the function " + functionName + " takes " + duration + "ms");
		stopwatch.remove(functionName);
	}

}
