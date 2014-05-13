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
	private static final String GLOBAL_PROPERTIES = ".global.properties";
	private static final String LOCAL_PROPERTIES = ".local.properties";
	private static final boolean MANDATORY = true;

	/**
	 * Create a new properties object and load global and local properties to the given property name into this object.
	 * Throw exceptions rigorously.
	 * 
	 * @param propertyName
	 *            property name of required properties. Must not be null. Will be extended with ".global.properties" or
	 *            ".local.properties", respectively. A {@link RuntimeException} will be thrown if the resource cannot be
	 *            found. A {@link IOException} will be thrown if there is an error with the properties file, e. g. with
	 *            the properties format.
	 * @param prefix
	 *            every key of the returned properties will be led by this prefix. May be null or empty.
	 * @return a new {@link Properties} object representing where the given properties have been updated.
	 */
	public static Properties loadMandatoryProperties(String propertyName, String prefix) {
		Properties properties = updateProperties(new Properties(), propertyName + GLOBAL_PROPERTIES, prefix,
				PropertyKind.GLOBAL, MANDATORY);
		properties = updateProperties(properties, propertyName + LOCAL_PROPERTIES, prefix, PropertyKind.LOCAL,
				MANDATORY);
		return properties;
	}

	/**
	 * Update existing properties with new ones. Global and local properties to the given property name are required for
	 * this update. Throw exceptions rigorously.
	 * 
	 * @param properties
	 *            will be updated with properties in {@link propertyName} file. If any property in the
	 *            {@link propertyName} file already exists in this properties object the latter will be overwritten.
	 * @param propertyName
	 *            property name of required properties. Must not be null. Will be extended with ".global.properties" or
	 *            ".local.properties", respectively. A {@link RuntimeException} will be thrown if the resource cannot be
	 *            found. A {@link IOException} will be thrown if there is an error with the properties file, e. g. with
	 *            the properties format.
	 * @param prefix
	 *            every key of the returned properties will be led by this prefix. May be null or empty.
	 * @return a new {@link Properties} object where the given properties have been updated.
	 */
	public static Properties updateMandatoryProperties(Properties properties, String propertyName, String prefix) {
		Properties updatedProperties = updateProperties(properties, propertyName + GLOBAL_PROPERTIES, prefix,
				PropertyKind.GLOBAL, MANDATORY);
		return updateProperties(updatedProperties, propertyName + LOCAL_PROPERTIES, prefix, PropertyKind.LOCAL,
				MANDATORY);
	}

	/**
	 * Update existing properties with new ones. Global and local properties to the given property name are required for
	 * this update. Missing {@link propertyName} will be of no consequence.
	 * 
	 * @param properties
	 *            will be updated with properties in {@link propertyName} file if it exists. If any property in the
	 *            {@link propertyName} file already exists in this properties object the latter will be overwritten.
	 * @param propertyName
	 *            file name of properties file. Must not be null. If the resource cannot be found a copy of
	 *            {@link properties} will be returned. A {@link IOException} will be thrown if there is an error with
	 *            the properties file, e. g. with the properties format.
	 * @param prefix
	 *            Every key of the returned properties will be led by this prefix. May be null or empty.
	 * @return a new {@link Properties} object representing the resource properties file if it exists. If not a copy of
	 *         {@link properties} will be returned.
	 */
	public static Properties updateOptionalProperties(Properties properties, String propertyName, String prefix) {
		Properties updatedProperties = updateProperties(properties, propertyName + GLOBAL_PROPERTIES, prefix,
				PropertyKind.GLOBAL, !MANDATORY);
		return updateProperties(updatedProperties, propertyName + LOCAL_PROPERTIES, prefix, PropertyKind.LOCAL,
				!MANDATORY);
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

	private static enum PropertyKind {
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
