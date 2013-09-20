package cc.topicexplorer.database.tables.document;

import java.sql.SQLException;
import cc.topicexplorer.chain.commands.TableFillCommand;
/** MIT-JOOQ-START 
import chain.commands.TableFillCommand;
import static jooq.generated.Tables.DOCUMENT;
 MIT-JOOQ-ENDE */ 
/**
 * @author user
 * 
 */
public class DocumentFill extends TableFillCommand {

	/**
	 * TODO pageindeq noch auf 1 gesetzt fehlt auch in config.ini und hier als
	 * propertyübergabe
	 */
	@Override
	public void fillTable() throws SQLException {
/** MIT-JOOQ-START 
		database.executeUpdateQuery("insert into " + DOCUMENT.getName() 
				+ " (" + DOCUMENT.DOCUMENT_ID.getName() 
				+ ", " + DOCUMENT.NUMBER_OF_TOKENS.getName() 
				+ ") select " + properties.getProperty("OrgTableId") 
				+ ", CHAR_LENGTH(" + properties.getProperty("OrgTableTxt")
				+ ") from " + properties.getProperty("OrgTableName"));
 MIT-JOOQ-ENDE */
/** OHNE_JOOQ-START */ 
		database.executeUpdateQuery("insert into " + "DOCUMENT" 
				+ " (" + "DOCUMENT.DOCUMENT_ID" 
				+ ", " + "DOCUMENT.NUMBER_OF_TOKENS" 
				+ ") select " + properties.getProperty("OrgTableId") 
				+ ", CHAR_LENGTH(" + properties.getProperty("OrgTableTxt")
				+ ") from " + properties.getProperty("OrgTableName"));
/** OHNE_JOOQ-ENDE */ 
	}

	@Override
	public void setTableName() {
/** MIT-JOOQ-START 
		tableName = DOCUMENT.getName();
 MIT-JOOQ-ENDE */
/** OHNE_JOOQ-START */
		tableName = "DOCUMENT";
/** OHNE_JOOQ-ENDE */ 
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentCreate");
	}	
	
}
