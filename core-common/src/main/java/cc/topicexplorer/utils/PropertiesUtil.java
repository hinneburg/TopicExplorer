package cc.topicexplorer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public final class PropertiesUtil {
	private static final Logger logger = Logger.getLogger(PropertiesUtil.class);

	/**
	 * Create a new properties object. Throw exceptions rigorously.
	 * 
	 * @param resource
	 *            file name of properties file. Must not be null. A
	 *            {@link RuntimeException} will be thrown if the resource cannot
	 *            be found. A {@link IOException} will be thrown if there is an
	 *            error with the properties file, e. g. with the properties
	 *            format.
	 * @param prefix
	 *            Every key of the returned properties will be led by this
	 *            prefix. May be null or empty.
	 * @param propertyKind
	 *            {@code LOCAL} or {@code GLOBAL}. If {@code LOCAL} then a
	 *            warning will be logged every time there is no {@code GLOBAL}
	 *            equivalent to a specific property. Must not be null.
	 * @return a new {@link Properties} object representing the resource
	 *         properties file.
	 */
	public static Properties loadMandatoryProperties(String resource, String prefix, PropertyKind propertyKind) {
		return updateMandatoryProperties(new Properties(), resource, prefix, propertyKind);
	}

	/**
	 * Update existing properties with new ones. Throw exceptions rigorously.
	 * 
	 * @param properties
	 *            will be updated with properties in {@link resource} file if it
	 *            exists. If any property in the {@link resource} file already
	 *            exists in this properties object the latter will be
	 *            overwritten.
	 * @param resource
	 *            file name of properties file. Must not be null. A
	 *            {@link RuntimeException} will be thrown if the resource cannot
	 *            be found. A {@link IOException} will be thrown if there is an
	 *            error with the properties file, e. g. with the properties
	 *            format.
	 * @param prefix
	 *            Every key of the returned properties will be led by this
	 *            prefix. May be null or empty.
	 * @param propertyKind
	 *            {@code LOCAL} or {@code GLOBAL}. If {@code LOCAL} then a
	 *            warning will be logged every time there is no {@code GLOBAL}
	 *            equivalent to a specific property. Must not be null.
	 * @return a new {@link Properties} object representing the resource
	 *         properties file if it exists. If not a copy of {@link properties}
	 *         will be returned.
	 */
	public static Properties updateMandatoryProperties(Properties properties, String resource, String prefix,
			PropertyKind propertyKind) {
		return updateProperties(properties, resource, prefix, propertyKind, true);
	}

	/**
	 * Update existing properties with new ones. Missing {@link resource} will
	 * be of no consequence.
	 * 
	 * @param properties
	 *            will be updated with properties in {@link resource} file if it
	 *            exists. If any property in the {@link resource} file already
	 *            exists in this properties object the latter will be
	 *            overwritten.
	 * @param resource
	 *            file name of properties file. Must not be null. If the
	 *            resource cannot be found a copy of {@link properties} will be
	 *            returned. A {@link IOException} will be thrown if there is an
	 *            error with the properties file, e. g. with the properties
	 *            format.
	 * @param prefix
	 *            Every key of the returned properties will be led by this
	 *            prefix. May be null or empty.
	 * @param propertyKind
	 *            {@code LOCAL} or {@code GLOBAL}. If {@code LOCAL} then a
	 *            warning will be logged every time there is no {@code GLOBAL}
	 *            equivalent to a specific property. Must not be null.
	 * @return a new {@link Properties} object representing the resource
	 *         properties file if it exists. If not a copy of {@link properties}
	 *         will be returned.
	 */
	public static Properties updateOptionalProperties(Properties properties, String resource, String prefix,
			PropertyKind propertyKind) {
		return updateProperties(properties, resource, prefix, propertyKind, false);
	}

	private static Properties updateProperties(Properties properties, String resource, String prefix,
			PropertyKind propertyKind, boolean mandatory) {
		Preconditions.checkNotNull(properties);
		Preconditions.checkArgument(!Strings.isNullOrEmpty(resource));
		Preconditions.checkNotNull(propertyKind);
		prefix = (prefix == null ? "" : prefix);

		Properties updatedProperties = new Properties();
		updatedProperties.putAll(properties);

		InputStream propertyInput = PropertiesUtil.class.getResourceAsStream("/" + resource);
		if (propertyInput == null) {
			if (mandatory) {
				RuntimeException loadException = new RuntimeException("Mandatory properties " + resource
						+ " not found.");
				logger.error(loadException);
				throw loadException;
			} else {
				logger.warn("Optional properties " + resource + " not found.");
			}
		} else {
			Properties loadedProperties = new Properties();
			try {
				loadedProperties.load(propertyInput);
			} catch (IOException e) {
				RuntimeException runtimeIOException = new RuntimeException("Mandatory " + resource
						+ " could not be loaded.", e);
				logger.error(runtimeIOException);
				throw runtimeIOException;
			}

			@SuppressWarnings("unchecked")
			Iterable<String> propertyNames = (Iterable<String>) Collections.list(loadedProperties.propertyNames());
			if (propertyKind.equals(PropertyKind.LOCAL)) {
				for (String propertyName : propertyNames) {
					if (!hasAttribute(updatedProperties, prefix + propertyName)) {
						logger.warn("Global equivalent for " + prefix + propertyName + " not found in "
								+ resource.replace("local", "global"));
					}
				}
			}

			for (String propertyName : propertyNames) {
				updatedProperties.setProperty(prefix + propertyName, loadedProperties.getProperty(propertyName));
			}
		}

		return updatedProperties;
	}

	public static enum PropertyKind {
		LOCAL, GLOBAL
	}

	private static boolean hasAttribute(Properties properties, String attribute) {
		Enumeration<?> propertyNames = properties.propertyNames();
		while (propertyNames.hasMoreElements()) {
			if (attribute.equals(propertyNames.nextElement())) {
				return true;
			}
		}
		return false;
	}

}
