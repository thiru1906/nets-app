//$Id$
package com.dev.news.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.dev.news.db.Db;
import com.dev.news.handler.Handler;

public class APIServlet extends HttpServlet{
	static final Logger LOGGER = Logger.getLogger(APIServlet.class.toString());
	private static ThreadLocal<Connection> connection = new ThreadLocal<>();
	public static Map<Long, JSONObject> userCache = new HashMap<>();
	public static ThreadLocal<Long> tokenId = new ThreadLocal<>();
	
	private static Connection getConnection() {
		return connection.get();
	}
	
	public static JSONObject getUserInfo() {
		return userCache.get(tokenId.get());
	}
	
	public static void setTokenId(long token) {
		tokenId.set(token);
	}
	
	private static void createConnection() throws Exception {
		if(connection.get() == null) {
			connection.set(Db.getSource().getConnection());
		}
	}
	
	public static PreparedStatement getStatement(String query) {
		try {
			createConnection();
			return getConnection().prepareStatement(query);
		} catch (Exception e1) {
			LOGGER.log(Level.SEVERE, e1.getMessage(), e1);
		}

		return null;
	}
	
	public static void closeConnection() {
		if(connection.get() != null) {
			try {
				connection.get().close();
				connection.remove();
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3672604105231439511L;
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			JSONObject obj = Handler.operationEntry(req, resp);
			resp.setContentType("json/application");
			resp.getWriter().write(obj.toString());
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
}
