package com.googlecode.reunion.jreunion.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Random;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jreunion.events.Event;
import com.googlecode.reunion.jreunion.events.EventListener;
import com.googlecode.reunion.jreunion.events.client.ClientSendEvent;
import com.googlecode.reunion.jreunion.events.server.ServerEvent;
import com.googlecode.reunion.jreunion.events.server.ServerStartEvent;
import com.googlecode.reunion.jreunion.events.server.ServerStopEvent;
import com.mysql.jdbc.MySQLConnection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Database implements EventListener {

	public Connection conn = null;
	
	private PreparedStatement statement;

	public Database(Server server) {
		server.addEventListener(ServerEvent.class, this);
		
	}

	public void connect() throws Exception {
		Parser databaseConfigParser = new Parser();
		databaseConfigParser.Parse("config/Database.dta");
		String[] requiredMembers = { "address", "database", "username",
				"password" };
		ParsedItem databaseConfig = databaseConfigParser.getItem("Database");

		if (databaseConfig == null
				|| !databaseConfig.checkMembers(requiredMembers)) {
			Logger.getLogger(Database.class).info("Error loading database config");
			return;
		}
		DatabaseUtils.getInstance().setDatabase(this); // link utils to
															// this database
		String userName = databaseConfig.getMemberValue("username");
		String password = databaseConfig.getMemberValue("password");
		String url = "jdbc:mysql://" + databaseConfig.getMemberValue("address")
				+ "/" + databaseConfig.getMemberValue("database")
				+ "?autoReconnect=true";
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conn = DriverManager.getConnection(url, userName, password);
		Logger.getLogger(Database.class).info(getClass().getSimpleName() + " connection established");

	}

	public void start(){

		try {
			connect();
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
	
	}

	public void stop() {

		if (conn != null) {
			try {
				conn.close();
				Logger.getLogger(Database.class).info(getClass().getName()
						+ " connection terminated");
			} catch (Exception e) { /* ignore close errors */
			}
		}
	}

	@Override
	public void handleEvent(Event event) {
		Logger.getLogger(Database.class).info(event);
		 if(event instanceof ServerStartEvent){
			start();
		}else if(event instanceof ServerStopEvent){
			stop();
		}				
	}

}
