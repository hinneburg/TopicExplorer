package cc.topicexplorer.plugin.wordtype.actions.getterms;

import java.sql.ResultSet;
import java.sql.SQLException;
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
			
			if(properties.getProperty("plugins").contains("mecab")) {
				try {
					ResultSet wordtypeChildrenRs = database.executeQuery("SELECT p1.POS FROM POS_TYPE p1, POS_TYPE p2 WHERE p2.POS=" + wordtype
							+ " AND p1.LOW>=p2.LOW AND p1.HIGH<=p2.HIGH");
					if(wordtypeChildrenRs.next()) {
						String wordtypeInString = "'" + wordtypeChildrenRs.getInt("POS") + "'";
						while(wordtypeChildrenRs.next()) {
							wordtypeInString += ",'" + wordtypeChildrenRs.getInt("POS") + "'";
						}
						getTermsAction.addWhereClause("TERM.WORDTYPE$WORDTYPE IN (" + wordtypeInString + ")");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else {
				getTermsAction.addWhereClause("TERM.WORDTYPE$WORDTYPE='" + wordtype + "'");
			}
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
