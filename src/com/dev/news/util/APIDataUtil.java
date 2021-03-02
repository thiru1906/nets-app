//$Id$
package com.dev.news.util;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

public class APIDataUtil {
	
	public static String getDateFromLong(long millis) {
		Date date = new Date(millis);
		return date.toString();
	}
	
	
	public static JSONObject getCriteria(String attrName, Object value, String condition) {
		
		JSONObject obj = new JSONObject();
		obj.put("field_name", attrName);
		obj.put("value", value);
		obj.put("condition", condition);
		
		return new JSONObject().put("criteria", new JSONArray().put(obj));
	}
}
