package cc.topicexplorer.plugin.duplicates.preprocessing.command;

import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.commandmanager.core.ResultState;
import cc.topicexplorer.database.Database;
import cc.topicexplorer.plugin.duplicates.preprocessing.implementation.Duplicates;

import com.google.common.collect.Sets;

public class Duplicates_Command implements Command {

	private static final Logger logger = Logger.getLogger(Duplicates_Command.class);
	private Properties properties;
	protected cc.topicexplorer.database.Database database;

	private final Duplicates duplicates = new Duplicates();

	@Override
	public ResultState execute(Context context) {
		logger.info("[ " + getClass() + " ] - " + "detecting duplicates");

		properties = context.get("properties", Properties.class);
		database = context.get("database", Database.class);

		duplicates.setLogger(logger);
		duplicates.setDB(database);

		String inFilePath = properties.getProperty("InCSVFile");
		duplicates.setCsvFilePath(inFilePath);

		int frameSize = Integer.parseInt(properties.getProperty("Duplicates_frameSize"));
		duplicates.setFrameSize(frameSize);

		duplicates.findDuplicates();
		try {
			duplicates.writeDuplicatesToDB();
		} catch (SQLException sqlEx) {
			logger.error("Could not write duplicates to db.");
			return ResultState.failure("Could not write duplicates to db.", sqlEx);
		}
		return ResultState.success();
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
