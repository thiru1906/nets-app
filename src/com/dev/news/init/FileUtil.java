//$Id$
package com.dev.news.init;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

public class FileUtil {
	static final Logger LOGGER = Logger.getLogger(FileUtil.class.toString());
	
	
	public static JSONObject readJSONFile(String path) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			StringBuffer str = new StringBuffer();
			
			String temp  = br.readLine();
			
			while(temp != null) {
				str.append(temp);
				temp = br.readLine();
			}
			
			return new JSONObject(str.toString());
			
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		
		return null;
	}
}
