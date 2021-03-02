//$Id$
package com.dev.news.handler;

import org.json.JSONObject;

import com.dev.news.constants.ApiOperation;
import com.dev.news.data.Unit;
import com.dev.news.servlet.APIServlet;

public class InternalPublisherHandler extends GenericHandler{

	public InternalPublisherHandler(Unit en) {
		super(en);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void modifyInputData(String method, JSONObject input) {
		
	}
	
	
	@Override
	protected boolean isUserAuthorized(String path, String method) {
		if(!ApiOperation.GET.equalsIgnoreCase(method)) {
			JSONObject userInfo = APIServlet.getUserInfo();
			if(!userInfo.has("publisher_id")) {
				return false;
			}
		}
		
		return true;
	}
	
}
