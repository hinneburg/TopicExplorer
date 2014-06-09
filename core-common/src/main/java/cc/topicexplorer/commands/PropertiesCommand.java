package cc.topicexplorer.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.topicexplorer.utils.PropertiesUtil;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class PropertiesCommand implements Command {

	private static final Logger logger = Logger.getLogger(PropertiesCommand.class);

	@VisibleForTesting
	static final String PROPERTIES_KEY = "properties";
	private static final String PLUGINS_KEY = "plugins";
	private static final String NO_PREFIX = "";
	private static final String DATABASE_PREFIX = "database.";

	/**
	 * @throws MissingResourceException
	 *             if local.config.properties could not be resolved as a stream
	 * @throws IOException
	 *             if an error occured when reading from the input stream
	 * @throws IllegalStateException
	 *             if one of the essential properties could not be loaded as they are config.local.properties,
	 *             database.global.properties, database.local.properties
	 */
	@Override
	public void execute(Context context) {
		Properties properties = PropertiesUtil.loadMandatoryProperties("config", NO_PREFIX);
		properties = PropertiesUtil.updateMandatoryProperties(properties, "database", DATABASE_PREFIX);
		properties = loadPluginProperties(properties);

		context.bind(PROPERTIES_KEY, properties);
	}

	private static Properties loadPluginProperties(Properties properties) {
		Properties pluginProperties = new Properties();
		pluginProperties.putAll(properties);

		Iterable<String> enabledPlugins = getEnabledPlugins(pluginProperties);
		for (String plugin : enabledPlugins) {
			if (Strings.isNullOrEmpty(plugin)) {
				logger.warn("Empty plugin detected in config properties.");
			} else {
				plugin = removeWhiteSpacesAndLowerCase(plugin);
				pluginProperties.setProperty("plugin_" + plugin, "true");
				String prefix = plugin.substring(0, 1).toUpperCase() + plugin.substring(1) + "_";
				pluginProperties = PropertiesUtil.updateOptionalProperties(pluginProperties, plugin, prefix);
			}
		}

		return pluginProperties;
	}

	private static Iterable<String> getEnabledPlugins(Properties properties) {
		return Arrays.asList(properties.getProperty(PLUGINS_KEY).split(","));
	}

	private static String removeWhiteSpacesAndLowerCase(String plugin) {
		return plugin.replaceAll("\\s", "").toLowerCase();
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}

}
