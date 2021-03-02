//$Id$
package com.dev.news.filter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dev.news.constants.ApiOperation;
import com.dev.news.init.Loader;
import com.dev.news.servlet.APIServlet;
import com.dev.news.util.APITransactionUtil;

public class EntryFilter implements Filter{
	private static final Logger LOGGER = Logger.getLogger(EntryFilter.class.toString());
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		
		String uri = req.getRequestURL().toString();
		
		String[] splittedUri = uri.split("News/")[1].split("/");
		
		String apiPath = splittedUri[1];
		
		LOGGER.log(Level.INFO, "Path received is "+ uri);
		
		if(!Loader.unitPathMap.containsKey("/"+apiPath)) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendResponse(res, "failed", HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		//if the api is for user/publisher creation, allow it inside
		if((apiPath.equals("users") || apiPath.equals("authors")) && req.getMethod().equalsIgnoreCase(ApiOperation.ADD)) {
			chain.doFilter(request, res);
			return;
		}
		 
		//code to find user with the given token_id & storing it in cache..
		if(req.getParameter("token_id") == null) {
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			sendResponse(res, "failed- token id is null", HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		long tokenId =Long.parseLong(req.getParameter("token_id"));
		
		APITransactionUtil.findAndUpdateUserInCache(tokenId);
		
		if(!APIServlet.userCache.containsKey(tokenId)) {
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			sendResponse(res, "failed - db has no such user", HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		APIServlet.setTokenId(tokenId);
		
		LOGGER.log(Level.INFO, "User who just logged is " + APIServlet.getUserInfo());
		chain.doFilter(req, res);
		
	}
	
	private static void sendResponse(HttpServletResponse res,String status, int code) throws IOException {
		res.setContentType("application/json");
		res.getWriter().write(APITransactionUtil.getResponseObject(status, code).toString());
	}

}
