package cc.topicexplorer.database.tables.document;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;

import com.google.common.collect.Sets;

/**
 * MIT-JOOQ-START import chain.commands.TableFillCommand; import static jooq.generated.Tables.DOCUMENT; MIT-JOOQ-ENDE
 */
public class DocumentFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(DocumentFill.class);

	/*
	 * TODO pageindeq noch auf 1 gesetzt fehlt auch in config.ini und hier als propertyübergabe
	 */
	@Override
	public void fillTable() {
		/**
		 * MIT-JOOQ-START database.executeUpdateQuery("insert into " + DOCUMENT.getName() + " (" +
		 * DOCUMENT.DOCUMENT_ID.getName() + ", " + DOCUMENT.NUMBER_OF_TOKENS.getName() + ") select " +
		 * properties.getProperty("OrgTableId") + ", CHAR_LENGTH(" + properties.getProperty("OrgTableTxt") + ") from " +
		 * properties.getProperty("OrgTableName")); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		try {
			database.executeUpdateQuery("insert into " + "DOCUMENT" + " (" + "DOCUMENT.DOCUMENT_ID" + ", "
					+ "DOCUMENT.NUMBER_OF_TOKENS" + ") select " + properties.getProperty("OrgTableId")
					+ ", CHAR_LENGTH(" + properties.getProperty("OrgTableTxt") + ") from "
					+ properties.getProperty("OrgTableName"));
		} catch (SQLException e) {
			logger.error("Table " + this.tableName + " could not be filled properly.");
			throw new RuntimeException(e);
		}
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public void setTableName() {
		/**
		 * MIT-JOOQ-START tableName = DOCUMENT.getName(); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		tableName = "DOCUMENT";
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentCreate");
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
