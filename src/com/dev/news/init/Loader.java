//$Id$
package com.dev.news.init;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.dev.news.data.Unit;
import com.dev.news.handler.Handler;

public class Loader {

	
	public static Map<String, Unit> unitMap = new HashMap<>();
	
	public static Map<String, Unit> unitPathMap = new HashMap<>();
	
	public static Map<String, Handler> handlerMap = new HashMap<>();
	
	private static List<String> getAllJSONFiles(String path){
		List<String> fileList = new ArrayList<>();
		
		File file = new File(path);
		if(file.isDirectory()) {
			for(File innerFile : file.listFiles()) {
				if(file.isDirectory()) {
					fileList.addAll(getAllJSONFiles(innerFile.getPath()));
				} else {
					if(innerFile.getName().endsWith("json") || innerFile.getName().endsWith("JSON")) {
						fileList.add(innerFile.getAbsolutePath());
					}
				}
			}
		} else {
			fileList.add(file.getAbsolutePath());
		}
		
		return fileList;
	}
	
	public static void createInstanceForUnits(List<String> filePaths) {
		for(String path : filePaths) {
			Logger.getLogger(Loader.class.toString()).log(Level.INFO, "unit path is " + path);
			JSONObject rawData = FileUtil.readJSONFile(path);
			
			Unit unit = new Unit(rawData);
			unitMap.put(unit.getUnit(), unit);
			unitPathMap.put(unit.getApiPath(), unit);
		}
	}
	
	public static void alterUserTableName() {
		Unit unit = unitMap.get("user");
		unit.setTableName("\"User\"");
	}
	
	//loading all the units
	public static void init() {
		String pathForUnit = System.getProperty("user.dir")+ "/../webapps/News/app_configuration";//"ZIDE/News"+ File.separator + "webapps" + File.separator + "app_configuration";
		Logger.getLogger(Loader.class.toString()).log(Level.INFO, "The path is "+ pathForUnit);
		List<String> filePaths = getAllJSONFiles(pathForUnit);
		createInstanceForUnits(filePaths);
		alterUserTableName();
	}
}
