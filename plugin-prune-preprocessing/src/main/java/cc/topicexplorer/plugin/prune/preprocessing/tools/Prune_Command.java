package cc.topicexplorer.plugin.prune.preprocessing.tools;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.topicexplorer.database.tables.document.DocumentFill;

import com.google.common.collect.Sets;

public class Prune_Command implements Command {

	private static final Logger logger = Logger.getLogger(DocumentFill.class);

	private Properties properties;
	private final Prune_Ram_SortedCsv prune = new Prune_Ram_SortedCsv();

	@Override
	public void execute(Context context) {
		properties = context.get("properties", Properties.class);
		
		if(!properties.get("newTopics").toString().equalsIgnoreCase("true")) {
			logger.info("Skip: Take the previous DOCUMENT_TERM_TOPIC table");
		} else {
			logger.info("pruning vocabular");
	
			
	
			float upperBoundPercent = Float.parseFloat(properties.getProperty("Prune_upperBound"));
			float lowerBoundPercent = Float.parseFloat(properties.getProperty("Prune_lowerBound"));
			prune.setLowerAndUpperBoundPercent(lowerBoundPercent, upperBoundPercent);
	
			prune.setLogger(logger);
			String inFilePath = properties.getProperty("InCSVFile");
			prune.setInFilePath(inFilePath);
	
			try {
				prune.prune();
			} catch (IOException e) {
				logger.error("During prune a file stream problem occured. Input file path: " + inFilePath);
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("InFilePreparation");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("DocumentTermTopicCreate");
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
