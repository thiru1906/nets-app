//$Id$
package com.dev.news.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.dev.news.db.Db;

public class DBListener implements ServletContextListener{

	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		sce.getServletContext().getContextPath();
		Db.initDb();
	}
	
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Db.destoryDb();
	}
}
