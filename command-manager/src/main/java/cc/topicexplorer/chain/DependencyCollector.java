package cc.topicexplorer.chain;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.Command;
import org.apache.log4j.*;


/**
 * Collects dependencies of commands mentioned in the catalog and gets them
 * ordered.
 * <p>
 * Each map follows the semantic that a key of a map is dependent of the
 * correspondent value (respectively dependent of each element of the values
 * ArrayList).
 */
public class DependencyCollector {

	private Catalog catalog;
	private List<String> orderedCommands = null;
	private Map<String, List<String>> composedDependencies = null;
	private Map<String, List<String>> dependencies = null;
	private Map<String, List<String>> optionalDependencies = null;
	private Map<String, List<String>> newDependencies = new HashMap<String, List<String>>();
	private Logger logger = Logger.getRootLogger();

	/**
	 * Class constructor taking the catalog argument and sets it in this class.
	 * 
	 * @param catalog
	 */
	public DependencyCollector(Catalog catalog) {
		this.catalog = catalog;
	}

	/**
	 * Creates a file in dot format. A -> B means that A depends of B. A dashed
	 * line represents an optional dependency. It accesses the global dependency
	 * maps, so it must be executed before the maps are changed, e.g. before
	 * executing the orderCommands method because it changes the maps.
	 */
	private void makeDotFile() {
		String dotContent = "digraph G { \n";
		dotContent += "rankdir = BT; \n";
		dotContent += "node [shape=record]; \n";
		dotContent += "edge [arrowhead=vee]; \n";

		for (String key : composedDependencies.keySet()) {
			if (composedDependencies.get(key).isEmpty()) {
				dotContent += key + "; \n";
			} else {
				for (String value : composedDependencies.get(key)) {
					dotContent += (key + " -> " + value + "; \n");
				}
			}
		}

		for (String key : optionalDependencies.keySet()) {
			if (!optionalDependencies.get(key).isEmpty()) {
				for (String value : optionalDependencies.get(key)) {
					dotContent += (key + " -> " + value + " [style = dotted] " + "; \n");
				}
			}
		}

		dotContent += "}";

		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
					"etc/graph.dot"));
			bufferedWriter.write(dotContent);
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the name is contained as key in the dependecies map. If it is,
	 * it takes the value of the key (an arrayList) and merges it with the given
	 * beforeDependencies. After merging, the compoundBeforeList is set as new
	 * value of the key. Otherwise the name and the beforeDependencies are added
	 * as new key-value-pair to the dependencies.
	 * <p>
	 * If there is an element in the afterDependencies, each element of the list
	 * must be added to the dependencies as key with the name as correspondent
	 * value. If that pair is contained the old and the new list have to be
	 * merged, otherwise only the new key-value-pair is added.
	 * 
	 * @param name
	 * @param dependencies
	 * @param afterDependencies
	 * @param beforeDependencies
	 */
	private void updateDependencies(String name,
			Map<String, List<String>> dependencies,
			List<String> afterDependencies, List<String> beforeDependencies) {
		List<String> compoundBeforeList = new ArrayList<String>();

		if (dependencies.containsKey(name)) {
			compoundBeforeList.addAll(dependencies.get(name));
		}
		compoundBeforeList.addAll(beforeDependencies);
		dependencies.put(name, compoundBeforeList);

		if (!afterDependencies.isEmpty()) {
			ArrayList<String> compoundAfterList = new ArrayList<String>();
			for (String key : afterDependencies) {
				compoundAfterList.add(name);
				if (dependencies.containsKey(key)) {
					compoundAfterList.addAll(dependencies.get(key));
				}
				dependencies.put(key, compoundAfterList);
			}
		}
	}

	/**
	 * Every command of the catalog will be executed with the dependencyContext.
	 * Then a command should set its dependencies in the dependencyContext.
	 * <p>
	 * Then those per command set dependencies and optional dependencies will be
	 * read out and processed by the updateDependencies method.
	 */
	private void collectDependencies() {
		dependencies = new HashMap<String, List<String>>();
		optionalDependencies = new HashMap<String, List<String>>();

		try {
			DependencyContext dependencyContext = new DependencyContext();
			String name;

			for (Iterator<?> it = catalog.getNames(); it.hasNext();) {
				name = (String) it.next();
				Command command = catalog.getCommand(name);
				command.execute(dependencyContext);

				updateDependencies(name, dependencies, dependencyContext
						.getAfterDependencies(), dependencyContext
						.getBeforeDependencies());
				updateDependencies(name, optionalDependencies,
						dependencyContext.getOptionalAfterDependencies(),
						dependencyContext.getOptionalBeforeDependencies());
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Composes dependencies and optionalDependencies.
	 */
	private void composeDependencies() {
		composedDependencies = new HashMap<String, List<String>>(dependencies);

		for (String key : optionalDependencies.keySet()) {
			for (String value : optionalDependencies.get(key)) {
				if (composedDependencies.containsKey(value)) {
					composedDependencies.get(key).add(value);
				}
			}
		}
	}

	/**
	 * Topologically sorts the composedDependencies and sets the orderedCommands
	 * variable.
	 */
	private void orderCommands() {
		dependencies = new ConcurrentHashMap<String, List<String>>(
				composedDependencies);
		orderedCommands = new LinkedList<String>();
		LinkedList<String> helpList = new LinkedList<String>();
		String node = "";

		// find all nodes with no dependencies, put into helpList, remove from
		// HashMap
		for (String key : dependencies.keySet()) {
			List<String> list = dependencies.get(key);

			if (list.isEmpty()) {
				helpList.add(key);
				dependencies.remove(key);
			}
		}

		// as long as helpList contains a node without dependencies, take one,
		// remove it from helpList, put into commandList
		while (!helpList.isEmpty()) {
			node = helpList.getFirst();
			helpList.remove(node);
			orderedCommands.add(node);

			// check if there is any edge between the node and another one
			for (String key : dependencies.keySet()) {
				List<String> list = dependencies.get(key);

				// if the node is in a value list, remove it
				if (list.contains(node)) {
					list.remove(node);
					dependencies.put(key, list);
				}

				// if the node has no other incoming edges, put it into
				// commandList
				if (dependencies.get(key).isEmpty()) {
					helpList.add(key);
					dependencies.remove(key);
				}
			}
		}

		// only if the dependencyMap is empty the graph was correct, otherwise
		// there was something wrong with it
		if (!dependencies.isEmpty()) {
			logger.fatal("The dependencyMap wasn't empty yet but it should have been: "
					+ dependencies);
			System.exit(1);
		}
	}

	/**
	 * Returns a list of topologically sorted commands, containing the commands
	 * given in the catalog. Executes the makeDotFile method.
	 * 
	 * @return A list of topologically sorted commands.
	 */
	public List<String> getOrderedCommands() {
		if (dependencies == null) {
			collectDependencies();
		}

		if (composedDependencies == null) {
			composeDependencies();
		}

		makeDotFile();

		if (orderedCommands == null) {
			orderCommands();
		}

		return orderedCommands;

	}
	
	public void getStrongComponents() {
		
		List<String> startCommands = new ArrayList<String>();
		startCommands.add("TopicFill");
		startCommands.add("TermFill");
		
		List<String> endCommands = new ArrayList<String>();
		endCommands.add("InFilePreperation");
		
		for (String command : startCommands) {
			newDependencies.put(command, composedDependencies.get(command));			
			iterateDependenciesUp(command);
		}
		
		for (String command : endCommands) {
			iterateDependenciesDown(command);
			newDependencies.put(command, new ArrayList<String>());
		}

		composedDependencies = newDependencies;
	}
	
	public void iterateDependenciesUp(String command) {
		for (String elem : composedDependencies.get(command)) {
			newDependencies.put(elem, composedDependencies.get(elem));
			iterateDependenciesUp(elem);
		}
	}
	
	public void iterateDependenciesDown(String command) {
		List<String> list = new ArrayList<String>(newDependencies.get(command));
		
//		pruefe ob command auch von anderen gebraucht wird, dazu muss es mehr als einmal in den values vorkommen
		if (!newDependencies.containsValue(command)) {
			newDependencies.remove(command);
		}
		
		for (String elem : list) {
			iterateDependenciesDown(elem);
		}
	}

}
