package cc.topicexplorer.database.tables.term;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.collect.Sets;

public class TermCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(TermCreate.class);

	@Override
	public void createTable() {

		try {
			database.executeUpdateQuery("CREATE TABLE `" + this.tableName + "` "
					+ "   ( TERM_ID INTEGER(11) NOT NULL KEY auto_increment,"
					+ "	TERM_NAME VARCHAR(100) NOT NULL unique , "
					+ " DOCUMENT_FREQUENCY bigint(21) NOT NULL DEFAULT '0', "
					+ " CORPUS_FREQUENCY bigint(21) NOT NULL DEFAULT '0', "
					+ " INVERSE_DOCUMENT_FREQUENCY double DEFAULT NULL, " + " CF_IDF double DEFAULT NULL "
					+ "	) ENGINE=InnoDB; ");
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be created.");
			throw new RuntimeException(e);
		}

	}

	@Override
	public void setTableName() {
		tableName = "TERM";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
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
