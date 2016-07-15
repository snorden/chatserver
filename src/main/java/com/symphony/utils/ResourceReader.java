package com.symphony.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ResourceReader {
	/** 
	 * Reads properties from a file
	 * 
	 */
	
	public static Properties readPropertiesfromfile(String propertiesFile) throws IOException{
		try{
			Properties properties = new Properties();
			File propFile = new File(propertiesFile);
			if (!propFile.exists() || !propFile.canRead())
				throw new IOException("Cannot read properties file at location "+propertiesFile);
			try(BufferedReader reader = new BufferedReader(new FileReader(propFile));){
				String msg = null;
				while ((msg = reader.readLine()) != null){
					String[] split = msg.split("=",-1);
					if (split.length != 2) continue;
					properties.setProperty(split[0],split[1]);
				}
				return properties;
			}
		}catch(Exception e){
			return null;
		}
	}
	
}
