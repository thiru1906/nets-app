//$Id$
package com.dev.news.handler;

import org.json.JSONObject;

import com.dev.news.constants.ApiOperation;
import com.dev.news.data.Unit;
import com.dev.news.servlet.APIServlet;

public class TopicHandler extends GenericHandler{

	public TopicHandler(Unit en) {
		super(en);
	}

	@Override
	protected void modifyInputData(String method, JSONObject input) {
		
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
