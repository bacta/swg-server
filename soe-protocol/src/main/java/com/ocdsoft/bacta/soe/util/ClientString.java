package com.ocdsoft.bacta.soe.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ClientString {

	private final static Logger logger = LoggerFactory.getLogger(ClientString.class);

	private static Properties prop;
	
	static {
		InputStream stream = ClientString.class.getResourceAsStream("/clientstrings.properties");
		prop = new Properties();
		try {
			prop.load(stream);
		} catch (IOException e) {
			logger.error("Error Loading ClientStrings", e);
		}
	}

	public static String get(String propertyName) {
		if(!prop.containsKey(propertyName))
			return "Unknown";
		
		return prop.getProperty(propertyName);
	}
	
	public static String get(int propertyName) {
		return get(Integer.toHexString(propertyName));
	}

	public static boolean containsKey(String name) {
		return prop.containsKey(name);
	}

	public static Set<Map.Entry<Object, Object>> entrySet() {
		return prop.entrySet();
	}
	
	
}
