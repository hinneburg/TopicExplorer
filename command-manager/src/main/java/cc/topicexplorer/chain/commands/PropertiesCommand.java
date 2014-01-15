package cc.topicexplorer.chain.commands;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import cc.topicexplorer.chain.CommunicationContext;

public class PropertiesCommand extends DependencyCommand {
	private Properties properties;
	private final Logger logger = Logger.getRootLogger();

	/**
	 * @throws MissingResourceException
	 *             if local.config.properties could not be resolved as a stream
	 * @throws IOException
	 *             if an error occured when reading from the input stream
	 * @throws IllegalStateException
	 *             if one of the essential properties could not be loaded as
	 *             they are config.local.properties, database.global.properties,
	 *             database.local.properties
	 */
	@Override
	public void specialExecute(Context context) {

		CommunicationContext communicationContext = (CommunicationContext) context;

		InputStream propertyInput = this.getClass().getResourceAsStream("/config.global.properties");
		if (propertyInput != null) {
			// global init
			this.logger.info("Loading config.global.properties");
			this.properties = new Properties();
			try {
				this.properties.load(propertyInput);
			} catch (IOException e) {
				logger.error("An error occured when reading from the input stream /config.global.properties");
				throw new RuntimeException(e);
			}

			// load local init, global db properties and local db properties
			if (!loadPropertyFile("config.local.properties", "", PropertyKind.LOCAL)) {
				throw new IllegalStateException("Essential config.local.properties could not be loaded.");
			}
			if (!loadPropertyFile("database.global.properties", "database.", PropertyKind.GLOBAL)) {
				throw new IllegalStateException("Essential database.global.properties could not be loaded.");
			}
			if (!loadPropertyFile("database.local.properties", "database.", PropertyKind.LOCAL)) {
				throw new IllegalStateException("Essential database.local.properties could not be loaded.");
			}

			// get enabled plugins
			String[] plugins = this.properties.getProperty("plugins").split(",");

			// check the plugin array
			if (plugins.length > 1 || plugins[0].replaceAll("\\s", "").length() > 0) {

				// load plugin property files
				for (String plugin : plugins) {
					String clean = plugin.replaceAll("\\s", "").toLowerCase();
					this.properties.setProperty("plugin_" + clean, "true");
					String globalPluginName = clean + ".global.properties";
					String localPluginName = clean + ".local.properties";
					String prefix = clean.substring(0, 1).toUpperCase() + clean.substring(1) + "_";
					loadPropertyFile(globalPluginName, prefix, PropertyKind.GLOBAL);
					loadPropertyFile(localPluginName, prefix, PropertyKind.LOCAL);
				}
			}

			communicationContext.put("properties", this.properties);

		} else {
			this.logger.error("config.global.properties could not be transformed to a stream");
			throw new MissingResourceException("Resource could not be transformed to a stream",
					InputStream.class.getSimpleName(), "/config.global.properties");
		}
	}

	private enum PropertyKind {
		LOCAL, GLOBAL
	}

	private boolean loadPropertyFile(String resource, String prefix, PropertyKind kind) {

		InputStream propertyInput = this.getClass().getResourceAsStream("/" + resource);

		if (propertyInput != null) {
			this.logger.info("Loading " + resource);

			Properties localProp = new Properties();

			try {
				localProp.load(propertyInput);
			} catch (IOException e) {
				logger.warn("An error occured when reading from the input stream /" + resource, e);
				return false;
			}

			Enumeration<?> eLocal = localProp.propertyNames();

			while (eLocal.hasMoreElements()) {
				String key = (String) eLocal.nextElement();
				if (kind.equals(PropertyKind.LOCAL) && !hasAttr(prefix + key)) {
					this.logger.warn("Global equivalent for " + prefix + key + " not found in "
							+ resource.replace("local", "global"));
				}
				this.properties.setProperty(prefix + key, localProp.getProperty(key));
			}

		} else {
			this.logger.warn(resource + " not found");
			return false;
		}

		return true;
	}

	private boolean hasAttr(String attribute) {
		Enumeration<?> eAll = this.properties.propertyNames();
		while (eAll.hasMoreElements()) {
			if (attribute.equals(eAll.nextElement())) {
				return true;
			}
		}
		return false;
	}
}
