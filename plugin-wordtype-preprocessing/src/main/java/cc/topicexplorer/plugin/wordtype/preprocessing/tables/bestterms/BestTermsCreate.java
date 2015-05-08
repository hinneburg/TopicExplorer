package cc.topicexplorer.plugin.wordtype.preprocessing.tables.bestterms;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.collect.Sets;

public class BestTermsCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(BestTermsCreate.class);

	@Override
	public void createTable() {
		try {
			database.executeUpdateQuery("create table `" + this.tableName + "` ("
					+ "TERM_ID INTEGER(11) NOT NULL, TOPIC_ID INTEGER(11) NOT NULL, "
					+ "TERM_NAME VARCHAR(100) NOT NULL, "
					+ "NUMBER_OF_DOCUMENT_TOPIC int(11) NOT NULL, "
					+ "WORDTYPE VARCHAR(100) NOT NULL) "
					+ "ENGINE=InnoDB; ");
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setTableName() {
		tableName = "WORDTYPE$BEST_TERMS";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("WordType_TermFill");
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
