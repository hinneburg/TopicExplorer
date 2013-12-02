package cc.topicexplorer.chain;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import cc.topicexplorer.chain.util.TestUtil;

public class DependencyCollectorTest {

	private DependencyCollector dependencyCollector;
	private Map<String, Set<String>> dependencies;

	@Before
	public void init() {
		this.dependencyCollector = new DependencyCollector(TestUtil.initializeNewCatalog("/DummyCatalog1.xml"));
		this.dependencies = new HashMap<String, Set<String>>();
	}

	@Test
	public void testUpdateDependencies() {
		Set<String> afterDependencies = new HashSet<String>(Arrays.asList("after1", "after2"));
		Set<String> beforeDependencies = new HashSet<String>(Arrays.asList("before1", "before2"));

		this.dependencyCollector
				.updateDependencies("command", this.dependencies, afterDependencies, beforeDependencies);

		assertThat(this.dependencies.get("after1")).contains("command");
		assertThat(this.dependencies.get("after2")).contains("command");
		assertThat(this.dependencies.get("command")).contains("before1", "before2");

	}

	@Test
	public void testUpdateDependenciesWithExistingDependencies() {
		this.dependencies.put("command_allready1", new HashSet<String>());
		this.dependencies.put("command_allready2", new HashSet<String>(Arrays.asList("before1")));
		this.dependencies.put("command_allreadyA",
				new HashSet<String>(Arrays.asList("command_allready1", "command_allready3", "command_allready4")));

		Set<String> afterDependencies = new HashSet<String>(Arrays.asList("command_allreadyA"));
		Set<String> beforeDependencies = new HashSet<String>(Arrays.asList("before2", "before3"));

		this.dependencyCollector.updateDependencies("command_allready2", this.dependencies, afterDependencies,
				beforeDependencies);

		assertThat(this.dependencies.get("command_allready1")).contains();
		assertThat(this.dependencies.get("command_allready2")).containsOnly("before1", "before2", "before3");
		assertThat(this.dependencies.get("command_allreadyA")).containsOnly("command_allready1", "command_allready2",
				"command_allready3", "command_allready4");
	}

	@Test
	public void testGetDependencies_beforeDependencies() {
		this.dependencies = this.dependencyCollector.getDependencies();
		assertThat(this.dependencies.get("DummyCommand1")).contains();
		assertThat(this.dependencies.get("DummyCommand2")).containsOnly("DummyCommand1");
		assertThat(this.dependencies.get("DummyCommand3")).containsOnly("DummyCommand2", "DummyCommand1");
	}

	@Test
	public void testGetDependencies_optionalBeforeDependencies() {
		this.dependencyCollector = new DependencyCollector(TestUtil.initializeNewCatalog("/DummyCatalog2.xml"));
		this.dependencies = this.dependencyCollector.getDependencies();
		assertThat(this.dependencies.get("DummyCommand3")).containsOnly("DummyCommand1");
	}

	@Test
	public void testGetDependencies_afterDependencies() {
		this.dependencyCollector = new DependencyCollector(TestUtil.initializeNewCatalog("/DummyCatalog3.xml"));
		this.dependencies = this.dependencyCollector.getDependencies();
		assertThat(this.dependencies.get("DummyCommand2")).containsOnly("DummyCommand1", "DummyCommand4");
	}

	@Test
	public void testGetDependencies_optionalAfterDependencies() {
		this.dependencyCollector = new DependencyCollector(TestUtil.initializeNewCatalog("/DummyCatalog4.xml"));
		this.dependencies = this.dependencyCollector.getDependencies();
		assertThat(this.dependencies.get("DummyCommand3")).contains("DummyCommand4");
	}

	@Test
	public void testOrderCommands() {
		this.dependencyCollector = new DependencyCollector(
				TestUtil.initializeNewCatalog("/DummyCatalog_for_ordering.xml"));
		this.dependencies = this.dependencyCollector.getDependencies();
		final List<String> orderedCommands = this.dependencyCollector.orderCommands(this.dependencies);
		orderedCommands.remove(("DummyCommand5"));
		assertThat(orderedCommands)
				.containsSequence("DummyCommand1", "DummyCommand4", "DummyCommand2", "DummyCommand3");

	}

	@AfterClass
	public static void removeDotFile() {
		if (new File("/home/user/workspace/topicexplorer-parent/command-manager/etc/graph.dot").delete()
				&& new File("/home/user/workspace/topicexplorer-parent/command-manager/etc").delete()) {
		} else {
			System.err.println("NOTE: A file or directory created for testing issues could not be removed.");
		}
	}
}
