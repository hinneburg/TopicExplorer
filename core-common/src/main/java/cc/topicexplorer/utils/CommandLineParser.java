package cc.topicexplorer.utils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

public class CommandLineParser {
	private final Options options;

	private final org.apache.commons.cli.CommandLineParser commandLineParser;
	private CommandLine commandLine;
	private final HelpFormatter helpFormatter;

	private boolean onlyDrawGraph = false;
	private String catalogLocation;
	private final Set<String> startCommands = new HashSet<String>();
	private final Set<String> endCommands = new HashSet<String>();

	private final String[] args;

	private static final Logger logger = Logger.getLogger(CommandLineParser.class);

	/**
	 * Adds the possible arguments. Sets global args and executes the parsing of
	 * the given arguments.
	 * 
	 * @param args
	 * @throws ParseException
	 *             if there are any problems encountered while parsing the
	 *             command line tokens.
	 */
	public CommandLineParser(String[] args) {
		options = new Options();
		options.addOption("h", "help", false, "prints information about passing arguments.");
		options.addOption("c", "catalog", true, "determines location of catalog file");
		options.getOption("c").setArgName("string");
		options.addOption("g", "graph", false, "only the graph is drawed");
		options.addOption("s", "start", true, "set commands to start with, separated by only comma");
		options.getOption("s").setArgName("string");
		options.addOption("e", "end", true, "set commands to end with, separated only by comma");
		options.getOption("e").setArgName("string");

		commandLineParser = new BasicParser();
		commandLine = null;
		helpFormatter = new HelpFormatter();

		this.args = args;

		parseArguments();
	}

	/**
	 * Checks if any of the mentioned options is contained in the arguments and
	 * then sets it in the class. If the usage of arguments is wrong help is
	 * printed.
	 * 
	 * @throws ParseException
	 *             if there are any problems encountered while parsing the
	 *             command line tokens.
	 */
	private void parseArguments() {
		// if there is something wrong with the input, print help
		try {
			commandLine = commandLineParser.parse(options, args);
		} catch (org.apache.commons.cli.ParseException parseEx) {
			this.logger.error("Wrong usage of arguments.");
			printHelp();
			throw new RuntimeException(parseEx);
		}

		if (commandLine.hasOption("h")) {
			printHelp();
		}

		if (commandLine.hasOption("g")) {
			onlyDrawGraph = true;
		}

		if (commandLine.hasOption("c")) {
			catalogLocation = commandLine.getOptionValue("c");
		} else {
			this.logger.info("No catalog location given, taking standard value.");
		}

		if (commandLine.hasOption("s")) {
			startCommands.addAll(Arrays.asList((commandLine.getOptionValue("s").split(","))));
		}

		if (commandLine.hasOption("e")) {
			endCommands.addAll(Arrays.asList((commandLine.getOptionValue("s").split(","))));
		}
	}

	public String getCatalogLocation() {
		return catalogLocation;
	}

	public boolean getOnlyDrawGraph() {
		return onlyDrawGraph;
	}

	public Set<String> getStartCommands() {
		return startCommands;
	}

	public Set<String> getEndCommands() {
		return endCommands;
	}

	public void printHelp() {
		helpFormatter.printHelp("TopicExplorer <command> [<arg>]", options);
	}
}
