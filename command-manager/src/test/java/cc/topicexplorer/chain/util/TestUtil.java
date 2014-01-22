package cc.topicexplorer.chain.util;

import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.config.ConfigParser;
import org.apache.commons.chain.impl.CatalogFactoryBase;

public final class TestUtil {

	private TestUtil() {
		throw new UnsupportedOperationException();
	}

	public static Catalog initializeNewCatalog(String url) {
		try {
			CatalogFactoryBase.clear();
			new ConfigParser().parse(TestUtil.class.getResource(url));
		} catch (Exception e) {// Exception type cannot be more specified, due
								// to parse()-signature
			e.printStackTrace();
		}
		return CatalogFactoryBase.getInstance().getCatalog();
	}

}
