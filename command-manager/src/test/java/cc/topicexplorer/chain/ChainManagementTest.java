package cc.topicexplorer.chain;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import cc.topicexplorer.exceptions.CatalogNotInstantiableException;

public class ChainManagementTest {

	private final ChainManagement _chainManager = new ChainManagement();

	@Test
	public void testSetCatalog() {
		try {
			_chainManager.setCatalog("/DummyCatalog1.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertThat(_chainManager.catalog).isNotNull();
	}

	@Test(expected = CatalogNotInstantiableException.class)
	public void testSetCatalogWithNoValidCatalog() throws Exception {
		try {
			_chainManager.setCatalog("");
			fail();
		} catch (Exception e) {
			throw e;
		}
	}

}
