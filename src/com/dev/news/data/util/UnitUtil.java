//$Id$
package com.dev.news.data.util;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dev.news.constants.ApiOperation;
import com.dev.news.data.Attribute;
import com.dev.news.data.Unit;
import com.dev.news.handler.Handler;
import com.dev.news.init.Loader;

public class UnitUtil {
	
	public static Unit getUnit(String unitName) {
		return Loader.unitMap.get(unitName);
	}
	
	public static Attribute[] getAttributes(JSONArray arr, String unit) {
		
		Attribute[] attributes = new Attribute[arr.length()];
		
		for(int i = 0; i < arr.length(); i++) {
			
			JSONObject obj= arr.getJSONObject(i);
			Attribute attr = new Attribute(obj, unit);
			attributes[i] = (attr);
		}
		
		return attributes;
	}
	
	public static List<String> getOperations(JSONArray arr){
		
		List<String> list = new ArrayList<>();
		
		for(int i = 0; i < arr.length(); i++) {
			String data = arr.getString(i);
			
			if(ApiOperation.ADD.equals(data)) {
				list.add(ApiOperation.ADD);
			} else if(ApiOperation.EDIT.equals(data)) {
				list.add(ApiOperation.EDIT);
			} else if(ApiOperation.DELETE.equals(data)) {
				list.add(ApiOperation.DELETE);
			} else {
				list.add(ApiOperation.GET);
			}
		}
		
		return list;
	}
	
	public static Handler getHandler(Unit un) throws Exception{
		if(!Loader.handlerMap.containsKey(un.getUnit())) {	
			synchronized(Loader.handlerMap) {
				if(!Loader.handlerMap.containsKey(un.getUnit())) {
				   Class c = Class.forName(un.getHandlerName());
				   Constructor cons = c.getConstructor(Unit.class);
				   Handler handler = (Handler) cons.newInstance(un);
				   Loader.handlerMap.put(un.getUnit(), handler);
				}
			}
		}
		
		return Loader.handlerMap.get(un.getUnit());
			
	}
	
	public static Unit getUnitByPath(String path) {
		return Loader.unitPathMap.get(path);
	}
	
	
	public static Attribute getAttributeByName(Unit unit, String name) {
		Attribute[] attr = unit.getAttributes();
		for(int i = 0; i < attr.length; i++) {
			if(attr[i].getDisplayName().equals(name)){
				return attr[i];
			}
		}
		return null;
	}
}
