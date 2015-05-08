package cc.topicexplorer.actions.getterms;

import java.io.PrintWriter;
import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Create extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		PrintWriter pw = context.get("SERVLET_WRITER", PrintWriter.class);
		int topicId = context.getInteger("TOPIC_ID");
		int offset = context.getInteger("OFFSET");

		GetTerms getTermsAction = new GetTerms(database, pw, topicId, offset, Integer.parseInt((String) properties.get("TopicBestItemLimit")));
		
		context.bind("GET_TERMS_ACTION", getTermsAction);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("GetTermsCoreGenerateSQL");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet();
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
