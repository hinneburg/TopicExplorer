package cc.topicexplorer.plugin.wiki.preprocessing;

import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import wikiParser.MediawikiColorationAction_EntryPointForParallelisation;
import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;

import com.google.common.collect.Sets;

public class MediawikiColoration implements Command {

	private static final Logger logger = Logger.getLogger(MediawikiColoration.class);
	private Properties properties;

	@Override
	public void execute(Context context) {
		logger.info("[ " + getClass() + " ] - " + ": coloring the mediawiki");

		properties = context.get("properties", Properties.class);
		try {
			MediawikiColorationAction_EntryPointForParallelisation pj = new MediawikiColorationAction_EntryPointForParallelisation(
					properties);
			pj.start();
		} catch (InterruptedException e) {
			logger.warn("Coloration of mediawiki failed", e);
		}
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentTermTopicFill", "ColorTopic_TopicFill");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}

}
