package cc.topicexplorer.plugin.japanesepos.preprocessing.command;


import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.topicexplorer.database.Database;
import cc.topicexplorer.plugin.japanesepos.preprocessing.implementation.postagger.JPOSMeCab;


public class JPOS_Command implements Command {

	private static final Logger logger = Logger.getLogger(JPOS_Command.class);
	private Properties properties;
	protected cc.topicexplorer.database.Database database;
	
	private JPOSMeCab jpos = new JPOSMeCab();
	

	@Override
	public void execute(Context context) {
		logger.info("[ " + getClass() + " ] - " + "detecting duplicates");

		properties = context.get("properties", Properties.class);
		database = context.get("database", Database.class);
		
		// TODO
//		jpos.parseString("");
		
		
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("InFilePreparation");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentTermTopicCreate");
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
