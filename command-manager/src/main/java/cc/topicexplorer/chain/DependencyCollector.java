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
	private Logger logger = Logger.getRootLogger();

	/**
	 * Class constructor taking the catalog argument and sets it in this class.
	 * 
	 * @param catalog
	 */
	public DependencyCollector(Catalog catalog) {
		this.catalog = catalog;
	}
	
	public DependencyCollector() {
		
	}

	/**
	 * Creates a file in dot format. A -> B means that A depends of B. A dashed
	 * line represents an optional dependency. It accesses the global dependency
	 * maps, so it must be executed before the maps are changed, e.g. before
	 * executing the orderCommands method because it changes the maps.
	 */
	private void makeDotFile(Map<String, List<String>> composedDependencies, Map<String, List<String>> optionalDependencies) {
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
	public Map<String, List<String>> getDependencies() {
		Map<String, List<String>> dependencies = new HashMap<String, List<String>>();
		Map<String, List<String>> optionalDependencies = new HashMap<String, List<String>>();
		Map<String, List<String>> composedDependencies = null;
		
		try {
			DependencyContext dependencyContext = new DependencyContext();
			String name;

			for (Iterator<?> it = catalog.getNames(); it.hasNext();) {
				name = (String) it.next();
				Command command = catalog.getCommand(name);
				command.execute(dependencyContext);

				updateDependencies(name, dependencies,
						dependencyContext.getAfterDependencies(),
						dependencyContext.getBeforeDependencies());
				updateDependencies(name, optionalDependencies,
						dependencyContext.getOptionalAfterDependencies(),
						dependencyContext.getOptionalBeforeDependencies());
			}
			
			composedDependencies = new HashMap<String, List<String>>(dependencies);
			
			for (String key : optionalDependencies.keySet()) {
				for (String value : optionalDependencies.get(key)) {
					if (composedDependencies.containsKey(value)) {
						composedDependencies.get(key).add(value);
					}
				}
			}
			
			makeDotFile(composedDependencies, optionalDependencies);
			
		} catch (Exception e) {
			logger.error(e);
		}
		
		return composedDependencies;
	}


	/**
	 * Topologically sorts the composedDependencies and sets the orderedCommands
	 * variable.
	 */
	public List<String> orderCommands(Map<String, List<String>> dependencies) {
		Map<String, List<String>> concurrentDependencies = new ConcurrentHashMap<String, List<String>>(
				dependencies);
		List<String> orderedCommands = new LinkedList<String>();
		LinkedList<String> helpList = new LinkedList<String>();
		String node = "";

		// find all nodes with no dependencies, put into helpList, remove from
		// HashMap
		for (String key : concurrentDependencies.keySet()) {
			List<String> list = concurrentDependencies.get(key);

			if (list.isEmpty()) {
				helpList.add(key);
				concurrentDependencies.remove(key);
			}
		}

		// as long as helpList contains a node without dependencies, take one,
		// remove it from helpList, put into commandList
		while (!helpList.isEmpty()) {
			node = helpList.getFirst();
			helpList.remove(node);
			orderedCommands.add(node);

			// check if there is any edge between the node and another one
			for (String key : concurrentDependencies.keySet()) {
				List<String> list = concurrentDependencies.get(key);

				// if the node is in a value list, remove it
				if (list.contains(node)) {
					list.remove(node);
					concurrentDependencies.put(key, list);
				}

				// if the node has no other incoming edges, put it into
				// commandList
				if (concurrentDependencies.get(key).isEmpty()) {
					helpList.add(key);
					concurrentDependencies.remove(key);
				}
			}
		}

		// only if the dependencyMap is empty the graph was correct, otherwise
		// there was something wrong with it
		if (!concurrentDependencies.isEmpty()) {
			logger.fatal("The dependencyMap wasn't empty yet but it should have been: "
					+ concurrentDependencies);
			System.exit(1);
		}
		
		return orderedCommands;
	}

	public Map<String, List<String>> getStrongComponents(Map<String, List<String>> dependencies, List<String> startCommands,
			List<String> endCommands) {

		Map<String, List<String>> newDependencies = new ConcurrentHashMap<String, List<String>>();
		
		if (startCommands.isEmpty()) {
			newDependencies.putAll(dependencies);
		} else {
			for (String command : startCommands) {
				iterateDependenciesDown(dependencies, newDependencies, command);
			}
		}

		for (String command : endCommands) {
			iterateDependenciesUp(command);
		}

		return newDependencies;
	}

	private void iterateDependenciesUp(String command) {
		//  to be done
	}

	private void iterateDependenciesDown(Map<String, List<String>> dependencies, Map<String, List<String>> newDependencies, String command) {
		// pruefe welche keys das aktuelle command in value-Liste haben, d.h.
		// welche commands von dem aktuellen abhaengen
		for (String key : dependencies.keySet()) {
			if (dependencies.get(key).contains(command)) {
				// falls enthalten, muss es in neue Map und rekursiv
				// abhanegigkeiten fuer dieses command pruefen
				List<String> tmp = new ArrayList<String>();
				tmp.add(command);
				if (newDependencies.get(key) != null) {
					tmp.addAll(newDependencies.get(key));
				}
				newDependencies.put(key, tmp);
				iterateDependenciesDown(dependencies, newDependencies, key);
			}
		}
	}

}
