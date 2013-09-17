package cc.topicexplorer.chain.commands;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import cc.topicexplorer.chain.CommunicationContext;


public class PropertiesCommand extends DependencyCommand {
	private Properties properties;
	private Logger logger = Logger.getRootLogger();
	
	private enum PropertyKind {
		LOCAL, GLOBAL
	}
	
	private void loadPropertyFile(String resource, String prefix, PropertyKind kind) throws IOException {
		logger.info("Loading" + resource);
		
		InputStream propertyInput = this.getClass().getResourceAsStream("/" + resource);
		if(propertyInput != null) {
			Properties localProp = new Properties();
			localProp.load(propertyInput);
			Enumeration<?> eLocal = localProp.propertyNames();
			
			while (eLocal.hasMoreElements()) {
				String key = (String) eLocal.nextElement();
				if(kind.equals(PropertyKind.LOCAL) && !hasAttr(prefix + key)) {
					logger.warn("Global equivalent for " 
							+ prefix + key 
							+ " not found in " 
							+ resource.replace("local", "global"));
				}		
				properties.setProperty(prefix + key, localProp.getProperty(key));
			}
		} else {
			logger.warn(resource + " not found");
		}
	}
	
	
	private boolean hasAttr(String attribute) {
		Enumeration<?> eAll = properties.propertyNames();
		while (eAll.hasMoreElements()) {
			if(attribute.equals((String) eAll.nextElement())) {
				return true;
			}
		}
		return false;		
	}
	
	
	@Override
	public void specialExecute(Context context) throws Exception {
		
		CommunicationContext communicationContext = (CommunicationContext) context;
		
		InputStream propertyInput = this.getClass().getResourceAsStream("/config.global.properties");
		if(propertyInput != null) {
			// global init
			logger.info("Loading config.global.properties");
			properties = new Properties();
			properties.load(propertyInput);
			// local init
			loadPropertyFile("config.local.properties", "", PropertyKind.LOCAL);;
			
			// global db properties
			loadPropertyFile("database.global.properties", "database.", PropertyKind.GLOBAL);
			// local db properties
			loadPropertyFile("database.local.properties", "database.", PropertyKind.LOCAL);
			
			// get enabled plugins
			String[] plugins = properties.getProperty("plugins").split(",");
			
			// check the plugin array
			if(plugins.length > 1 || plugins[0].replaceAll("\\s", "").length() > 0) {
			
				// load plugin property files
				for(String plugin: plugins) {
					String clean = plugin.replaceAll("\\s", "").toLowerCase();
					properties.setProperty("plugin_" + clean, "true");
					String globalPluginName = clean + ".global.properties";
					String localPluginName = clean + ".local.properties";
					String prefix = clean.substring(0, 1).toUpperCase() + clean.substring(1) + "_";
					loadPropertyFile(globalPluginName, prefix, PropertyKind.GLOBAL);
					loadPropertyFile(localPluginName, prefix, PropertyKind.LOCAL);
				}
			}
			
			communicationContext.put("properties", properties);
			
		} else {
			logger.fatal("config.global.properties not found");
			System.exit(0);
		}
	}
}
