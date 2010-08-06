package com.googlecode.reunion.jreunion.server;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_Database extends S_ClassModule {

	public Connection conn = null;

	public S_Database(S_Module parent) {

		super(parent);

	}

	public void Connect() throws Exception {
		S_Parser databaseConfigParser = new S_Parser();
		databaseConfigParser.Parse("databaseconfig.dta");
		String[] requiredMembers = { "address", "database", "username",
				"password" };
		S_ParsedItem databaseConfig = databaseConfigParser.getItem("Database");

		if (databaseConfig == null
				|| !databaseConfig.checkMembers(requiredMembers)) {
			System.out.println("Error loading database config");
			return;
		}
		S_DatabaseUtils.getInstance().setDatabase(this); // link utils to
															// this database
		String userName = databaseConfig.getMemberValue("username");
		String password = databaseConfig.getMemberValue("password");
		String url = "jdbc:mysql://" + databaseConfig.getMemberValue("address")
				+ "/" + databaseConfig.getMemberValue("database")
				+ "?autoReconnect=true";
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conn = DriverManager.getConnection(url, userName, password);
		System.out.println(getClass().getName() + " connection established");

	}

	@Override
	public void Start() throws Exception {

		Connect();

	}

	@Override
	public void Stop() {

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

	}

}
