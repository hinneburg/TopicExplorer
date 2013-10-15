package cc.topicexplorer.web;

import cc.topicexplorer.chain.ChainManagement;
import cc.topicexplorer.chain.CommunicationContext;

public class Context {

	private static ChainManagement chainManagement = null;
	private static CommunicationContext context = null;
	public static long time;
	private static int counter = 0;
	
	
	private Context() {
		
	}
	
	public static CommunicationContext getInstance() {
		init();
		counter++;
		System.out.println("Shared instance: time " + time + ", counter " + counter);
		
		return context;
	}
	
	public static void init() {
		try {
			if (context == null) {
				time = System.currentTimeMillis();
				chainManagement = new ChainManagement();
				chainManagement.init();
				chainManagement.setCatalog("/catalog.xml");
				context = chainManagement.getInitialCommunicationContext();
				context.put("dependencies", chainManagement.getDependencies());
				System.out.println("Init context.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
