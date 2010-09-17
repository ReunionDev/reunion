package com.googlecode.reunion.jreunion.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Random;

import com.googlecode.reunion.jcommon.S_ParsedItem;
import com.googlecode.reunion.jcommon.S_Parser;
import com.mysql.jdbc.MySQLConnection;
import java.sql.PreparedStatement;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Database extends ClassModule {

	public Connection conn = null;
	
	private PreparedStatement statement;

	public Database(Module parent) {

		super(parent);

	}

	public void connect() throws Exception {
		S_Parser databaseConfigParser = new S_Parser();
		databaseConfigParser.Parse("config/Database.dta");
		String[] requiredMembers = { "address", "database", "username",
				"password" };
		S_ParsedItem databaseConfig = databaseConfigParser.getItem("Database");

		if (databaseConfig == null
				|| !databaseConfig.checkMembers(requiredMembers)) {
			System.out.println("Error loading database config");
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
		System.out.println(getClass().getSimpleName() + " connection established");

	}

	@Override
	public void start() throws Exception {

		connect();
	
	}

	@Override
	public void stop() {

		if (conn != null) {
			try {
				conn.close();
				System.out.println(getClass().getName()
						+ " connection terminated");
			} catch (Exception e) { /* ignore close errors */
			}
		}
	}

	@Override
	public void Work() {
		DatabaseUtils.getInstance().work();
	}

}
