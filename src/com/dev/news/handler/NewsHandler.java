//$Id$
package com.dev.news.handler;

import org.json.JSONObject;

import com.dev.news.constants.ApiOperation;
import com.dev.news.data.Attribute;
import com.dev.news.data.Unit;
import com.dev.news.servlet.APIServlet;
import com.dev.news.util.APIDataUtil;

public class NewsHandler extends GenericHandler{

	public NewsHandler(Unit en) {
		super(en);
	}
	
	@Override
	protected void modifyInputData(String method, JSONObject input) {
		if(method.equalsIgnoreCase(ApiOperation.ADD)) {
			input.getJSONObject("data")
			.put("created_time", System.currentTimeMillis());
		}
		
		if(method.equalsIgnoreCase(ApiOperation.EDIT)) {
			input.getJSONObject("data")
			.remove("created_time");
		}
	}
	
	@Override
	protected void modifyOutputData(JSONObject data) {
		JSONObject unitData = data.getJSONObject("data");
		
		Long millis = unitData.getLong("created_time");
		unitData.put("created_time", APIDataUtil.getDateFromLong(millis));
	}
	
	@Override
	protected boolean isUserAuthorized(String path, String method) {
		
		if(ApiOperation.ADD.equalsIgnoreCase(method) ||
				ApiOperation.EDIT.equalsIgnoreCase(method) ||
				ApiOperation.DELETE.equalsIgnoreCase(method)) {
			
			JSONObject cacheObj = APIServlet.getUserInfo();
			if(!cacheObj.has("publisher_id")) {
				return false;
			}
		}
		
		return true;
	}
}
