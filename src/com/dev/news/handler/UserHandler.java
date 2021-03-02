//$Id$
package com.dev.news.handler;

import org.json.JSONObject;

import com.dev.news.constants.ApiOperation;
import com.dev.news.data.Unit;
import com.dev.news.servlet.APIServlet;

public class UserHandler extends GenericHandler{
	private String method;
	public UserHandler(Unit en) {
		super(en);
	}

	@Override
	protected void modifyInputData(String method, JSONObject input) {
		this.method = method;
	}
	

	@Override
	protected void modifyOutputData(JSONObject data) {
		if(!method.equalsIgnoreCase(ApiOperation.GET) && !method.equalsIgnoreCase(ApiOperation.DELETE)) {
			JSONObject obj = data.getJSONObject("data");
			long token = obj.getLong("token_id");
			APIServlet.userCache.put(token, obj);
		} else if(method.equalsIgnoreCase(ApiOperation.DELETE)) {
			APIServlet.userCache.remove(APIServlet.tokenId.get());
		}
	}
}
