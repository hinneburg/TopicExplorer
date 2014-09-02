package cc.topicexplorer.plugin.wordtype.actions.getterms;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.actions.getterms.GetTerms;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		GetTerms getTermsAction = context.get("GET_TERMS_ACTION", GetTerms.class);
		getTermsAction.addTableColumn("TERM.WORDTYPE$WORDTYPE", "WORDTYPE$WORDTYPE");
		if(context.containsKey("wordtype")) {
			String wordtype = context.getString("wordtype");
			
			getTermsAction.addWhereClause("TERM.WORDTYPE$WORDTYPE='" + wordtype + "'");
		}
		context.rebind("GET_TERMS_ACTION", getTermsAction);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("GetTermsCoreGenerateSQL");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("GetTermsCoreCreate");
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
