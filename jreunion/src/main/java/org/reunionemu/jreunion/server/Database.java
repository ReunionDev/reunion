package org.reunionemu.jreunion.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jcommon.Parser;

import com.mysql.jdbc.Driver;

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
			LoggerFactory.getLogger(Database.class).info("Error loading database config");
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
		LoggerFactory.getLogger(Database.class).info("Dinamic "+getClass().getSimpleName() + " connection established");

	}

	public void start(){

		try {
			connectDinamic();
		} catch (Exception e) {
			LoggerFactory.getLogger(this.getClass()).warn("Exception",e);
		}
		
	}

	public void stop() {

		if (dinamicConn != null) {
			try {
				dinamicConn.close();
				LoggerFactory.getLogger(Database.class).info(getClass().getName()
						+ " connection terminated");
			} catch (Exception e) { /* ignore close errors */
			}
		}
		if (staticConn != null) {
			try {
				staticConn.close();
				LoggerFactory.getLogger(Database.class).info(getClass().getName()
						+ " connection terminated");
			} catch (Exception e) { /* ignore close errors */
			}
		}
	}

}
