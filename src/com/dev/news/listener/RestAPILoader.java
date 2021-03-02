//$Id$
package com.dev.news.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.dev.news.init.Loader;

public class RestAPILoader implements ServletContextListener{
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		Loader.init();
	}
	
}
