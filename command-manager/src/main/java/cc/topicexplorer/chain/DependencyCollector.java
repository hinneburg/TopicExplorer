package cc.topicexplorer.chain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.Command;
import org.apache.log4j.Logger;

import com.google.common.annotations.VisibleForTesting;

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
	private final Logger logger = Logger.getRootLogger();

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
	private void makeDotFile(Map<String, Set<String>> composedDependencies,
			Map<String, Set<String>> optionalDependencies, String name) {
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
			File dir = new File("etc");
			if (!dir.exists()) {
				dir.mkdir();
			}

			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("etc/graph" + name + ".dot"));
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
	@VisibleForTesting
	void updateDependencies(String name, Map<String, Set<String>> dependencies, Set<String> afterDependencies,
			Set<String> beforeDependencies) {

		if (dependencies.containsKey(name)) {
			dependencies.get(name).addAll(beforeDependencies);
		} else {
			dependencies.put(name, beforeDependencies);
		}

		if (!afterDependencies.isEmpty()) {
			for (String key : afterDependencies) {
				if (dependencies.containsKey(key)) {
					dependencies.get(key).add(name);
				} else {
					dependencies.put(key, new HashSet<String>(Arrays.asList(name)));
				}
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
	public Map<String, Set<String>> getDependencies() {
		Map<String, Set<String>> dependencies = new HashMap<String, Set<String>>();
		Map<String, Set<String>> optionalDependencies = new HashMap<String, Set<String>>();
		Map<String, Set<String>> composedDependencies = null;

		try {
			DependencyContext dependencyContext = new DependencyContext();
			String name;

			for (Iterator<?> it = catalog.getNames(); it.hasNext();) {
				name = (String) it.next();

				Command command = catalog.getCommand(name);
				command.execute(dependencyContext);

				updateDependencies(name, dependencies, dependencyContext.getAfterDependencies(),
						dependencyContext.getBeforeDependencies());
				updateDependencies(name, optionalDependencies, dependencyContext.getOptionalAfterDependencies(),
						dependencyContext.getOptionalBeforeDependencies());
			}

			// start
		} catch (Exception e) {
			logger.info("Error 1");
			logger.error(e.getStackTrace());
		}

		logger.info("deps " + dependencies);
		logger.info("opt deps " + optionalDependencies);
		try {

			// ende einfügung

			composedDependencies = new HashMap<String, Set<String>>(dependencies);

			// start
		} catch (Exception e) {
			logger.info("Error 2");
			logger.error(e.getStackTrace());
		}
		try {

			// ende

			for (String key : optionalDependencies.keySet()) {

				// start
				logger.info(key);
				try {
					// ende

					optionalDependencies.get(key);

					// start
				} catch (Exception e) {
					logger.info("Error 3a");
					logger.error(e.getStackTrace());
				}
				// ende

				// If anweisung als erster Fix eingefuegt. Bitte pruefen ob das
				// richtig ist.
				if (composedDependencies.containsKey(key)) {
					for (String value : optionalDependencies.get(key)) {
						if (composedDependencies.containsKey(value)) {
							// start
							try {
								// ende

								composedDependencies.get(key).add(value);
								// start
							} catch (Exception e) {
								logger.info("Error 3b");
								logger.info("key " + key);
								logger.info("value " + value);
								logger.info("containsKey " + composedDependencies.containsKey(value) + " getKey "
										+ composedDependencies.get(key));
								logger.error(e.getStackTrace());
							}
							// ende
						}
					}
				}
			}
			// start
		} catch (Exception e) {
			logger.info("Error 3");
			logger.error(e.getStackTrace());
		}

		try {
			// ende
			makeDotFile(composedDependencies, optionalDependencies, "");

		} catch (Exception e) {
			// logger.error(e.getMessage());

			// start
			logger.info("Error 4");
			logger.error(e.getStackTrace());
			// ende
		}

		return composedDependencies;
	}

	/**
	 * Topologically sorts the composedDependencies and sets the orderedCommands
	 * variable.
	 */
	public List<String> orderCommands(Map<String, Set<String>> dependencies) {
		Map<String, Set<String>> concurrentDependencies = new ConcurrentHashMap<String, Set<String>>(dependencies);
		List<String> orderedCommands = new ArrayList<String>();
		List<String> helpList = new ArrayList<String>();
		String node = "";

		// find all nodes with no dependencies, put into helpList, remove from
		// HashMap
		for (String key : concurrentDependencies.keySet()) {
			Set<String> list = concurrentDependencies.get(key);

			if (list.isEmpty()) {
				helpList.add(key);
				concurrentDependencies.remove(key);
			}
		}

		// as long as helpList contains a node without dependencies, take one,
		// remove it from helpList, put into commandList
		while (!helpList.isEmpty()) {
			node = helpList.iterator().next();
			helpList.remove(node);
			orderedCommands.add(node);

			// check if there is any edge between the node and another one
			for (String key : concurrentDependencies.keySet()) {
				Set<String> list = concurrentDependencies.get(key);

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
			logger.error("The dependencyMap wasn't empty yet but it should have been: " + concurrentDependencies);
			throw new IllegalStateException();
		}

		return orderedCommands;
	}

	public Map<String, Set<String>> getStrongComponents(Map<String, Set<String>> dependencies,
			Set<String> startCommands, Set<String> endCommands) {

		Map<String, Set<String>> newDependencies = new HashMap<String, Set<String>>();

		logger.info("startCommands " + startCommands + "+++");

		if (startCommands.isEmpty()) {
			newDependencies.putAll(dependencies);
		} else {
			for (String command : startCommands) {
				// pruefen, ob es sich wirklich um Wurzel handelt
				// dazu muss command als key mit leerer value-Menge vorhanden
				// sein
				if (!dependencies.get(command).isEmpty()) {
					logger.error("Given command seems not to be a root.");
					throw new IllegalStateException();
				} else {
					// fuege aktuelles Element mit leeren values hinzu
					newDependencies.put(command, new HashSet<String>());
					iterateDependenciesDown(dependencies, newDependencies, command);
				}
			}
		}

		for (String command : endCommands) {
			iterateDependenciesUp(command);
		}

		makeDotFile(newDependencies, new HashMap<String, Set<String>>(), "_strongComponents");

		return newDependencies;
	}

	private void iterateDependenciesUp(String command) {
		// to be done
	}

	private void iterateDependenciesDown(Map<String, Set<String>> dependencies,
			Map<String, Set<String>> newDependencies, String command) {

		// pruefe welche keys das aktuelle command in value-Liste haben, d.h.
		// welche commands von dem aktuellen abhaengen
		for (String key : dependencies.keySet()) {
			if (dependencies.get(key).contains(command)) {
				// falls enthalten, muss es in neue Map und rekursiv
				// abhanegigkeiten fuer dieses command pruefen
				Set<String> tmp = new HashSet<String>();
				tmp.add(command);
				if (newDependencies.containsKey(key)) {
					tmp.addAll(newDependencies.get(key));
				}
				newDependencies.put(key, tmp);
				iterateDependenciesDown(dependencies, newDependencies, key);
			}
		}
	}
}
