package cc.topicexplorer.plugin.wiki.preprocessing;

import wikiParser.PreMalletAction_EntryPointForParallelisation;

import java.util.Properties;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.DependencyCommand;

public class PreMallet extends DependencyCommand {

	private Properties properties;
	
	@Override
	public void specialExecute(Context context) throws Exception {
		
		logger.info("[ " + getClass() + " ] - " + "preparing wiki-articles for mallet");
		
		CommunicationContext communicationContext = (CommunicationContext) context;
		properties = (Properties) communicationContext.get("properties");
//		database = (Database) communicationContext.get("database");
		
		PreMalletAction_EntryPointForParallelisation ph = new PreMalletAction_EntryPointForParallelisation(properties);
		ph.start();
		
	}

	
	@Override
	public void addDependencies() {
		afterDependencies.add("InFilePreparation");
		optionalAfterDependencies.add("Prune");
		
	}	


}
