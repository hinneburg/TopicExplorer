package cc.topicexplorer.chain.commands;

import org.apache.commons.chain.Context;

public final class DummyCommand5 extends DependencyCommand {

	@Override
	public void specialExecute(Context context) {
		System.err.println("DummyCommad5 was called.");
	}
}
