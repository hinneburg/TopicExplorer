package cc.topicexplorer.actions.getterms;

import java.sql.SQLException;

import org.apache.commons.chain.Context;

import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.TableSelectCommand;

public class GenerateSQL extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		CommunicationContext communicationContext = (CommunicationContext) context;

		AllTerms allTermsAction = (AllTerms) communicationContext.get("ALL_TERMS_ACTION");

		try {
			allTermsAction.executeQueriesAndWriteAllTerms();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("AllTermsCoreCreate");
	}

}