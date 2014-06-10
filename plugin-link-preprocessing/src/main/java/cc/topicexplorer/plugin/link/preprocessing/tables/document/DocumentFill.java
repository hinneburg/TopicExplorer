package cc.topicexplorer.plugin.link.preprocessing.tables.document;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;

import com.google.common.collect.Sets;

/**
 * MIT-JOOQ-START import static jooq.generated.Tables.DOCUMENT; MIT-JOOQ-ENDE
 */
public class DocumentFill extends TableFillCommand {

	private static final Logger logger = Logger.getLogger(DocumentFill.class);

	@Override
	public void fillTable() {

		if (Boolean.parseBoolean(properties.getProperty("plugin_link"))) {
			/**
			 * MIT-JOOQ-START database.executeUpdateQuery("UPDATE " + this.tableName + " d, " + " (SELECT " +
			 * properties.getProperty("Link_OrgTableIndegree") + " AS indeg, " +
			 * properties.getProperty("Link_OrgTableLink") + " AS link, " + properties.getProperty("OrgTableId") +
			 * " AS id FROM " + properties.getProperty("OrgTableName") + " ) AS org SET " + DOCUMENT.LINK$URL.getName()
			 * + " = org.link, " + DOCUMENT.LINK$IN_DEGREE.getName() + " = org.indeg" + " WHERE org.id = d." +
			 * DOCUMENT.DOCUMENT_ID.getName()); MIT-JOOQ-ENDE
			 */
			/** OHNE_JOOQ-START */

			try {
				database.executeUpdateQuery("UPDATE " + this.tableName + " d, " + " (SELECT "
						+ properties.getProperty("Link_OrgTableIndegree") + " AS indeg, "
						+ properties.getProperty("Link_OrgTableLink") + " AS link, "
						+ properties.getProperty("OrgTableId") + " AS id FROM "
						+ properties.getProperty("OrgTableName") + " ) AS org SET " + "d.LINK$URL" + " = org.link, "
						+ "d.LINK$IN_DEGREE" + " = org.indeg" + " WHERE org.id = d." + "DOCUMENT_ID");
			} catch (SQLException e) {
				logger.error("Table " + this.tableName + " could not be filled properly.");
				throw new RuntimeException(e);
			}
			/** OHNE_JOOQ-ENDE */
		}

	}

	@Override
	public void setTableName() {
		/**
		 * MIT-JOOQ-START tableName = DOCUMENT.getName(); MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		this.tableName = "DOCUMENT";
		/** OHNE_JOOQ-ENDE */
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentFill", "Link_DocumentCreate");
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
