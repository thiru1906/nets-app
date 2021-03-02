//$Id$
package com.dev.news.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;


public class DBUtil {
	static final Logger LOGGER = Logger.getLogger(DBUtil.class.toString());
	public static ResultSet addOrUpdate(PreparedStatement statement,Object... param) throws Exception{
		ResultSet rs = null;
		
		try {			
			Integer i = 0;
			for(i = 0 ; i < param.length; i++) {
				Object obj = param[i];
				
				if(obj.getClass() == Integer.class) {
					statement.setInt(i+1,(Integer) obj);
				} else if(obj.getClass() == Float.class){
					statement.setFloat(i+1, (Float)obj);
				} else if(obj.getClass() == String.class){
					statement.setString(i+1, (String)obj);
				} else if(obj.getClass() == Long.class){
					statement.setLong(i+1, (Long)obj);
				}else if(obj.getClass() == Boolean.class) {
					statement.setBoolean(i+1, (Boolean)obj);
				} else {
					statement.setString(i+1, obj.toString());
				}
			}
			return statement.executeQuery();
			
		}catch(Exception e) {
			throw e;
		}
	}
	
	public static ResultSet get(PreparedStatement statement) throws Exception{
		ResultSet rs = null;			
		rs = statement.executeQuery();
		return rs;
	}
	
	public static boolean delete(PreparedStatement statement) throws Exception {
		statement.executeUpdate();
		return true;
	}
}
