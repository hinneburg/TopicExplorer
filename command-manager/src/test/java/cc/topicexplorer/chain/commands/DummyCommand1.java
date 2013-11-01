package cc.topicexplorer.chain.commands;

import org.apache.commons.chain.Context;

public final class DummyCommand1 extends DependencyCommand {

	@Override
	public void specialExecute(Context context) throws Exception {
		System.err.println("DummyCommad1 was called.");
	}
}
