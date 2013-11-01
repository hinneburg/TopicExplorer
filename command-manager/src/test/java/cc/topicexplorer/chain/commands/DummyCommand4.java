package cc.topicexplorer.chain.commands;

import org.apache.commons.chain.Context;

public final class DummyCommand4 extends DependencyCommand {

	@Override
	public void specialExecute(Context context) throws Exception {
		System.err.println("DummyCommand4 was called.");
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("DummyCommand1");
		afterDependencies.add("DummyCommand2");
		optionalAfterDependencies.add("DummyCommand3");
	}
}
