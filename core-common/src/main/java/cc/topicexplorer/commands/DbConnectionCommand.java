package cc.topicexplorer.commands;

import java.util.Properties;
import java.util.Set;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.topicexplorer.database.Database;

import com.google.common.collect.Sets;

public class DbConnectionCommand implements Command {

	@Override
	public void execute(Context context) {
		Database database = new Database(context.get("properties", Properties.class), false);
		context.bind("database", database);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("PropertiesCommand");
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
