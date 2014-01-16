package cc.topicexplorer.plugin.wiki.preprocessing;

import java.util.Properties;

import org.apache.commons.chain.Context;

import wikiParser.MediawikiColorationAction_EntryPointForParallelisation;
import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.DependencyCommand;

public class MediawikiColoration extends DependencyCommand {

	private Properties properties;

	@Override
	public void specialExecute(Context context) {

		logger.info("[ " + getClass() + " ] - " + ": coloring the mediawiki");

		CommunicationContext communicationContext = (CommunicationContext) context;
		properties = (Properties) communicationContext.get("properties");

		try {

			MediawikiColorationAction_EntryPointForParallelisation pj = new MediawikiColorationAction_EntryPointForParallelisation(
					properties);
			pj.start();
		} catch (Exception e) {

			logger.warn("Coloration of mediawiki failed");
			e.printStackTrace();
		}

	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentTermTopicFill");
		beforeDependencies.add("ColorTopic_TopicFill");

	}

}
