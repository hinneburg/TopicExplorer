package cc.topicexplorer.plugin.wiki.preprocessing;

import java.util.Properties;

import org.apache.commons.chain.Context;

import wikiParser.PreMalletAction_EntryPointForParallelisation;
import cc.commandmanager.core.CommunicationContext;
import cc.commandmanager.core.DependencyCommand;

public class PreMallet extends DependencyCommand {

	private Properties properties;

	@Override
	public void specialExecute(Context context) {

		logger.info("[ " + getClass() + " ] - " + "preparing wiki-articles for mallet");

		CommunicationContext communicationContext = (CommunicationContext) context;
		properties = (Properties) communicationContext.get("properties");
		// database = (Database) communicationContext.get("database");

		PreMalletAction_EntryPointForParallelisation ph = new PreMalletAction_EntryPointForParallelisation(properties);
		ph.start();

	}

	@Override
	public void addDependencies() {
		afterDependencies.add("InFilePreparation");
		optionalAfterDependencies.add("Prune");

	}

}
