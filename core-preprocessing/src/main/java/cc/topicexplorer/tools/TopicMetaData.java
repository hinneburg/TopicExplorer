package cc.topicexplorer.tools;

/** MIT-JOOQ-START 
 import static jooq.generated.Tables.TOPIC;
 import static jooq.generated.Tables.TERM_TOPIC;
 MIT-JOOQ-ENDE */

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableCommand;

import com.google.common.collect.Sets;

public class TopicMetaData extends TableCommand {

	private final static Logger logger = Logger.getLogger(TopicMetaData.class);

	@Override
	public void tableExecute(Context context) {

		try {
			this.prepareRscriptInput();
		} catch (IOException e) {
			logger.error("A file system error occured while preparing the R script.");
			throw new RuntimeException(e);
		} catch (SQLException e) {
			logger.error("A SQL error occured while preparing the R script.");
			throw new RuntimeException(e);
		}

		this.processRscript();
	}

	private void prepareRscriptInput() throws IOException, SQLException {
		// @STARTONLYSECONDRUN@//

		FileWriter fos = new FileWriter("temp/similarities.out");
		ResultSet rsSim;

		fos.write("tid\ttid\tsim\n");

		/**
		 * MIT-JOOQ-START String sql = "SELECT kt1." + TERM_TOPIC.TOPIC_ID.getName() + " AS tid1, kt2." +
		 * TERM_TOPIC.TOPIC_ID.getName() + " AS tid2, sum(kt1." + TERM_TOPIC.PR_TERM_GIVEN_TOPIC.getName() + "*kt2." +
		 * TERM_TOPIC.PR_TERM_GIVEN_TOPIC.getName() + ")/sqrt(t1.n1* t2.n2) AS sim " + "FROM " + TERM_TOPIC.getName() +
		 * " kt1, " + TERM_TOPIC.getName() + " kt2, " + "  ( select " + TERM_TOPIC.TOPIC_ID.getName() + ", sum(" +
		 * TERM_TOPIC.PR_TERM_GIVEN_TOPIC.getName() + "*" + TERM_TOPIC.PR_TERM_GIVEN_TOPIC.getName() + ") as n1 from " +
		 * TERM_TOPIC.getName() + " group by " + TERM_TOPIC.TOPIC_ID.getName() + ") t1," + "  ( select " +
		 * TERM_TOPIC.TOPIC_ID.getName() + ", sum(" + TERM_TOPIC.PR_TERM_GIVEN_TOPIC.getName() + "*" +
		 * TERM_TOPIC.PR_TERM_GIVEN_TOPIC.getName() + ") as n2 from " + TERM_TOPIC.getName() + " group by " +
		 * TERM_TOPIC.TOPIC_ID.getName() + ") t2 WHERE kt1." + TERM_TOPIC.TERM_ID.getName() + "=kt2." +
		 * TERM_TOPIC.TERM_ID.getName() + " and t1." + TERM_TOPIC.TOPIC_ID.getName() + "=kt1." +
		 * TERM_TOPIC.TOPIC_ID.getName() + " and  t2." + TERM_TOPIC.TOPIC_ID.getName() + "=kt2." +
		 * TERM_TOPIC.TOPIC_ID.getName() + " " + "GROUP BY kt1." + TERM_TOPIC.TOPIC_ID.getName() + ", kt2." +
		 * TERM_TOPIC.TOPIC_ID.getName() + ", t1.n1, t2.n2 " + "ORDER BY kt1." + TERM_TOPIC.TOPIC_ID.getName() +
		 * ", kt2." + TERM_TOPIC.TOPIC_ID.getName() + " ;"; MIT-JOOQ-ENDE
		 */
		/** OHNE_JOOQ-START */
		// @formatter:off
		String sql = "SELECT kt1." + "TOPIC_ID" + " AS tid1, kt2." + "TOPIC_ID" + " AS tid2, sum(kt1."
				+ "PR_TERM_GIVEN_TOPIC" + "*kt2." + "PR_TERM_GIVEN_TOPIC" + ")/sqrt(t1.n1* t2.n2) AS sim " + "FROM "
				+ "TERM_TOPIC" + " kt1, " + "TERM_TOPIC" + " kt2, " + "  ( select " + "TERM_TOPIC.TOPIC_ID" + ", sum("
				+ "TERM_TOPIC.PR_TERM_GIVEN_TOPIC" + "*" + "TERM_TOPIC.PR_TERM_GIVEN_TOPIC" + ") as n1 from "
				+ "TERM_TOPIC" + " group by " + "TERM_TOPIC.TOPIC_ID" + ") t1," + "  ( select " + "TERM_TOPIC.TOPIC_ID"
				+ ", sum(" + "TERM_TOPIC.PR_TERM_GIVEN_TOPIC" + "*" + "TERM_TOPIC.PR_TERM_GIVEN_TOPIC"
				+ ") as n2 from " + "TERM_TOPIC" + " group by " + "TERM_TOPIC.TOPIC_ID" + ") t2 WHERE kt1." + "TERM_ID"
				+ "=kt2." + "TERM_ID" + " and t1." + "TOPIC_ID" + "=kt1." + "TOPIC_ID" + " and  t2." + "TOPIC_ID"
				+ "=kt2." + "TOPIC_ID" + " " + "GROUP BY kt1." + "TOPIC_ID" + ", kt2." + "TOPIC_ID" + ", t1.n1, t2.n2 "
				+ "ORDER BY kt1." + "TOPIC_ID" + ", kt2." + "TOPIC_ID" + " ;";
		// @formatter:on
		/** OHNE_JOOQ-ENDE */

		rsSim = this.database.executeQuery(sql);

		while (rsSim.next()) {
			fos.write(rsSim.getObject("tid1") + "\t" + rsSim.getObject("tid2") + "\t" + rsSim.getObject("sim") + "\n");
		}
		fos.close();
		logger.info("R-Script input successfully generated");
	}

	private void processRscript() {

		// @formatter:off
		ProcessBuilder p = new ProcessBuilder("bash", "-c", "Rscript " + "scripts/topic_order.R "
				+ "temp/similarities.out " + properties.getProperty("malletNumTopics") + " " + "temp/topic_order.csv");
		// @formatter:on

		Process process = null;

		try {
			process = p.start();
		} catch (IOException e1) {
			logger.warn("The shell process caused a file stream problem.", e1);
		}

		InputStream is = process.getErrorStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;

		try {
			while ((line = br.readLine()) != null) {
				logger.info("processRscipt " + line);
			}
			logger.info("R-Script successfully executed");
		} catch (IOException e2) {
			logger.error("R-Script execution failed");
			throw new RuntimeException(e2);
		}
	}

	@Override
	public void setTableName() {
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("TermTopicFill", "TopicFill");
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
