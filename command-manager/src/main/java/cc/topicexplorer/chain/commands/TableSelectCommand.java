package cc.topicexplorer.chain.commands;

import org.apache.commons.chain.Context;

public abstract class TableSelectCommand extends TableCommand {

	@Override
	public abstract void tableExecute(Context context);

	@Override
	public void setTableName() {
	}
}
