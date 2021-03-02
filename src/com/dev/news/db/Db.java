//$Id$
package com.dev.news.db;

import java.io.PrintWriter;
import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Db {
	
	private static DataSource source;
	
	
	public static void initDb() {
		setupDataSource();
	}
	 	  
	private static void setupDataSource() {
		Properties props = new Properties();
		props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
		props.setProperty("dataSource.user", "postgres");
		props.setProperty("dataSource.password", "");
		props.setProperty("dataSource.databaseName", "news");
		props.put("dataSource.logWriter", new PrintWriter(System.out));
		props.put("dataSource.portNumber","5432");
		props.put("dataSource.serverName","localhost");
		props.put("maximumPoolSize", 15);
		HikariConfig config = new HikariConfig(props);
		
		source = new HikariDataSource(config);
	}
	
	public static void destoryDb() {
		((HikariDataSource) source).close();
	}
	
	public static DataSource getSource() {
		return source;
	}
	
}
