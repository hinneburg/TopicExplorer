package cc.topicexplorer.TopicModelling.mallet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import cc.mallet.classify.tui.Csv2Vectors;
import cc.mallet.topics.tui.Vectors2Topics;
import cc.topicexplorer.chain.CommunicationContext;
import cc.topicexplorer.chain.commands.DependencyCommand;

public class Mallet extends DependencyCommand {
	private Properties properties;
	private Logger logger = Logger.getRootLogger();
	
	public void importFile() throws FileNotFoundException, IOException {
		String[] malletArgs = { 
				"--keep-sequence", "TRUE", 
				"--input", "temp/malletinput.txt", 
				"--output", "temp/out.sequence.input",
				"--print-output", "FALSE",		
				"--token-regex", "[\\p{L}\\p{N}_]+|[\\p{P}]+" };
		Csv2Vectors.main(malletArgs);
		logger.info("import done");
	}

	public void trainTopics() throws IOException {
		String[] malletArgs = { 
				"--input", "temp/out.sequence.input",
				"--num-topics", this.properties.getProperty("malletNumTopics"), 
				"--num-iterations", "500",
				"--optimize-interval", "10", 
				"--optimize-burn-in", "1000",
				"--output-state", "temp/out.topic-state.gz",
				"--output-doc-topics", "temp/out.doc-topics",
				"--inferencer-filename", "temp/out.inferencer",
				"--num-threads", "4", 
				"--num-top-words", "20", 
				"--output-topic-keys", "temp/out.topic-keys" };
		Vectors2Topics.main(malletArgs);
		logger.info("train topics done");
	}

	@Override
	public void specialExecute(Context context) throws Exception {

		logger.info("[ " + getClass() + " ] - "
					+ "run mallet");
			
		CommunicationContext communicationContext = (CommunicationContext) context;
		properties = (Properties) communicationContext.get("properties");
	
		this.importFile();
		this.trainTopics();
	}
	
	@Override
	public void addDependencies() {
		beforeDependencies.add("InFilePreparation");
	}	
}
