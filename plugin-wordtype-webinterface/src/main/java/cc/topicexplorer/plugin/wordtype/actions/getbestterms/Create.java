package cc.topicexplorer.plugin.wordtype.actions.getbestterms;

import java.io.PrintWriter;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Create extends TableSelectCommand {

	private static final Logger logger = Logger.getLogger(Create.class);

	@Override
	public void tableExecute(Context context) {
		PrintWriter pw = context.get("SERVLET_WRITER", PrintWriter.class);
		context.bind("BEST_TERMS_ACTION", new BestTerms(this.database, pw, logger, (String) properties.get("Wordtype_wordtypes"), properties.getProperty("plugins").contains("mecab")));
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("BestTermsGenerateSQL");
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
