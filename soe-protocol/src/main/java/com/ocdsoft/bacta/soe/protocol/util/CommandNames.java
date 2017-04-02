package com.ocdsoft.bacta.soe.protocol.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CommandNames {

	private static Logger logger = LoggerFactory.getLogger("CommandNames");

	private static Properties prop;
	
	static {
		InputStream stream = CommandNames.class.getResourceAsStream("/commandnames.properties");
		prop = new Properties();
		try {
			prop.load(stream);
		} catch (IOException e) {
			logger.error("Error Loading CommandNames", e);
		}
	}

	public static String get(String propertyName) {
		if(!prop.containsKey(propertyName.toLowerCase())) {
            return ClientString.get(propertyName);
		}
		
		return prop.getProperty(propertyName.toLowerCase());
	}
	
	public static String get(int propertyName) {
		return get(Integer.toHexString(propertyName));
	}
}
