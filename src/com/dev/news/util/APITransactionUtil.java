//$Id$
package com.dev.news.util;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.dev.news.data.Unit;
import com.dev.news.data.util.UnitUtil;
import com.dev.news.handler.Handler;
import com.dev.news.init.Loader;
import com.dev.news.servlet.APIServlet;

public class APITransactionUtil {

	public static void findAndUpdateUserInCache(long tokenId) {
		
		if(!APIServlet.userCache.containsKey(tokenId)) {
			findReaderByToken(tokenId);
			if(!APIServlet.userCache.containsKey(tokenId)) {
				findPublisherByToken(tokenId);
			}
		}		
	}
	
	
	//find reader from token_id
	public static JSONObject findReaderByToken(long tokenId) {
		
		if(APIServlet.userCache.containsKey(tokenId)) {
			return APIServlet.userCache.get(tokenId);
		}
		
		JSONObject rule = APIDataUtil.getCriteria("token_id", tokenId, "eq");
		
		Unit userUnit = UnitUtil.getUnit("user");
		
		Handler handler;
		try {
			handler = UnitUtil.getHandler(userUnit);
			JSONObject res = handler.get(rule, null);
			
			if(res.has("data")) {
				APIServlet.userCache.put(tokenId, res.getJSONObject("data"));
				return res.getJSONObject("data");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		return null;
	}
	
	
	
	
	//find publisher from token_id
	public static JSONObject findPublisherByToken(long tokenId) {
		
		if(APIServlet.userCache.containsKey(tokenId)) {
			return APIServlet.userCache.get(tokenId);
		}
		
		JSONObject rule = APIDataUtil.getCriteria("token_id", tokenId, "eq");
		
		Unit userUnit = UnitUtil.getUnit("publisher");
		
		Handler handler;
		try {
			handler = UnitUtil.getHandler(userUnit);
			JSONObject res = handler.get(rule, null);
			
			if(res.has("data")) {
				APIServlet.userCache.put(tokenId, res.getJSONObject("data"));
				return res.getJSONObject("data");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}	
	
	
	public static JSONObject getResponseObject(String status, long code) {
		JSONObject obj= new JSONObject();
		
		obj.put("status", status);
		obj.put("code", code);
		
		return new JSONObject().put("response", obj);
	}
	
	public static JSONObject getUnauthorizedResponse() {
		String status = "Unauthorized";
		int code = HttpServletResponse.SC_UNAUTHORIZED;
		return getResponseObject(status, code);
	}
	
	public static JSONObject getInvalidInputResponse() {
		String status = "Invalid input";
		int code = HttpServletResponse.SC_BAD_REQUEST;
		return getResponseObject(status, code);
	}
	
	public static JSONObject getServerErrorResponse() {
		String status = "Internal error";
		int code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		return getResponseObject(status, code);
	}
	
}
