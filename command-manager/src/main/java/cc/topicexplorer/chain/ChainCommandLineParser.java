package cc.topicexplorer.chain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 * Retrieves arguments from the commandline and makes them accessable via a
 * getter method
 * 
 * @author Sebastian Baer
 * 
 */
public class ChainCommandLineParser {
	private Options options;

	private CommandLineParser commandLineParser;
	private CommandLine commandLine;
	private HelpFormatter helpFormatter;

	private boolean onlyDrawGraph = false;
	private String catalogLocation;
	private Set<String> startCommands = new HashSet<String>();
	private Set<String> endCommands = new HashSet<String>();

	private String[] args;

	/**
	 * Adds the possible arguments. Sets global args and executes the
	 * parsing of the given arguments.
	 * 
	 * @param args
	 */
	public ChainCommandLineParser(String[] args) {
		options = new Options();
		options.addOption("h", "help", false,
				"prints information about passing arguments.");
		options.addOption("c", "catalog", true,
				"determines location of catalog file");
		options.getOption("c").setArgName("string");
		options.addOption("g", "graph", false, "only the graph is drawed");
		options.addOption("s", "start", true,
				"set commands to start with, separated by only comma");
		options.getOption("s").setArgName("string");
		options.addOption("e", "end", true,
				"set commands to end with, separated only by comma");
		options.getOption("e").setArgName("string");

		commandLineParser = new BasicParser();
		commandLine = null;
		helpFormatter = new HelpFormatter();

		this.args = args;

		parseArguments();
	}

	/**
	 * Checks if any of the mentioned options is contained in the arguments
	 * and then sets it in the class. If the usage of arguments is wrong
	 * help is printed.
	 */
	private void parseArguments() {
		// if there is something wrong with the input, print help
		try {
			commandLine = commandLineParser.parse(options, args);
		} catch (Exception e) {
			printHelp();
			System.out.println("Usage of arguments wrong.");
			System.exit(1);
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
			System.out.println("No catalog location given, taking standard value.");
		}

		if (commandLine.hasOption("s")) {
			startCommands.addAll(Arrays.asList((commandLine.getOptionValue("s")
					.split(","))));
		}

		if (commandLine.hasOption("e")) {
			endCommands.addAll(Arrays.asList((commandLine.getOptionValue("s")
					.split(","))));
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