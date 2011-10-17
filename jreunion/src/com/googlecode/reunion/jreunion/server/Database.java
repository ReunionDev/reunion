package com.googlecode.reunion.jreunion.server;

import java.sql.Connection;
import java.sql.DriverManager;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.mysql.jdbc.Driver;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Database {

	public Connection dinamicConn = null; //reunion database
	
	public Connection staticConn = null; //reunionStatic database
	
	private PreparedStatement statement;

	public Database(Server server) {
		
	}

	public void connectDinamic() throws Exception {
		Parser dinamicDatabaseConfigParser = new Parser();
		dinamicDatabaseConfigParser.Parse("config/Database.dta");
		String[] requiredMembers = { "address", "database", "username",
				"password" };
		ParsedItem dinamicDatabaseConfig = dinamicDatabaseConfigParser.getItem("Database");

		if (dinamicDatabaseConfig == null
				|| !dinamicDatabaseConfig.checkMembers(requiredMembers)) {
			Logger.getLogger(Database.class).info("Error loading database config");
			return;
		}
		DatabaseUtils.getDinamicInstance().setDinamicDatabase(this); // link utils to
															// this database
		String userName = dinamicDatabaseConfig.getMemberValue("username");
		String password = dinamicDatabaseConfig.getMemberValue("password");
		String url = "jdbc:mysql://" + dinamicDatabaseConfig.getMemberValue("address")
				+ "/" + dinamicDatabaseConfig.getMemberValue("database")
				+ "?autoReconnect=true";
		Driver driver = (Driver)ClassFactory.create("com.mysql.jdbc.Driver");
		
		dinamicConn = DriverManager.getConnection(url, userName, password);
		Logger.getLogger(Database.class).info("Dinamic "+getClass().getSimpleName() + " connection established");

	}
	
	public void connectStatic() throws Exception {
		Parser staticDatabaseConfigParser = new Parser();
		staticDatabaseConfigParser.Parse("config/Database_static.dta");
		String[] requiredMembers = { "address", "database", "username",
				"password" };
		ParsedItem staticDatabaseConfig = staticDatabaseConfigParser.getItem("Database");

		if (staticDatabaseConfig == null
				|| !staticDatabaseConfig.checkMembers(requiredMembers)) {
			Logger.getLogger(Database.class).info("Error loading database config");
			return;
		}
		DatabaseUtils.getStaticInstance().setStaticDatabase(this); // link utils to
															// this database
		String userName = staticDatabaseConfig.getMemberValue("username");
		String password = staticDatabaseConfig.getMemberValue("password");
		String url = "jdbc:mysql://" + staticDatabaseConfig.getMemberValue("address")
				+ "/" + staticDatabaseConfig.getMemberValue("database")
				+ "?autoReconnect=true";
		Driver driver = (Driver)ClassFactory.create("com.mysql.jdbc.Driver");
		
		staticConn = DriverManager.getConnection(url, userName, password);
		Logger.getLogger(Database.class).info("Static "+getClass().getSimpleName() + " connection established");

	}

	public void start(){

		try {
			connectDinamic();
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
		
		try {
			connectStatic();
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
	
	}

	public void stop() {

		if (dinamicConn != null) {
			try {
				dinamicConn.close();
				Logger.getLogger(Database.class).info(getClass().getName()
						+ " connection terminated");
			} catch (Exception e) { /* ignore close errors */
			}
		}
		if (staticConn != null) {
			try {
				staticConn.close();
				Logger.getLogger(Database.class).info(getClass().getName()
						+ " connection terminated");
			} catch (Exception e) { /* ignore close errors */
			}
		}
	}

}
