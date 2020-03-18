package cc.topicexplorer.TopicModelling.mallet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.commandmanager.core.ResultState;
import cc.mallet.classify.tui.Csv2Vectors;
import cc.mallet.topics.tui.Vectors2Topics;

import com.google.common.collect.Sets;

public class Mallet implements Command {

	private final static Logger logger = Logger.getLogger(Mallet.class);

	private Properties properties;

	@Override
	public ResultState execute(Context context) {
		properties = context.get("properties", Properties.class);

		if (!properties.get("newTopics").toString().equalsIgnoreCase("true")) {
			logger.info("Skip: Take the previous DOCUMENT_TERM_TOPIC table");
		} else {
			logger.info("run mallet");

			try {
				importFile();
			} catch (FileNotFoundException e1) {
				logger.error("Import file could not be found.");
				return ResultState.failure("Import file could not be found.", e1);
			} catch (IOException e2) {
				logger.error("During file import a file stream problem occured.");
				return ResultState.failure("During file import a file stream problem occured.", e2);
			}

			try {
				trainTopics(properties.getProperty("malletNumTopics"), properties.getProperty("malletNumThreads"));
			} catch (IOException e3) {
				logger.error("During topic training a file stream problem occured.");
				return ResultState.failure("During topic training a file stream problem occured.", e3);
			}
		}
		return ResultState.success();
	}

	private static void importFile() throws FileNotFoundException, IOException {
		String[] malletArgs = { "--keep-sequence", "TRUE", "--input", "temp/malletinput.txt", "--output",
				"temp/out.sequence.input", "--print-output", "FALSE", "--token-regex", "[^\\s]+" }; // [\\p{L}\\p{N}_]+|[\\p{P}]+
		Csv2Vectors.main(malletArgs);
		logger.info("import done");
	}

	private static void trainTopics(String malletNumTopics, String malletNumThreads) throws IOException {
		String[] malletArgs = { "--input", "temp/out.sequence.input", "--num-topics", malletNumTopics,
				"--num-iterations", "500", "--optimize-interval", "10", "--optimize-burn-in", "1000", "--output-state",
				"temp/out.topic-state.gz", "--output-doc-topics", "temp/out.doc-topics", "--inferencer-filename",
				"temp/out.inferencer", "--num-threads", malletNumThreads, "--num-top-words", "20", "--output-topic-keys",
		"temp/out.topic-keys" };
		Vectors2Topics.main(malletArgs);
		logger.info("train topics done");
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("InFilePreparation");
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
