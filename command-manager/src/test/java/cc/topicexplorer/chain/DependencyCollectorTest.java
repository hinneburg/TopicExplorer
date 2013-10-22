package cc.topicexplorer.chain;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.config.ConfigParser;
import org.apache.commons.chain.impl.CatalogFactoryBase;
import org.junit.Before;
import org.junit.Test;

public class DependencyCollectorTest {

	private DependencyCollector dependencyCollector;

	@Before
	public void init() {
		try {
			new ConfigParser().parse(this.getClass().getResource("/DummyCatalog.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Catalog catalog = CatalogFactoryBase.getInstance().getCatalog();

		dependencyCollector = new DependencyCollector(catalog);
	}

	@Test
	public void testUpdateDependencies() {
		Map<String, Set<String>> dependencies = new HashMap<String, Set<String>>();
		Set<String> afterDependencies = new HashSet<String>(Arrays.asList("after1", "after2"));
		Set<String> beforeDependencies = new HashSet<String>(Arrays.asList("before1", "before2"));

		dependencyCollector.updateDependencies("command", dependencies, afterDependencies, beforeDependencies);

		assertThat(dependencies.get("after1")).contains("command");
		assertThat(dependencies.get("after2")).contains("command");
		assertThat(dependencies.get("command")).contains("before1", "before2");

	}

	@Test
	public void testUpdateDependenciesWithExistingDependencies() {
		Map<String, Set<String>> dependencies = new HashMap<String, Set<String>>();

		dependencies.put("command_allready1", new HashSet<String>());
		dependencies.put("command_allready2", new HashSet<String>(Arrays.asList("before1")));
		dependencies.put("command_allreadyA",
				new HashSet<String>(Arrays.asList("command_allready1", "command_allready3", "command_allready4")));

		Set<String> afterDependencies = new HashSet<String>(Arrays.asList("command_allreadyA"));
		Set<String> beforeDependencies = new HashSet<String>(Arrays.asList("before2", "before3"));

		dependencyCollector
				.updateDependencies("command_allready2", dependencies, afterDependencies, beforeDependencies);

		assertThat(dependencies.get("command_allready1")).contains();
		assertThat(dependencies.get("command_allready2")).containsOnly("before1", "before2", "before3");
		assertThat(dependencies.get("command_allreadyA")).containsOnly("command_allready1", "command_allready2",
				"command_allready3", "command_allready4");
	}

}
