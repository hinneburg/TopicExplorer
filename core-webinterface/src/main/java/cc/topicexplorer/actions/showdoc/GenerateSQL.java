package cc.topicexplorer.actions.showdoc;


import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Context;

import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class GenerateSQL extends TableSelectCommand {

	private static final Logger logger = Logger.getLogger(GenerateSQL.class);

	@Override
	public void tableExecute(Context context) {
		try {
			ShowDoc showDocAction = context.get("SHOW_DOC_ACTION", ShowDoc.class);
			showDocAction.executeQuery();
		} catch (SQLException sqlEx) {
			logger.error("A problem occured while executing the query.");
			throw new RuntimeException(sqlEx);
		}
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("ShowDocCoreCreate");
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
