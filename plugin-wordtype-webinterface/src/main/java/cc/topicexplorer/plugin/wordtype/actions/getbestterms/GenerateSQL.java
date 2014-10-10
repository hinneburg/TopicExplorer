package cc.topicexplorer.plugin.wordtype.actions.getbestterms;

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
			BestTerms bestTermsAction = context.get("BEST_TERMS_ACTION", BestTerms.class);
			bestTermsAction.getBestTerms(((String) properties.get("Wordtype_wordtypes")).split(","));
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
		return Sets.newHashSet("BestTermsCreate");
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
