package cc.topicexplorer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;

public final class PropertiesUtil {
	private static final Logger logger = Logger.getLogger(PropertiesUtil.class);
	private final Properties _hostProperties;

	/**
	 * 
	 * @param hostProperties
	 *            must not be null.
	 */
	public PropertiesUtil(Properties hostProperties) {
		_hostProperties = Preconditions.checkNotNull(hostProperties);
	}

	/**
	 * @return {@link Properties} object formerly filled by
	 *         {@link #loadPropertyFile(String, String, PropertyKind)
	 *         loadPropertyFile} method.
	 */
	public Properties getHostProperties() {
		return _hostProperties;
	}

	/**
	 * Loads a property file from a resource and stores it into the
	 * {@code hostProperties}. If any property in the property file already
	 * exists in the {@code hostProperties} the latter will be overwritten.
	 * 
	 * @param resource
	 *            will be loaded into a temporary property object via
	 *            {@link Properties#load()}. Properties will be used to update
	 *            the {@code hostProperties}.
	 * @param prefix
	 *            Every key of the {@code hostProperties} will be led by this
	 *            prefix
	 * @param propertyKind
	 *            {@code LOCAL} or {@code GLOBAL}. If {@code LOCAL} then a
	 *            warning will be logged everytime there is no {@code GLOBAL}
	 *            equivalent to a specific property.
	 * @return {@code false} if an error would occur while reading from the
	 *         input stream and if no resource with the name, specified by the
	 *         {@resource} parameter, is found. {@code true} otherwise.
	 */
	public boolean loadPropertyFile(String resource, String prefix, PropertyKind propertyKind) {
		Preconditions.checkArgument(!resource.trim().isEmpty());
		Preconditions.checkNotNull(propertyKind);

		InputStream propertyInput = PropertiesUtil.class.getResourceAsStream("/" + resource);
		if (propertyInput != null) {
			Properties temporaryProperties = new Properties();
			try {
				temporaryProperties.load(propertyInput);
			} catch (IOException e) {
				logger.warn("An error occured when reading from the input stream /" + resource, e);
				return false;
			}

			@SuppressWarnings("unchecked")
			ArrayList<String> propertyNames = (ArrayList<String>) Collections.list(temporaryProperties.propertyNames());

			if (propertyKind.equals(PropertyKind.LOCAL)) {
				for (String propertyName : propertyNames) {
					if (!hasAttr(prefix + propertyName)) {
						logger.warn("Global equivalent for " + prefix + propertyName + " not found in "
								+ resource.replace("local", "global"));
					}
				}
			}

			for (String propertyName : propertyNames) {
				_hostProperties.setProperty(prefix + propertyName, temporaryProperties.getProperty(propertyName));
			}

		} else {
			logger.warn(resource + " not found");
			return false;
		}

		return true;
	}

	public static enum PropertyKind {
		LOCAL, GLOBAL
	}

	private boolean hasAttr(String attribute) {
		Enumeration<?> eAll = _hostProperties.propertyNames();
		while (eAll.hasMoreElements()) {
			if (attribute.equals(eAll.nextElement())) {
				return true;
			}
		}
		return false;
	}

}
