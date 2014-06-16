package cc.topicexplorer.commands;

import cc.commandmanager.core.Context;

public abstract class TableSelectCommand extends TableCommand {

	@Override
	public abstract void tableExecute(Context context);

	@Override
	public void setTableName() {
	}
}
