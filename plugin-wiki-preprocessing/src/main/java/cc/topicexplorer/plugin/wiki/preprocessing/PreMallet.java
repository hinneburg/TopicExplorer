package cc.topicexplorer.plugin.wiki.preprocessing;

import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import wikiParser.PreMalletAction_EntryPointForParallelisation;
import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.commandmanager.core.ResultState;

import com.google.common.collect.Sets;

public class PreMallet implements Command {

	private static final Logger logger = Logger.getLogger(PreMallet.class);
	private Properties properties;

	@Override
	public ResultState execute(Context context) {
		logger.info("[ " + getClass() + " ] - " + "preparing wiki-articles for mallet");

		properties = context.get("properties", Properties.class);
		PreMalletAction_EntryPointForParallelisation ph = new PreMalletAction_EntryPointForParallelisation(properties);
		ph.start();
		return ResultState.success();
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("InFilePreparation");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet("Prune");
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}

}
