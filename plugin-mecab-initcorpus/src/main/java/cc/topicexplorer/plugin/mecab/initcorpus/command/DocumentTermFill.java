package cc.topicexplorer.plugin.mecab.initcorpus.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableFillCommand;
import cc.topicexplorer.plugin.mecab.initcorpus.implementation.postagger.JPOSMeCab;
import cc.topicexplorer.plugin.mecab.initcorpus.implementation.treetagger.PreparationWithTreeTagger;

import com.google.common.collect.Sets;

public class DocumentTermFill extends TableFillCommand {
	private static final Logger logger = Logger
			.getLogger(DocumentTermFill.class);

	@Override
	public void fillTable() {
		String textAnalyzer = properties.getProperty("Mecab_text-analyzer").trim();

		String fileName = "temp/docTerm.sql.csv";
		File fileTemp = new File(fileName);
		if (fileTemp.exists()) {
			fileTemp.delete();
		}
		try {
			JPOSMeCab jpos = null;
			PreparationWithTreeTagger treeTaggerAnalyzer = null;
			if ("mecab".equals(textAnalyzer)) {
				if (!properties.containsKey("Mecab_LibraryPath")) {
					logger.error("Mecab library path not set. Did you enable mecab plugin in config.properties?");
					throw new RuntimeException("Mecab library path not set.");
				}
				jpos = new JPOSMeCab(properties
						.getProperty("Mecab_LibraryPath").trim(), logger);
			} else if ("treetagger".equals(textAnalyzer)) {
				if (!properties.containsKey("Mecab_treetagger-path")
						|| !properties.containsKey("Mecab_treetagger-model")) {
					logger.error("TreeTagger path or model not set.");
					throw new RuntimeException(
							"TreeTagger path or model not set.");
				}

				HashMap<String,Integer> tag2id = new HashMap<String,Integer>();
				Statement posType_stmt = database.getConnection().createStatement(
						java.sql.ResultSet.TYPE_FORWARD_ONLY,
						java.sql.ResultSet.CONCUR_READ_ONLY);
				posType_stmt.setFetchSize(Integer.MIN_VALUE);

				ResultSet posRs = posType_stmt
						.executeQuery("SELECT POS,DESCRIPTION FROM POS_TYPE;");
				while (posRs.next()) {
					tag2id.put(posRs.getString("DESCRIPTION"), posRs.getInt("POS"));
				}
				posRs.close();
				posType_stmt.close();

				treeTaggerAnalyzer = new PreparationWithTreeTagger(',',
						properties.getProperty("Mecab_treetagger-path").trim(), properties.getProperty("Mecab_treetagger-model").trim(), tag2id);

				treeTaggerAnalyzer.setLogger(org.apache.log4j.Logger.getRootLogger());
				
			}
			BufferedWriter docTermCSVWriter = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(fileName, true), "UTF-8"));

			Statement stmt = database.getConnection().createStatement(
					java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(Integer.MIN_VALUE);

			ResultSet textRs = stmt
					.executeQuery("SELECT DOCUMENT_ID, DOCUMENT_TEXT FROM orgTable_text");
			while (textRs.next()) {
				List<String> csvList = null;
				if ("mecab".equals(textAnalyzer)) {
					csvList = jpos.parseString(textRs.getInt("DOCUMENT_ID"),
							textRs.getString("DOCUMENT_TEXT"), logger);
				} else if ("treetagger".equals(textAnalyzer)) {
					csvList = treeTaggerAnalyzer.parse(textRs.getInt("DOCUMENT_ID"),
							textRs.getString("DOCUMENT_TEXT"));
				}
				for (String csvEntry : csvList) {
					docTermCSVWriter.write(csvEntry + "\n");
				}
			}
			docTermCSVWriter.flush();
			docTermCSVWriter.close();

			database.executeUpdateQuery("LOAD DATA LOCAL INFILE '"
					+ fileName
					+ "' IGNORE INTO TABLE "
					+ tableName
					+ " CHARACTER SET utf8 FIELDS TERMINATED BY ',' ENCLOSED BY '\"' (`DOCUMENT_ID`, "
					+ "`POSITION_OF_TOKEN_IN_DOCUMENT`, `TERM`, `TOKEN`, `WORDTYPE_CLASS`, `CONTINUATION`);");
			stmt.close();

			database.executeUpdateQuery("CREATE INDEX TERM_WORDCLASS ON DOCUMENT_TERM(TERM,WORDTYPE_CLASS,DOCUMENT_ID)");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setTableName() {
		this.tableName = "DOCUMENT_TERM";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentTermCreate","PosTypeFill");
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
