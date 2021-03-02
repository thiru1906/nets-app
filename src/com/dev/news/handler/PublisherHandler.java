//$Id$
package com.dev.news.handler;

import java.util.logging.Level;

import org.json.JSONObject;

import com.dev.news.constants.ApiOperation;
import com.dev.news.data.Attribute;
import com.dev.news.data.Unit;
import com.dev.news.data.util.UnitUtil;
import com.dev.news.servlet.APIServlet;
import com.dev.news.util.APIDataUtil;
import com.dev.news.util.APITransactionUtil;

public class PublisherHandler extends GenericHandler{

	public PublisherHandler(Unit en) {
		super(en);
	}

	@Override
	protected void modifyInputData(String method, JSONObject input) {
		
	}

	@Override
	protected boolean isUserAuthorized(String path , String method) {
		
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
